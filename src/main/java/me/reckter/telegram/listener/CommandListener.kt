package me.reckter.telegram.listener

import me.reckter.telegram.model.Message

/**
 *  @author Hannes Güdelhöfer
 */
interface CommandListener {
    fun onCommand(message: Message, arguments: List<String>)

    fun accepts(command:String): Boolean
}

abstract class SimpleCommandListener(val commandName: String): CommandListener {


    abstract fun onCommand(message:Message)

    override fun onCommand(message: Message, arguments: List<String>) = onCommand(message)

    override fun accepts(command: String): Boolean {
        return command == commandName
    }
}