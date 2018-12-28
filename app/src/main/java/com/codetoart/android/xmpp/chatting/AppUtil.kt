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
    }
}