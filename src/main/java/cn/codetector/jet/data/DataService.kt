/*
 * Copyright (c) 2016. Codetector (Yaotian Feng)
 */

package cn.codetector.jet.data

import cn.codetector.jet.Jet
import cn.codetector.jet.data.annotation.DataService
import io.vertx.core.logging.LoggerFactory
import org.reflections.Reflections
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created by Codetector on 20/11/2016.
 */
class DataService(val jet: Jet) {
    val logger = LoggerFactory.getLogger(this.javaClass)
    val executors: ExecutorService = Executors.newSingleThreadExecutor()
    private val services: MutableMap<String, AbstractDataService> = HashMap<String, AbstractDataService>()

    fun start() {
        logger.info("Starting DataService...")
//        //Initialize Services
//        PermissionManager.setDBClient(Main.sharedJDBCClient)
//        UserManager.setDBClient(Main.sharedJDBCClient)
//        IPLocationService.setDBClient(Main.sharedJDBCClient)
//        AuthLogService.setDBClient(Main.sharedJDBCClient)
//
//        // Register Tickers
//        executors.submit(DataServiceTicker(5000, {
//            PermissionManager.tick()
//            UserManager.tick()
//            UserHash.tick()
//            AuthLogService.tick()
//            IPLocationService.tick()
//        }))
//        load()
        val reflections = Reflections()
        reflections.getSubTypesOf(AbstractDataService::class.java).forEach { clazz ->
            val service = clazz.newInstance()
            services.put(service.serviceName(), service)
        }

        logger.info("Initializing DataServices....")
        logger.trace("Sorting DataServices by Loading-priority")
        val sortedServiceList = services.values.toMutableList()
        sortedServiceList.sortByDescending { it.loadingPriority() }
        logger.trace("DataServices Sorted!")
        sortedServiceList.forEach { service ->
            logger.info("Initializing ${service.serviceName()} (P:${service.loadingPriority()})...")
            injectServices(service)
            service.setJetInstance(jet)
        }
        load()
        executors.submit(DataServiceTicker(5000, {
            services.forEach { s ->
                s.value.tick()
            }
        }))
        logger.info("DataServices Started!")
    }

    fun getService(name: String): AbstractDataService {
        if (services.contains(name))
            return services.get(name)!!
        else
            throw IllegalArgumentException("Error")
    }

    fun save() {
        save {}
    }

    fun save(action: () -> Unit) {
        logger.info("Saving DataServices....")
        logger.trace("Sorting DataServices by Loading-priority")
        val sortedServiceList = services.values.toMutableList()
        sortedServiceList.sortByDescending { it.loadingPriority() }
        logger.trace("DataServices Sorted!")
        sortedServiceList.forEach { service ->
            val sign: CountDownLatch = CountDownLatch(1);
            logger.info("Saving ${service.serviceName()} (P:${service.loadingPriority()})...")
            service.saveToDatabase {
                sign.countDown()
            }
            sign.await()
        }
        logger.info("DataServices Saved!")
        action.invoke()
    }

    fun terminate() {
        logger.info("Shutting down Data Services Sorted!")
        val sortedServiceList = services.values.toMutableList()
        sortedServiceList.sortByDescending { it.loadingPriority() }
        logger.trace("DataServices Sorted!")
        sortedServiceList.forEach { service ->
            val sign: CountDownLatch = CountDownLatch(1);
            logger.info("Saving ${service.serviceName()} (P:${service.loadingPriority()})...")
            service.saveToDatabase {
                sign.countDown()
            }
            sign.await()
            logger.info("Terminating ${service.serviceName()} (P:${service.loadingPriority()})...")
            service.shutdown()
        }
        executors.awaitTermination(3, TimeUnit.SECONDS)
        executors.shutdown()
    }

    fun isTerminated(): Boolean {
        return executors.isTerminated
    }

    fun reload() {
        load()
    }

    /**
     * @param service any object that you need some dataService to be injected in. Does not have to be a subclass of dataService. Anything could work.
     * Inject dataService into any annotated field
     * @see DataService
     */
    fun injectServices(service: Any) {
        service.javaClass.declaredFields.forEach { field ->
            logger.debug("Field found: ${field.name}:${field.type}")
            field.declaredAnnotations.forEach { annotation ->
                logger.debug("Annotation found: ${annotation}:${field.name}")
                if (annotation.annotationClass == DataService::class) {
                    val ann = field.getDeclaredAnnotation(DataService::class.java)
                    try {
                        val targetService = getService(ann.name)
                        if (field.type.isAssignableFrom(targetService.javaClass)) {
                            field.isAccessible = true
                            field.set(service, targetService)
                            logger.debug("Service injected: ${ann.name} for ${field.name} in ${service.javaClass.simpleName}")
                        } else {
                            logger.error("Failed to inject incompatible service ${targetService.javaClass.name} into ${field.type.name}")
                        }
                    } catch (e: IllegalArgumentException) {
                        logger.error("Non-existing service @ ${field.name} in ${service.javaClass.name}")
                    }
                }
            }
        }
    }

    fun load() {
        logger.info("Loading DataServices....")
        logger.trace("Sorting DataServices by Loading-priority")
        val sortedServiceList = services.values.toMutableList()
        sortedServiceList.sortByDescending { it.loadingPriority() }
        logger.trace("DataServices Sorted!")
        sortedServiceList.forEach { service ->
            val sign: CountDownLatch = CountDownLatch(1);
            logger.info("Loading ${service.serviceName()} (P:${service.loadingPriority()})...")
            service.loadFromDatabase {
                sign.countDown()
            }
            sign.await()
        }
        logger.info("DataServices Loaded!")
    }
}