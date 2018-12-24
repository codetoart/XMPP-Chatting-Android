package com.codetoart.android.xmpp.chatting.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.codetoart.android.xmpp.chatting.MainApplication
import com.codetoart.android.xmpp.chatting.R
import com.codetoart.android.xmpp.chatting.SmackConnection
import kotlinx.android.synthetic.main.chat_fragment.*

class ChatFragment : Fragment() {

    companion object {

        val LOG_TAG: String = ChatFragment::class.java.simpleName
        const val BUNDLE_SENDER_USERNAME = "BUNDLE_SENDER_USERNAME"
        const val BUNDLE_RECEIVER_USERNAME = "BUNDLE_RECEIVER_USERNAME"

        fun newInstance(senderUsername: String, receiverUsername: String): ChatFragment {

            val chatFragment = ChatFragment()
            val bundle = Bundle()
            bundle.putString(BUNDLE_SENDER_USERNAME, senderUsername)
            bundle.putString(BUNDLE_RECEIVER_USERNAME, receiverUsername)
            chatFragment.arguments = bundle
            return chatFragment
        }
    }

    private lateinit var smackConnection: SmackConnection
    private lateinit var senderUsername: String
    private lateinit var receiverUsername: String
    private lateinit var viewModel: ChatViewModel

    private val onClickSendListener = View.OnClickListener { view ->
        val button = view as Button
        Log.v(LOG_TAG, "-> onClick -> ${button.text}")

        val messageBody = editTextMessage.text.toString()
        if (messageBody.isNotEmpty()) {
            editTextMessage.text.clear()
            viewModel.sendMessage(messageBody)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(LOG_TAG, "-> onActivityCreated")

        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        Log.v(LOG_TAG, "-> init")

        smackConnection = SmackConnection.get()
        senderUsername = arguments!!.getString(BUNDLE_SENDER_USERNAME)!!
        receiverUsername = arguments!!.getString(BUNDLE_RECEIVER_USERNAME)!!

        viewModel = getViewModel(MainApplication.get(), smackConnection, senderUsername, receiverUsername)

        textViewTitle.text = getString(R.string.chatting_with, senderUsername, receiverUsername)

        viewModel.livePresenceType.observe(this, Observer {
            textViewPresenceType.text = it
        })
        viewModel.livePresenceMode.observe(this, Observer {
            textViewPresenceMode.text = it
        })

        viewModel.liveNoOfMessages.observe(this, Observer {
            var text = ""
            viewModel.listOfMessages.forEach { appendMessage ->
                text += appendMessage
            }
            textViewChatList.text = text
        })

        viewModel.liveChatState.observe(this, Observer {
            textViewChatState.text = it.name
        })

        editTextMessage.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Log.v(LOG_TAG, "-> onTextChanged")
                viewModel.setChatStateComposing(viewModel.chat)
            }
        })

        buttonSend.setOnClickListener(onClickSendListener)

        if (savedInstanceState == null)
            viewModel.init()
    }

    private fun getViewModel(
        app: MainApplication,
        smackConnection: SmackConnection,
        senderUsername: String,
        receiverUsername: String
    ): ChatViewModel {
        Log.v(LOG_TAG, "-> getViewModel")

        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ChatViewModel(app, smackConnection, senderUsername, receiverUsername) as T
            }
        }).get(ChatViewModel::class.java)
    }
}
