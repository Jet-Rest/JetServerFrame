package cn.codetector.jet.message

data class MessageRecipient (val recipient: String, val type: MessageRecipientType)
enum class MessageRecipientType(val value: String) {
    EMAIL("Email"),
    TEXT("Text")
}