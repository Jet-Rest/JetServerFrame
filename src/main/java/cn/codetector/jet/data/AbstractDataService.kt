/*
 * Copyright (c) 2016. Codetector (Yaotian Feng)
 */

package cn.codetector.jet.data

import cn.codetector.jet.Jet
import cn.codetector.yiling.server.data.services.configuration.DatabaseConfiguration
import io.vertx.ext.jdbc.JDBCClient

/**
 * the Abstracted dataService. Extend this class to create your own data service. (Will automatically load)
 */
abstract class AbstractDataService {
//    @Deprecated(message = "dbClient is deprecated please use jet.sharedJDBCClient", level = DeprecationLevel.WARNING)
    protected lateinit var dbClient: JDBCClient
    protected lateinit var jet: Jet
    private var hasChange = false
    protected val dbprefix = DatabaseConfiguration.db_prefix

    open fun serviceName(): String {
        return this.javaClass.simpleName
    }

    fun isInitialized(): Boolean {
        return true
    }

    fun setJetInstance(jet: Jet) {
        this.jet = jet
        this.dbClient = jet.sharedJDBCClient
        initialize()
    }

    abstract fun initialize()

    open fun shutdown() {}

    abstract fun saveToDatabase(action: () -> Unit)
    fun saveToDatabase() {
        saveToDatabase {}
    }

    abstract fun loadFromDatabase(action: () -> Unit)
    fun loadFromDatabase() {
        loadFromDatabase {}
    }

    open fun markChange() {
        this.hasChange = true
    }

    protected fun hasChanged(): Boolean {
        if (hasChange) {
            hasChange = false
            return true
        }
        return false
    }

    open fun tick() {
        if (this.hasChanged()) {
            saveToDatabase()
        }
    }

    open fun loadingPriority(): Int {
        return 0
    }
}