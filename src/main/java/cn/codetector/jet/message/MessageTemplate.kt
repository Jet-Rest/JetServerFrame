package cn.codetector.yiling.server.message

/**
 * Created by Codetector on 2017/2/12.
 */
class MessageTemplate(templateContent: String, title: String) {
    private var currentTemplate: String = templateContent
    private var currentTitle: String = title
    fun compileContent(): String {
        return currentTemplate
    }
    fun compileTitle(): String {
        return this.currentTitle
    }
    fun setValue(key: String, value: Any) {
        this.currentTemplate = this.currentTemplate.replace("{{${key}}}",value.toString())
        this.currentTitle = this.currentTitle.replace("{{${key}}}",value.toString())
    }
}