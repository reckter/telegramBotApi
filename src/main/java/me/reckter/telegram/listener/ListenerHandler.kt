package me.reckter.telegram.listener

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import me.reckter.telegram.Telegram
import me.reckter.telegram.model.InlineQuery
import me.reckter.telegram.model.MessageType
import me.reckter.telegram.model.update.Update
import me.reckter.telegram.requests.inlineMode.InlineQueryAnswer
import org.slf4j.LoggerFactory

/**
 *  @author Hannes Güdelhöfer
 */
class ListenerHandler(val ignoreMessageBefore: Long, val telegram: Telegram) {

    private val LOG = LoggerFactory.getLogger(ListenerHandler::class.java)


    val commandListeners = mutableSetOf<CommandListener>()

    val messageListener = mutableSetOf<MessageListener>()

    val locationListener = mutableSetOf<LocationListener>()

    val editListener = mutableSetOf<EditListener>()

    val callBackListener = mutableSetOf<CallBackListener>()

    var inlineQueryHandler: ((InlineQuery) -> InlineQueryAnswer)? = null

    fun addReflectionListener(listener: Any) {
        val reflectionListener = ReflectionListener(listener)
        commandListeners.add(reflectionListener)
        messageListener.add(reflectionListener)
        locationListener.add(reflectionListener)
        editListener.add(reflectionListener)
        callBackListener.add(reflectionListener)
    }


    fun acceptUpdate(update: Update) {
        try {

            if (update.message != null) {
                if (update.message!!.date < ignoreMessageBefore) {
                    return
                }

                // inject telegram into message and chat
                update.message!!.telegram = telegram
                update.message!!.chat.telegram = telegram


                val type = update.message!!.type

                when (type) {
                    MessageType.COMMAND -> {
                        var found = false
                        val arguments = getArguments(update.message!!.text!!)
                        commandListeners.filter { it.accepts(arguments[0]) }
                                .forEach {
                                    found = true
                                    it.onCommand(update.message!!, arguments)
                                }
                        if (!found) {
                            if (!arguments[0].contains("@")) {
                                update.message!!.reply("Did not find this command.")
                            }
                        }
                    }
                    MessageType.MESSAGE -> {
                        messageListener.forEach { it.onMessage(update.message!!) }
                    }
                    MessageType.LOCATION -> {
                        locationListener.forEach { it.onLocation(update.message!!) }
                    }
                    MessageType.EDITED -> {
                        editListener.forEach { it.onEdit(update.message!!) }
                    }
                    else -> {
                    }
                }
            } else if (update.callbackQuery != null) {
                callBackListener.forEach { it.OnCallBack(update.callbackQuery!!) }
            } else if (update.inlineQuery != null) {
                if(inlineQueryHandler != null) {
                    telegram.sendInlineQueryAnswer(inlineQueryHandler!!(update.inlineQuery!!))
                }
            } else {
                val mapper = ObjectMapper()
                LOG.error("I DO NOT KNOW WHAT TO DO WITH THIS UPDATE!: " + mapper.writeValueAsString(update));
                return
            }


        } catch (e: Exception) {
            if (update.message == null) {
                LOG.error("update.message was null!")
                val mapper = ObjectMapper()
                try {
                    LOG.error(mapper.writeValueAsString(update))
                } catch (e1: JsonProcessingException) {
                    e1.printStackTrace()
                }

            } else {
                update.message!!.reply("Sorry got a hick up. Try again later.")
            }
            telegram.sendExceptionErrorMessage(e)
        }

    }


    private fun getArguments(text: String): List<String> {

        var tmp = text.substring(1)
        tmp += " "

        val split = tmp.split(" ".toRegex(), 2).toTypedArray()
        tmp = split[0].replace("_", " ") + " " + split[1]

        val ret = mutableListOf<String>()

        var argumentStartedAt = 0
        var insideQoutes = false
        var i = 0
        while (i < tmp.length) {

            if (tmp[i] == '\\') {
                i++
                continue
            }

            // because apple sometimes uses 8440 instead of 32 >_>
            if (tmp[i] == '"' || tmp[i] == '“') {
                insideQoutes = !insideQoutes
            }

            if (!insideQoutes && tmp[i] == ' ') {
                var offset = 0;
                if ((tmp[argumentStartedAt] == '"' || tmp[argumentStartedAt] == '“')) offset = 1
                ret.add(tmp.substring(argumentStartedAt + offset, i - offset))
                argumentStartedAt = i + 1
            }
            i++
        }
        if (argumentStartedAt != i) {
            ret.add(tmp.substring(argumentStartedAt, i))
        }


        ret[0] = ret[0].replace("@${telegram.me.username}", "").replace("/", "")
        return ret
    }
}
