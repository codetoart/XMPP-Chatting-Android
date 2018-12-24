package com.codetoart.android.xmpp.chatting

import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.DomainBareJid
import org.jxmpp.jid.impl.JidCreate
import java.net.InetAddress

object AppConstants {

    val hostAddress: InetAddress = InetAddress.getByName(BuildConfig.XMPP_SERVER_HOST)
    const val port = 5222
    val xmppServiceDomain: DomainBareJid = JidCreate.domainBareFrom("localhost")

    val adminConfig: XMPPTCPConnectionConfiguration = XMPPTCPConnectionConfiguration.builder()
        .setHostAddress(hostAddress)
        .setPort(port)
        .setUsernameAndPassword("admin", "password")
        .setXmppDomain(xmppServiceDomain)
        .build()

    val bobConfig: XMPPTCPConnectionConfiguration = XMPPTCPConnectionConfiguration.builder()
        .setHostAddress(hostAddress)
        .setPort(port)
        .setUsernameAndPassword("bob", "password")
        .setXmppDomain(xmppServiceDomain)
        .build()

    val andrewConfig: XMPPTCPConnectionConfiguration = XMPPTCPConnectionConfiguration.builder()
        .setHostAddress(hostAddress)
        .setPort(port)
        .setUsernameAndPassword("andrew", "password")
        .setXmppDomain(xmppServiceDomain)
        .build()

    val johnConfig: XMPPTCPConnectionConfiguration = XMPPTCPConnectionConfiguration.builder()
        .setHostAddress(hostAddress)
        .setPort(port)
        .setUsernameAndPassword("john", "password")
        .setXmppDomain(xmppServiceDomain)
        .build()

    val maxConfig: XMPPTCPConnectionConfiguration = XMPPTCPConnectionConfiguration.builder()
        .setHostAddress(hostAddress)
        .setPort(port)
        .setUsernameAndPassword("max", "password")
        .setXmppDomain(xmppServiceDomain)
        .build()
}
