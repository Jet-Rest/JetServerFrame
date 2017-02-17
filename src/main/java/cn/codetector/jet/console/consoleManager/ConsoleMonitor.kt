package cn.codetector.jet.console.consoleManager

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class ConsoleMonitor(val targetStream: InputStream) : Runnable {

    val reader: BufferedReader = BufferedReader(InputStreamReader(targetStream))

    var running = true

    fun terminate() {
        running = false;
    }

    override fun run() {
        while (running) {
            if (targetStream.available() > 0) {
                val line = reader.readLine()
                ConsoleManager.processCommand(line)
            } else {
                Thread.sleep(100)
            }
        }
    }
}