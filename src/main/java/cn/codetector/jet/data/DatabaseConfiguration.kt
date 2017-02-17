/*
 * Copyright (c) 2016. Codetector (Yaotian Feng)
 */

package cn.codetector.yiling.server.data.services.configuration

import cn.codetector.util.Configuration.ConfigurationManager
import io.vertx.core.json.JsonObject

/**
 * Created by Codetector on 2016/7/14.
 */
object DatabaseConfiguration {
    private val dbConfig = ConfigurationManager.getConfiguration("mysql.json")

    val driver_class = dbConfig.getStringValue("driver_class", "com.mysql.jdbc.Driver")
    val max_pool_size = dbConfig.getIntegerValue("max_pool_size", 5)
    val initial_pool_size = dbConfig.getIntegerValue("initial_pool_size", 3)
    val db_user = dbConfig.getStringValue("db_user", "guardian_classroom")
    val db_password = dbConfig.getStringValue("db_password", "")
    val db_url = dbConfig.getStringValue("url", "jdbc:mysql://localhost:3306/")
    val db_name = dbConfig.getStringValue("db_name", "guardian_classroom")
    val db_ssl = dbConfig.getStringValue("useSSL", "false")
    val db_charset = dbConfig.getStringValue("charSet", "utf-8")
    val db_prefix = dbConfig.getStringValue("db_prefix", "classroom")
    val db_max_idle_time = dbConfig.getIntegerValue("db_max_idle_time", 30)

    fun getDBConnectionURLWithSettings(): String {
        return "${db_url}${db_name}?useSSL=${db_ssl}&characterEncoding=${db_charset}"
    }

    fun getVertXJDBCConfigObject(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.put("driver_class", driver_class)
        jsonObject.put("user", db_user)
        jsonObject.put("password", db_password)
        jsonObject.put("max_pool_size", max_pool_size)
        jsonObject.put("initial_pool_size", initial_pool_size)
        jsonObject.put("max_idle_time", db_max_idle_time)
        jsonObject.put("url", getDBConnectionURLWithSettings())
        return jsonObject
    }

}
