package com.codetoart.android.xmpp.chatting

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex

class MainApplication : Application() {

    companion object {
        val LOG_TAG: String = MainApplication::class.java.simpleName
        private var mainApplication: MainApplication? = null

        fun set(mainApplication: MainApplication) {
            this.mainApplication = mainApplication
        }

        fun get(): MainApplication {
            return mainApplication ?: throw Exception("MainApplication.set() not called")
        }
    }

    private lateinit var appExecutors: AppExecutors
    private lateinit var smackConnection: SmackConnection

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Log.v(LOG_TAG, "-> onCreate")

        set(this)

        appExecutors = AppExecutors.get()
        smackConnection = SmackConnection.get()
    }
}