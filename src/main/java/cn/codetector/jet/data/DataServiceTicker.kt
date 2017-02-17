/*
 * Copyright (c) 2016. Codetector (Yaotian Feng)
 */

package cn.codetector.jet.data

class DataServiceTicker(val interval: Long, val call: () -> Unit) : Runnable {
    private var running = true
    private var lastTick = 0L
    fun stopTick() {
        running = false
    }

    override fun run() {
        while (running) {
            if ((System.currentTimeMillis() - lastTick) >= interval) {
                lastTick = System.currentTimeMillis()
                call.invoke()
            }
            Thread.sleep(1)
        }
    }
}