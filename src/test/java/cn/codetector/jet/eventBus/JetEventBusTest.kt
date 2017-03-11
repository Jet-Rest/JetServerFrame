package cn.codetector.jet.eventBus

import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


/**
 * Created by codetector on 18/02/2017.
 */
class JetEventBusTest {
    private var testObject = BusTestObject()

    @BeforeEach
    fun setUp() {
        testObject.resetCounter()
        JetEventBus.hashCode()
    }

    @Test
    fun subscribe() {
        JetEventBus.fireEvent("MagicEvent", JsonObject())
        Thread.sleep(50)
        assertEquals(0, testObject.getCount())
        JetEventBus.subscribe(testObject)
        Thread.sleep(50)
        assertEquals(0, testObject.getCount())
        JetEventBus.fireEvent("MagicEvent", JsonObject())
        Thread.sleep(50)
        assertEquals(1, testObject.getCount())
    }

    @AfterEach
    fun tearDown() {
        testObject.resetCounter()
    }

    @Test
    fun fireEvent() {
        JetEventBus.subscribe(testObject)
        JetEventBus.fireEvent("MagicEvent", JsonObject())
        Thread.sleep(50)
        assertEquals(1, testObject.getCount())
        JetEventBus.fireEvent("MagicEvent", JsonObject())
        Thread.sleep(50)
        assertEquals(2, testObject.getCount())
        JetEventBus.fireEvent("TestEvent", JsonObject())
        Thread.sleep(50)
        assertEquals(4, testObject.getCount())
    }

}