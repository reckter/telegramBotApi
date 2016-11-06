package me.reckter.telegram

import java.util.stream.Stream


/**
 * @author hannes
 */
class Puller(var sleep: Int, var telegram: Telegram) {
    internal var isAlive = false

    internal var lastSeenUpdateId: Long = 0


    fun start() {
        isAlive = true
        println("starting pulling..")
        pullThread.start()
    }

    fun end() {
        isAlive = false
        pullThread.interrupt()
    }

    private val pullThread = Thread {
        while (isAlive) {
            val updates = telegram.getUpdates(lastSeenUpdateId + 1, 100, (sleep * 100).toLong())

            updates.forEach { update ->
                if (lastSeenUpdateId < update.id) {
                    lastSeenUpdateId = update.id.toLong()
                }
            }
            updates.forEach { update ->

                Thread {
                    telegram.acceptUpdate(update)
                }.start()
            }
        }
    }
}


public inline fun <T> Collection<T>.stream(): Stream<T>
        = (this as java.util.Collection<T>).stream()
