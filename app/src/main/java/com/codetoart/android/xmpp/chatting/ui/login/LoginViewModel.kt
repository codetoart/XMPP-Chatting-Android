package com.codetoart.android.xmpp.chatting.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codetoart.android.xmpp.chatting.SmackConnection
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration

class LoginViewModel(
    private val smackConnection: SmackConnection
) : ViewModel() {

    companion object {
        val LOG_TAG: String = LoginViewModel::class.java.simpleName
    }

    private var loginDisposable: Disposable? = null
    val liveLoginState = MutableLiveData<LoginState>()

    fun attemptLogin(config: XMPPTCPConnectionConfiguration) {
        Log.v(LOG_TAG, "-> attemptLogin")

        liveLoginState.value = LoginState(LoginState.Status.AUTHENTICATING)

        loginDisposable = smackConnection.attemptLogin(config)
            .subscribeOn(Schedulers.io())
            .subscribe({
                Log.v(LOG_TAG, "-> attemptLogin -> onSuccess")
                liveLoginState.postValue(LoginState(LoginState.Status.AUTHENTICATED))
            }, { t ->
                Log.e(LOG_TAG, "-> attemptLogin -> onError ->", t)
                liveLoginState.postValue(LoginState(LoginState.Status.FAILED, t.message))
            })
    }

    override fun onCleared() {
        super.onCleared()
        Log.v(LOG_TAG, "-> onCleared")

        loginDisposable?.dispose()
    }
}

class LoginState(
    val status: Status,
    val message: String? = null
) {
    enum class Status {
        AUTHENTICATING,
        AUTHENTICATED,
        FAILED,
        UNKNOWN
    }
}
