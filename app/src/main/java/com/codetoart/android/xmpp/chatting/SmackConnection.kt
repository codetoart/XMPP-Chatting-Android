package com.codetoart.android.xmpp.chatting

import android.util.Log
import io.reactivex.Single
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import java.io.Serializable
import java.net.InetAddress

class SmackConnection : Serializable {

    companion object {
        val LOG_TAG: String = SmackConnection::class.java.simpleName

        @Volatile
        private var singleton: SmackConnection? = null

        fun get(): SmackConnection =
            singleton ?: synchronized(this) {
                singleton ?: SmackConnection().also { singleton = it }
            }
    }

    lateinit var connection: AbstractXMPPConnection

    fun attemptLogin(configBuilder: XMPPTCPConnectionConfiguration.Builder): Single<AbstractXMPPConnection> {
        Log.v(LOG_TAG, "-> attemptLogin")

        return Single.create<AbstractXMPPConnection> { source ->

            val hostAddress: InetAddress = InetAddress.getByName(BuildConfig.XMPP_SERVER_HOST)
            val configuration = configBuilder.setHostAddress(hostAddress).build()
            val connection = XMPPTCPConnection(configuration)

            // TODO -> Add try catch block
            connection.connect()
            if (connection.isConnected) {
                Log.v(LOG_TAG, "-> attemptLogin -> connected")
            } else {
                source.onError(Throwable("Unable to connect"))
            }

            connection.login()
            if (connection.isAuthenticated) {
                Log.v(LOG_TAG, "-> attemptLogin -> ${configuration.username} authenticated")
            } else {
                source.onError(Throwable("Unable to login"))
            }

            this.connection = connection

            val roster = Roster.getInstanceFor(connection)
            roster.subscriptionMode = Roster.SubscriptionMode.accept_all

            roster.entries.forEach {
                Log.d(LOG_TAG, "-> attemptLogin -> entry -> $it")
            }

            source.onSuccess(connection)
        }
    }

    fun attemptLogOff() {
        Log.v(LOG_TAG, "-> attemptLogOff")

        AppExecutors.get().networkExecutor.execute {
            connection.disconnect()
        }
    }
}