package com.codetoart.android.xmpp.chatting

import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.DomainBareJid
import org.jxmpp.jid.impl.JidCreate

object AppConstants {

    const val port = 5222
    val xmppServiceDomain: DomainBareJid = JidCreate.domainBareFrom(BuildConfig.XMPP_SERVICE_DOMAIN)

    fun getBaseConfig(): XMPPTCPConnectionConfiguration.Builder {
        return XMPPTCPConnectionConfiguration.builder()
            .setPort(port)
            .setXmppDomain(xmppServiceDomain)
    }

    const val adminUsername = "admin"
    const val bobUsername = "bob"
    const val andrewUsername = "andrew"
    const val johnUsername = "john"
    const val maxUsername = "max"

    val adminConfig: XMPPTCPConnectionConfiguration.Builder =
        getBaseConfig().setUsernameAndPassword(adminUsername, "password")

    val bobConfig: XMPPTCPConnectionConfiguration.Builder =
        getBaseConfig().setUsernameAndPassword(bobUsername, "password")

    val andrewConfig: XMPPTCPConnectionConfiguration.Builder =
        getBaseConfig().setUsernameAndPassword(andrewUsername, "password")

    val johnConfig: XMPPTCPConnectionConfiguration.Builder =
        getBaseConfig().setUsernameAndPassword(johnUsername, "password")

    val maxConfig: XMPPTCPConnectionConfiguration.Builder =
        getBaseConfig().setUsernameAndPassword(maxUsername, "password")
}
