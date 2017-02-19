package cn.codetector.jet.eventBus

import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import java.lang.reflect.Method
import java.util.*

open class EventBus(val busName: String) {
    private val logger = LoggerFactory.getLogger("JEventBus/${busName}")
    private val subscribers: MutableCollection<EventSubscriber> = HashSet()

    fun subscribe(target: Any, m: Method) {
        subscribers.add(EventSubscriber(target, m))
    }

    internal fun fireEvent(arg: JsonObject) {
        logger.debug(MarkerFactory.getMarker("JEventBus"), "Event fired with data: ${arg.encode()}")
        Thread({
            subscribers.forEach { s ->
                if (s.method.parameterCount > 0) {
                    s.method.invoke(s.target, arg)
                } else {
                    s.method.invoke(s.target)
                }
            }
        }).start()
    }
}