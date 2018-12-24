package com.codetoart.android.xmpp.chatting.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.codetoart.android.xmpp.chatting.R

class ProfileActivity : AppCompatActivity() {

    companion object {
        val LOG_TAG: String = ProfileActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(LOG_TAG, "-> onCreate")

        setContentView(R.layout.profile_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ProfileFragment.newInstance())
                .commitNow()
        }
    }

}
