package cn.codetector.jet

import cn.codetector.jet.console.consoleManager.ConsoleManager
import cn.codetector.jet.data.DataService
import cn.codetector.util.Configuration.ConfigurationManager
import cn.codetector.yiling.server.data.services.configuration.DatabaseConfiguration
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.jdbc.JDBCClient
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by codetector on 17/02/2017.
 */
class Jet {
    val rootLogger = LoggerFactory.getLogger("Server Root")
    val globalConfig = ConfigurationManager.getConfiguration("mainConfig.json")
    val sharedVertx: Vertx = Vertx.vertx(VertxOptions().setWorkerPoolSize(globalConfig.getIntegerValue("workerPoolSize", 32)))
    val sharedJDBCClient: JDBCClient = JDBCClient.createShared(sharedVertx, DatabaseConfiguration.getVertXJDBCConfigObject())
    val dataService = DataService(sharedJDBCClient)

    fun startServer () {
        try {
            dataService.start()
//            MessageService.init()
//            WebService.initService(sharedVertx, sharedJDBCClient) //Init Web API Services
        } catch (t: Throwable) {
            rootLogger.error(t)
            t.printStackTrace()
        }
        ConsoleManager.monitorStream("ConsoleIn", System.`in`)

//        val template = MessageService.getTemplate("server_start", "服务器启动啦！！！")
//        val format1 = SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSS")
//        template.setValue("time",format1.format(Date()))
//        MessageService.sendMessage("server@codetector.cn", template)
    }
}