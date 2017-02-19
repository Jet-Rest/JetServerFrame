package cn.codetector.jet.eventBus

import cn.codetector.jet.eventBus.annotation.EventHandler
import io.vertx.core.json.JsonObject

/**
 * Created by codetector on 18/02/2017.
 */
class BusTestObject {

    private var count: Int = 0
    var magicValue = 0

    @EventHandler(eventName = "MagicEvent")
    fun eventBusHandler(){
        count ++
    }

    fun resetCounter() {
        count = 0
    }

    fun getCount(): Int {
        return this.count
    }

    @EventHandler(eventName = "TestEvent")
    private fun eventBusHandler(obj: JsonObject){
        count ++
        if (obj.containsKey("magic")) {
            magicValue = obj.getInteger("magic")
        }
    }

    @EventHandler(eventName = "TestEvent")
    fun eventBusHandlers(obj: JsonObject){
        count ++
    }
}