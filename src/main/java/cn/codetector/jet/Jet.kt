package cn.codetector.jet

import cn.codetector.jet.console.consoleManager.ConsoleManager
import cn.codetector.jet.data.DataService
import cn.codetector.jet.message.MessageService
import cn.codetector.jet.webService.WebService
import cn.codetector.util.Configuration.ConfigurationManager
import cn.codetector.yiling.server.data.services.configuration.DatabaseConfiguration
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.jdbc.JDBCClient

/**
 * Created by codetector on 17/02/2017.
 */
class Jet {
    val rootLogger = LoggerFactory.getLogger("Server Root")
    val globalConfig = ConfigurationManager.getConfiguration("mainConfig.json")
    val sharedVertx: Vertx = Vertx.vertx(VertxOptions().setWorkerPoolSize(globalConfig.getIntegerValue("workerPoolSize", 32)))
    val sharedJDBCClient: JDBCClient = JDBCClient.createShared(sharedVertx, DatabaseConfiguration.getVertXJDBCConfigObject())
    //Services
    val dataService = DataService(this)
    val webService = WebService(this)
    val messageService = MessageService(this)

    fun startServer() {
        try {
            dataService.start()
            webService.initService() //Init Web API Services
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

    fun save() {
        dataService.save()
    }

    fun stopServer() {
        rootLogger.info("Shutting down Server")
        ConsoleManager.stop()
        webService.shutdown()
        dataService.terminate()
        dataService.save {
            //TODO move configuration shutdown into Dataservice
            rootLogger.info("Disconnecting from Database")
            sharedJDBCClient.close()
            rootLogger.info("All Database connection shutdown")
            sharedVertx.close({ res ->
                if (res.succeeded()) {
                    rootLogger.info("Vert.X Shutdown")
                    System.exit(0)
                }
            })
        }

    }
}