package cn.codetector.jet.eventBus

import cn.codetector.jet.eventBus.annotation.EventHandler
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import java.util.*

object JetEventBus {
    private val logger = org.slf4j.LoggerFactory.getLogger("JEventBus")
    private val allEventBus: MutableMap<String, EventBus> = HashMap()

    /**
     * @param target just use <code>this</code> (It's the object you are trying to subscrive on
     */
    fun subscribe(target: Any) {
        target.javaClass.declaredMethods.forEach { method ->
            method.declaredAnnotations.forEach { annotation ->
                if (annotation.annotationClass == EventHandler::class) {
                    //Verify method signature
                    if (method.parameters.isEmpty() || ((method.parameters.size == 1)
                            && (method.parameters[0].type.isAssignableFrom(JsonObject::class.java)))) {
                        getEventBus((annotation as EventHandler).eventName).subscribe(target, method)
                    } else {
                        logger.error("Failed to subscribe ${target.javaClass.simpleName}.${method.name} params not compatible")
                    }
                }
            }
        }
    }

    fun fireEvent(eventName: String, eventArgs: JsonObject) {
        getEventBus(eventName).fireEvent(eventArgs)
    }

    private fun getEventBus(busName: String): EventBus {
        if (allEventBus.containsKey(busName) && allEventBus[busName] != null) {
            return allEventBus[busName]!!
        } else {
            allEventBus.put(busName, EventBus(busName))
            return getEventBus(busName)
        }
    }
}