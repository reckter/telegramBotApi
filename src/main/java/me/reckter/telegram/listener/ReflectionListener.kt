package me.reckter.telegram.listener

import me.reckter.telegram.model.Message
import me.reckter.telegram.model.MessageType
import me.reckter.telegram.model.update.CallbackQuery
import java.lang.reflect.Method

/**
 *  @author Hannes Güdelhöfer
 */
class ReflectionListener(val listener: Any) : MessageListener, CommandListener, EditListener, LocationListener, CallBackListener {


    val methods = mutableMapOf<MessageType, MutableList<Method>>()

    val callBackListener = mutableListOf<Method>()

    init {
        MessageType.values()
                .filter { it != MessageType.ALL }
                .forEach {
                    methods.put(it, mutableListOf<Method>())
                }
        parse()
    }

    private fun parse() {
        val listenerMethods = listener.javaClass.methods
        for (listenerMethod in listenerMethods) {
            val messageTypes: List<MessageType> = when {
                listenerMethod.isAnnotationPresent(OnMessage::class.java) -> {
                    var tmp = listenerMethod.getAnnotation(OnMessage::class.java).value.toMutableSet()
                    if (tmp.any { it == MessageType.ALL }) {
                        tmp.addAll(MessageType.values())
                    }
                    tmp.remove(MessageType.ALL)
                    tmp.toList()
                }
                listenerMethod.isAnnotationPresent(OnCommand::class.java) -> listOf(MessageType.COMMAND)
                listenerMethod.isAnnotationPresent(OnLocation::class.java) -> listOf(MessageType.LOCATION)
                listenerMethod.isAnnotationPresent(OnUserJoin::class.java) -> listOf(MessageType.NEW_CHAT_PARTICIPANT)
                listenerMethod.isAnnotationPresent(OnUserLeave::class.java) -> listOf(MessageType.LEFT_CHAT_PARTICIPANT)
                listenerMethod.isAnnotationPresent(OnCallBack::class.java) -> {
                    listenerMethod.isAccessible = true
                    callBackListener.add(listenerMethod)
                    listOf()
                }
                else -> {
                    listOf()
                }
            }
            if (messageTypes.size > 0) {
                listenerMethod.isAccessible = true
                messageTypes.forEach {
                    methods[it]!!.add(listenerMethod)
                }
            }
        }
    }


    private fun methodAcceptsCommand(method: Method, command: String): Boolean {
        if(!method.isAnnotationPresent(OnCommand::class.java)) {
            return true
        }
        val trigger = method.getAnnotation(OnCommand::class.java)

        return trigger.value.any { it == command }
    }


    override fun onEdit(message: Message) {
        methods[message.type]!!.forEach { it.invoke(listener, message) }
    }

    override fun onCommand(message: Message, arguments: List<String>) {
        methods[message.type]!!.filter { methodAcceptsCommand(it, arguments.first()) }
                .forEach {
                    if(it.parameterCount == 2) {
                        it.invoke(listener, message, arguments)
                    } else {
                        it.invoke(listener, message)
                    }
                }
    }

    override fun onMessage(message: Message) {
        methods[message.type]!!.forEach { it.invoke(listener, message) }
    }

    override fun onLocation(message: Message) {
        methods[message.type]!!.forEach { it.invoke(listener, message) }
    }

    override fun accepts(command: String): Boolean {
        return methods[MessageType.COMMAND]!!.any { methodAcceptsCommand(it, command) }
    }

    override fun OnCallBack(callbackQuery: CallbackQuery) {
        callBackListener.forEach { it.invoke(listener, callbackQuery) }
    }
}