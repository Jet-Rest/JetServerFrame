package cn.codetector.jet.message.messengers

import cn.codetector.jet.message.MessageRecipient
import cn.codetector.yiling.server.message.MessageTemplate


interface IMessenger {
    fun canSendMessage(recipient: MessageRecipient, message: MessageTemplate): Boolean
    fun sendMessage(recipient: MessageRecipient, message: MessageTemplate)
    fun messageCost(recipient: MessageRecipient, message: MessageTemplate): Double
}