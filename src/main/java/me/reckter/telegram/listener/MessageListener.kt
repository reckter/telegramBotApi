package me.reckter.telegram.listener

import me.reckter.telegram.model.Message

/**
 *  @author Hannes Güdelhöfer
 */
interface MessageListener {

    fun onMessage(message: Message)


}