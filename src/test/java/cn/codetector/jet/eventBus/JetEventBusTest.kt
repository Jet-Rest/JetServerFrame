package cn.codetector.jet.eventBus

import io.vertx.core.json.JsonObject
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.util.*

/**
 * Created by codetector on 18/02/2017.
 */
class JetEventBusTest {
    private var testObject = BusTestObject()

    @Before
    fun setUp() {
        testObject.resetCounter()
        JetEventBus.hashCode()
    }

    @Test
    fun subscribe() {
        JetEventBus.fireEvent("MagicEvent", JsonObject())
        assertEquals(0, testObject.getCount())
        JetEventBus.subscribe(testObject)
        assertEquals(0, testObject.getCount())
        JetEventBus.fireEvent("MagicEvent", JsonObject())
        assertEquals(1, testObject.getCount())
    }

    @After
    fun tearDown() {
        testObject.resetCounter()
    }

    @Test
    fun fireEvent() {
        JetEventBus.subscribe(testObject)
        JetEventBus.fireEvent("MagicEvent", JsonObject())
        assertEquals(1, testObject.getCount())
        JetEventBus.fireEvent("MagicEvent", JsonObject())
        assertEquals(2, testObject.getCount())
        JetEventBus.fireEvent("TestEvent", JsonObject())
        assertEquals(4, testObject.getCount())
    }

}