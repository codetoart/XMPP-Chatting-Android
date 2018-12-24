package com.codetoart.android.xmpp.chatting.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.codetoart.android.xmpp.chatting.R

class ChatActivity : AppCompatActivity() {

    companion object {
        val LOG_TAG: String = ChatActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(LOG_TAG, "-> onCreate")

        setContentView(R.layout.chat_activity)

        val senderUsername: String = intent.getStringExtra(ChatFragment.BUNDLE_SENDER_USERNAME)
        val receiverUsername: String = intent.getStringExtra(ChatFragment.BUNDLE_RECEIVER_USERNAME)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ChatFragment.newInstance(senderUsername, receiverUsername))
                .commitNow()
        }
    }

}
