package com.codetoart.android.xmpp.chatting.ui.login

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
import com.codetoart.android.xmpp.chatting.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : Fragment() {

    companion object {
        val LOG_TAG: String = LoginActivity::class.java.simpleName
        fun newInstance() = LoginFragment()
    }

    private lateinit var smackConnection: SmackConnection
    private lateinit var viewModel: LoginViewModel

    private val userButtonClickListener = View.OnClickListener { view ->

        when (view.id) {
            R.id.buttonAdmin -> viewModel.attemptLogin(AppConstants.adminConfig)
            R.id.buttonBob -> viewModel.attemptLogin(AppConstants.bobConfig)
            R.id.buttonAndrew -> viewModel.attemptLogin(AppConstants.andrewConfig)
            R.id.buttonJohn -> viewModel.attemptLogin(AppConstants.johnConfig)
            R.id.buttonMax -> viewModel.attemptLogin(AppConstants.maxConfig)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(LOG_TAG, "-> onActivityCreated")

        smackConnection = SmackConnection.get()
        viewModel = getViewModel(smackConnection)

        viewModel.liveLoginState.observe(this, Observer {
            when (it.status) {
                LoginState.Status.AUTHENTICATED -> {
                    Log.v(LOG_TAG, "-> liveLoginState Observer -> ${it.status}")
                    val intent = Intent(context, ProfileActivity::class.java)
                    startActivity(intent)
                }
                LoginState.Status.AUTHENTICATING -> {
                    Log.v(LOG_TAG, "-> liveLoginState Observer -> ${it.status}")
                }
                LoginState.Status.FAILED -> {
                    Log.e(LOG_TAG, "-> liveLoginState Observer -> ${it.status}")
                }
                else -> {
                    Log.v(LOG_TAG, "-> liveLoginState Observer -> ${it.status}")
                }
            }
        })

        buttonAdmin.setOnClickListener(userButtonClickListener)
        buttonBob.setOnClickListener(userButtonClickListener)
        buttonAndrew.setOnClickListener(userButtonClickListener)
        buttonJohn.setOnClickListener(userButtonClickListener)
        buttonMax.setOnClickListener(userButtonClickListener)
    }

    private fun getViewModel(smackConnection: SmackConnection): LoginViewModel {
        Log.v(LOG_TAG, "-> getViewModel")

        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(smackConnection) as T
            }
        }).get(LoginViewModel::class.java)
    }
}
