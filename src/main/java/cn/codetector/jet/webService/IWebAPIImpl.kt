package cn.codetector.jet.webService

import io.vertx.core.Vertx
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.web.Router

interface IWebAPIImpl {
    fun initAPI(router: Router, sharedVertx: Vertx, dbClient: JDBCClient)
}