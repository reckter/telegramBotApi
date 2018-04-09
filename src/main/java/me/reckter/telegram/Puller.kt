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
            val updates = telegram.getUpdates(
                offset = lastSeenUpdateId + 1,
                limit = 100,
                timeout = (sleep * 100).toLong()
            )

            lastSeenUpdateId = updates
                .maxBy { it.id }?.id?.toLong() ?: lastSeenUpdateId

            updates
                .forEach { update ->
                Thread {
                    telegram.acceptUpdate(update)
                }.start()
            }
        }
    }
}

public inline fun <T> Collection<T>.stream(): Stream<T> = (this as java.util.Collection<T>).stream()
