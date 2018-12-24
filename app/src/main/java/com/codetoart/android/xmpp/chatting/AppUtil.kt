package com.codetoart.android.xmpp.chatting

import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.BareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Localpart

class AppUtil {

    companion object {

        fun getBareJid(configuration: XMPPTCPConnectionConfiguration): BareJid {

            val localpart = Localpart.from(configuration.username.toString())
            val domainBareJid = configuration.xmppServiceDomain
            return JidCreate.bareFrom(localpart, domainBareJid)
        }

        fun getByUsername(username: String): XMPPTCPConnectionConfiguration {
            return when (username) {
                AppConstants.adminConfig.username -> AppConstants.adminConfig
                AppConstants.bobConfig.username -> AppConstants.bobConfig
                AppConstants.andrewConfig.username -> AppConstants.andrewConfig
                AppConstants.johnConfig.username -> AppConstants.johnConfig
                AppConstants.maxConfig.username -> AppConstants.maxConfig
                else -> throw Exception("-> Unknown username: $username")
            }
        }
    }
}