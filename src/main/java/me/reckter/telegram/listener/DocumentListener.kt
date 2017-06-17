package me.reckter.telegram.listener

import me.reckter.telegram.model.Message

/**
 * Created by hannesgudelhofer on 16.06.17.
 */
interface DocumentListener {
    fun onDocument(message: Message)
}