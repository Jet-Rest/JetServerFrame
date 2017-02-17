package cn.codetector.jet.message

import cn.codetector.util.Configuration.ConfigurationManager
import cn.codetector.yiling.server.message.MessageTemplate
import com.google.common.io.Resources
import io.vertx.core.Vertx
import io.vertx.ext.mail.MailClient
import io.vertx.ext.mail.StartTLSOptions
import io.vertx.ext.mail.MailConfig
import io.vertx.ext.mail.MailMessage
import java.util.concurrent.CountDownLatch


/**
 * Created by Codetector on 2017/2/12.
 */
class MessageService(val sharedVertx: Vertx) {

    private val mConfig = ConfigurationManager.getConfiguration("smtp.config.json")
    val config = MailConfig()
    val mailClient: MailClient
    init {
        config.hostname = mConfig.getStringValue("hostname","localhost")
        config.port = mConfig.getIntegerValue("port", 587)
        config.starttls = if (mConfig.getBooleanValue("starttls", false)) StartTLSOptions.REQUIRED else StartTLSOptions.OPTIONAL
        config.isSsl = mConfig.getBooleanValue("ssl", true)
        config.username = mConfig.getStringValue("username","user")
        config.password = mConfig.getStringValue("password","password")
        mailClient = MailClient.createNonShared(sharedVertx, config)
    }

    fun getTemplate(templateName: String, title: String) : MessageTemplate {
        return MessageTemplate(Resources.toString(this.javaClass.getResource("/mail/${templateName}.tmp"), Charsets.UTF_8), title)
    }

    fun sendMessage(recipient: String, message: MessageTemplate): Boolean {
        val latch = CountDownLatch(1)
        var sendResult = true
        val msg = MailMessage()
        msg.from = config.username
        msg.setTo(recipient)
        msg.subject = message.compileTitle()
        msg.html = message.compileContent()
        mailClient.sendMail(msg, { result ->
            sendResult = result.succeeded()
            latch.countDown()
        })
        latch.await()
        return sendResult
    }
}