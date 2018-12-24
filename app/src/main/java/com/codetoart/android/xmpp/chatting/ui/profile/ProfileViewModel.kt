package com.codetoart.android.xmpp.chatting.ui.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codetoart.android.xmpp.chatting.SmackConnection

class ProfileViewModel(
    private val smackConnection: SmackConnection
) : ViewModel() {

    companion object {
        val LOG_TAG: String = ProfileViewModel::class.java.simpleName
    }

    val liveUsername = MutableLiveData<String>()

    fun init() {
        Log.v(LOG_TAG, "-> init")

        val username = smackConnection.connection.configuration.username.toString()
        liveUsername.value = username
    }

    override fun onCleared() {
        super.onCleared()
        Log.v(LOG_TAG, "-> onCleared")

        smackConnection.attemptLogOff()
    }
}
