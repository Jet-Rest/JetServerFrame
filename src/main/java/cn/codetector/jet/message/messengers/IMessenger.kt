package cn.codetector.jet.message.messengers

import cn.codetector.yiling.server.message.MessageTemplate

interface IMessenger {
    fun sendMessage(recipient: String, message: MessageTemplate)
}