/*
 * Copyright (c) 2016. Codetector (Yaotian Feng)
 */

package cn.codetector.jet.webService

import cn.codetector.util.Configuration.Configuration
import cn.codetector.util.Configuration.ConfigurationManager
import cn.codetector.jet.webService.IWebAPIImpl
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.JksOptions
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.web.Router
import org.reflections.Reflections
import java.util.*

class WebService (val sharedVertx: Vertx, val jdbcClient: JDBCClient){

    private val logger = LoggerFactory.getLogger(this.javaClass)

    var isServiceRunning = false
        private set

    private val config: Configuration = ConfigurationManager.getConfiguration("webConfig.json")
    private val useSSL = config.getBooleanValue("enableSSL", false)
    private val httpPort = config.getIntegerValue("serverPort", 8000)
    private val sslKeyStore = config.getStringValue("SSLKeystoreFile", "key.jks")
    private val sslPassword = config.getStringValue("SSLKeystorePassword", "db_password")

    private val serviceList: MutableList<IWebAPIImpl> = ArrayList<IWebAPIImpl>()

    private var server: HttpServer? = null

    init {
        registerProviders()
    }

    private fun registerProviders() {
        val reflections = Reflections()
        val allAnnotatedClasses: Set<Class<*>> = reflections.getTypesAnnotatedWith(WebAPIImpl::class.java)
        allAnnotatedClasses.forEach {
            clazz ->
            if (IWebAPIImpl::class.java.isAssignableFrom(clazz)) {
                serviceList.add(clazz.newInstance() as IWebAPIImpl)
            }
        }
    }

    fun initService() {
        if (!isServiceRunning) {
            logger.info("Starting WebService...")
            val router = Router.router(sharedVertx)
            serviceList.forEach {
                serviceImpl ->
                var prefix = ""
                for (annotation in serviceImpl.javaClass.declaredAnnotations) {
                    if (annotation is WebAPIImpl) {
                        prefix = annotation.prefix
                    }
                }
                if (prefix == "") {
                    serviceImpl.initAPI(router, sharedVertx, jdbcClient)
                } else {
                    val subRouter: Router = Router.router(sharedVertx)
                    serviceImpl.initAPI(subRouter, sharedVertx, jdbcClient)
                    router.mountSubRouter("/" + prefix, subRouter)
                }
            }
            router.route().handler {
                ctx ->
                ctx.response().setStatusCode(404).end("You should not land here.. Whoops!")
            }
            server = sharedVertx.createHttpServer(HttpServerOptions()
                    .setSsl(useSSL)
                    .setKeyStoreOptions(JksOptions()
                            .setPath(sslKeyStore)
                            .setPassword(sslPassword))
            )
            server!!.requestHandler { context ->
                router.accept(context)
            }.listen(httpPort, {
                handler ->
                this.isServiceRunning = true
                logger.info("WebService started at Port: ${httpPort}, SSL=${useSSL}")
            })
        } else {
            logger.warn("Failed to start service : Service already running")
        }
    }

    fun shutdown() {
        if (server != null && isServiceRunning) {
            logger.info("Shutting down Web Service ...")
            server!!.close { h ->
                this.isServiceRunning = false
                logger.info("Web Service Shutdown")
            }
        }
    }
}