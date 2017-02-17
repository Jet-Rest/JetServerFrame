package cn.codetector.jet.util

import cn.codetector.yiling.server.data.services.configuration.DatabaseConfiguration
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.spi.FutureFactory
import io.vertx.ext.jdbc.JDBCClient

fun doesTableExist(tableName: String, dbClient: JDBCClient, action: (AsyncResult<Boolean>) -> Unit) {
    dbClient.getConnection { conn ->
        if(conn.succeeded()) {
            conn.result().query("SELECT `TABLE_NAME` FROM information_schema.tables WHERE table_schema = '${DatabaseConfiguration.db_name}' AND table_name = '${tableName}'", { q ->
                if (q.succeeded()) {
                    action.invoke(Future.succeededFuture(q.result().numRows == 1))
                } else {
                    action.invoke(Future.failedFuture(q.cause()))
                }
                conn.result().close()
            })
        } else {
            action.invoke(Future.failedFuture(conn.cause()))
        }
    }
}
