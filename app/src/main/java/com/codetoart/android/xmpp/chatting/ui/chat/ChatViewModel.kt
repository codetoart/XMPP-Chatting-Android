package com.codetoart.android.xmpp.chatting.ui.chat

import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.codetoart.android.xmpp.chatting.AppConstants
import com.codetoart.android.xmpp.chatting.MainApplication
import com.codetoart.android.xmpp.chatting.R
import com.codetoart.android.xmpp.chatting.SmackConnection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterListener
import org.jivesoftware.smackx.chatstates.ChatState
import org.jivesoftware.smackx.chatstates.ChatStateListener
import org.jivesoftware.smackx.chatstates.ChatStateManager
import org.jivesoftware.smackx.mam.MamManager
import org.jxmpp.jid.BareJid
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Localpart


class ChatViewModel(
    private val app: MainApplication,
    private val smackConnection: SmackConnection,
    val senderUsername: String,
    val receiverUsername: String
) : AndroidViewModel(app) {

    companion object {
        val LOG_TAG: String = ChatViewModel::class.java.simpleName
    }

    private lateinit var roster: Roster
    private lateinit var receiverBareJid: BareJid
    private lateinit var chatManager: ChatManager
    lateinit var chat: Chat
    private lateinit var mamManager: MamManager
    private lateinit var chatStateManager: ChatStateManager

    val livePresenceType = MutableLiveData<String>()
    val livePresenceMode = MutableLiveData<String>()
    val liveNoOfMessages = MutableLiveData<Int>()
    val liveChatState = MutableLiveData<ChatState>()

    var listOfMessages = mutableListOf<String>()
    private var disposableMessages: Disposable? = null
    private val chatStateHandler = Handler(app.mainLooper)

    private val chatStatePausedRunnable = Runnable {
        chatStateManager.setCurrentState(ChatState.active, chat)
    }

    private val rosterListener = object : RosterListener {
        override fun entriesDeleted(addresses: MutableCollection<Jid>?) {

        }

        override fun presenceChanged(presence: Presence) {

            val bestPresence = roster.getPresence(receiverBareJid)
            Log.v(
                LOG_TAG, "-> presenceChanged -> Type: ${presence.type}, Mode: ${presence.mode}" +
                        " but bestPresence Type: ${presence.type}, Mode: ${presence.mode}"
            )
            livePresenceType.postValue(bestPresence.type.name)
            livePresenceMode.postValue(bestPresence.mode.name)
        }

        override fun entriesUpdated(addresses: MutableCollection<Jid>?) {

        }

        override fun entriesAdded(addresses: MutableCollection<Jid>?) {

        }
    }

    private val incomingChatMessageListener = IncomingChatMessageListener { from, message, chat ->

        if (from.asBareJid() == receiverBareJid) {
            Log.v(LOG_TAG, "-> newIncomingMessage")
            val appendMessage = "\n\n${from.localpart}: ${message.body}"
            listOfMessages.add(appendMessage)
            liveNoOfMessages.postValue(listOfMessages.lastIndex)
        }
    }

    private val chatStateListener = ChatStateListener { chat, state, message ->

        if (chat.xmppAddressOfChatPartner.asBareJid() == receiverBareJid) {
            Log.v(LOG_TAG, "-> stateChanged")
            liveChatState.postValue(state)
        }
    }

    fun init() {
        Log.v(LOG_TAG, "-> init")

        initRoster()
        initChat()
        initMam()
    }

    private fun initRoster() {
        Log.v(LOG_TAG, "-> initRoster")

        receiverBareJid = JidCreate.bareFrom(Localpart.from(receiverUsername), AppConstants.xmppServiceDomain)
        roster = Roster.getInstanceFor(smackConnection.connection)
        roster.addRosterListener(rosterListener)

        roster.createEntry(receiverBareJid, receiverUsername, null)
        val bestPresence = roster.getPresence(receiverBareJid)
        livePresenceType.value = bestPresence.type.name
        livePresenceMode.value = bestPresence.mode.name
    }

    private fun initChat() {
        Log.v(LOG_TAG, "-> initChat")

        listOfMessages.add(app.applicationContext.getString(R.string.chatList))
        liveNoOfMessages.value = listOfMessages.lastIndex
        chatManager = ChatManager.getInstanceFor(smackConnection.connection)
        chatManager.addIncomingListener(incomingChatMessageListener)
        chat = chatManager.chatWith(receiverBareJid as EntityBareJid)

        chatStateManager = ChatStateManager.getInstance(smackConnection.connection)
        chatStateManager.addChatStateListener(chatStateListener)
        chatStateManager.setCurrentState(ChatState.active, chat)

        // TODO -> Try to get best chat state available at start
    }

    fun setChatStateComposing(chat: Chat) {
        //Log.v(LOG_TAG, "-> setChatStateComposing")

        chatStateHandler.removeCallbacks(chatStatePausedRunnable)
        chatStateManager.setCurrentState(ChatState.composing, chat)
        chatStateHandler.postDelayed(chatStatePausedRunnable, 5000)
    }

    private fun initMam() {
        Log.v(LOG_TAG, "-> initMam")

        mamManager = MamManager.getInstanceFor(smackConnection.connection)
        if (!mamManager.isSupported) {
            Log.e(LOG_TAG, "-> init -> MAM is not supported")
            return
        }
        mamManager.enableMamForAllMessages()
        val mamQueryArgs = MamManager.MamQueryArgs.builder()
            .limitResultsToJid(receiverBareJid)
            .setResultPageSize(10)
            .queryLastPage()
            .build()

        disposableMessages = getObservableMessages(mamQueryArgs, 10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ listOfMessages ->
                val tempList = mutableListOf<String>()
                listOfMessages.forEach { message ->
                    val appendMessage = "\n\n${message.from.localpartOrNull}: ${message.body}"
                    tempList.add(appendMessage)
                }
                this.listOfMessages.addAll(1, tempList)
                liveNoOfMessages.value = this.listOfMessages.lastIndex
            }, { t ->
                Log.e(LOG_TAG, "-> initMam -> onError ->", t)
            })
    }

    private fun getObservableMessages(
        mamQueryArgs: MamManager.MamQueryArgs,
        pageSize: Int
    ): Observable<List<Message>> {

        return Observable.create<List<Message>> { source ->
            try {
                val mamQuery = mamManager.queryArchive(mamQueryArgs)
                source.onNext(mamQuery.messages)
                while (!mamQuery.isComplete) {
                    source.onNext(mamQuery.pagePrevious(pageSize))
                }
            } catch (e: Exception) {
                source.onError(e)
            }
            source.onComplete()
        }
    }

    fun sendMessage(messageBody: String) {
        Log.v(LOG_TAG, "-> sendMessage")

        chat.send(messageBody)
        val appendMessage = "\n\n$senderUsername: $messageBody"
        listOfMessages.add(appendMessage)
        liveNoOfMessages.value = listOfMessages.lastIndex
    }

    override fun onCleared() {
        super.onCleared()
        Log.v(LOG_TAG, "-> onCleared")

        roster.removeRosterListener(rosterListener)
        chatManager.removeIncomingListener(incomingChatMessageListener)

        chatStateHandler.removeCallbacks(chatStatePausedRunnable)
        chatStateManager.setCurrentState(ChatState.gone, chat)
        chatStateManager.removeChatStateListener(chatStateListener)

        disposableMessages?.dispose()
    }
}
