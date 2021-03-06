package com.codetoart.android.xmpp.chatting.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.codetoart.android.xmpp.chatting.AppConstants
import com.codetoart.android.xmpp.chatting.R
import com.codetoart.android.xmpp.chatting.SmackConnection
import com.codetoart.android.xmpp.chatting.ui.chat.ChatActivity
import com.codetoart.android.xmpp.chatting.ui.chat.ChatFragment
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileFragment : Fragment() {

    companion object {
        val LOG_TAG: String = ProfileFragment::class.java.simpleName
        fun newInstance() = ProfileFragment()
    }

    private lateinit var smackConnection: SmackConnection
    private lateinit var viewModel: ProfileViewModel

    private val chatWithClickListener = View.OnClickListener { view ->
        Log.v(LOG_TAG, "-> onClick -> chatWith")

        when (view.id) {
            R.id.buttonChatAdmin -> startActivityFor(AppConstants.adminUsername)
            R.id.buttonChatBob -> startActivityFor(AppConstants.bobUsername)
            R.id.buttonChatAndrew -> startActivityFor(AppConstants.andrewUsername)
            R.id.buttonChatJohn -> startActivityFor(AppConstants.johnUsername)
            R.id.buttonChatMax -> startActivityFor(AppConstants.maxUsername)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(LOG_TAG, "-> onActivityCreated")

        smackConnection = SmackConnection.get()
        viewModel = getViewModel(smackConnection)

        viewModel.liveUsername.observe(this, Observer {
            textViewGreeting.text = getString(R.string.greeting, it)
            disableButtonChatWith(it)
        })

        buttonChatAdmin.setOnClickListener(chatWithClickListener)
        buttonChatBob.setOnClickListener(chatWithClickListener)
        buttonChatAndrew.setOnClickListener(chatWithClickListener)
        buttonChatJohn.setOnClickListener(chatWithClickListener)
        buttonChatMax.setOnClickListener(chatWithClickListener)

        if (savedInstanceState == null)
            viewModel.init()
    }

    private fun startActivityFor(receiverUsername: String) {
        Log.v(LOG_TAG, "-> startActivityFor")

        val senderUsername = smackConnection.connection.configuration.username.toString()
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra(ChatFragment.BUNDLE_SENDER_USERNAME, senderUsername)
        intent.putExtra(ChatFragment.BUNDLE_RECEIVER_USERNAME, receiverUsername)
        startActivity(intent)
    }

    private fun disableButtonChatWith(username: String) {
        Log.v(LOG_TAG, "-> disableButtonChatWith")

        when (username) {
            AppConstants.adminUsername -> buttonChatAdmin.isEnabled = false
            AppConstants.bobUsername -> buttonChatBob.isEnabled = false
            AppConstants.andrewUsername -> buttonChatAndrew.isEnabled = false
            AppConstants.johnUsername -> buttonChatJohn.isEnabled = false
            AppConstants.maxUsername -> buttonChatMax.isEnabled = false
        }
    }

    private fun getViewModel(smackConnection: SmackConnection): ProfileViewModel {
        Log.v(LOG_TAG, "-> getViewModel")

        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(smackConnection) as T
            }
        }).get(ProfileViewModel::class.java)
    }
}
