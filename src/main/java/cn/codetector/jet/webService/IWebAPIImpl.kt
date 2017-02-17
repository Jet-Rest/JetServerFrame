package cn.codetector.jet.webService

import cn.codetector.jet.Jet
import io.vertx.core.Vertx
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.web.Router

interface IWebAPIImpl {
    fun initAPI(router: Router, jet: Jet)
}