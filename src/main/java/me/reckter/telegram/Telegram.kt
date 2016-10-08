package me.reckter.telegram

import com.fasterxml.jackson.databind.ObjectMapper
import me.reckter.telegram.listener.ListenerHandler
import me.reckter.telegram.model.Error
import me.reckter.telegram.model.Location
import me.reckter.telegram.model.Message
import me.reckter.telegram.model.User
import me.reckter.telegram.model.update.CallbackQuery
import me.reckter.telegram.model.update.Update
import me.reckter.telegram.requests.*
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author hannes
 */
class Telegram(apiKey: String, startPulling: Boolean) {

    private val telegramClient: TelegramClient

    internal var mapper = ObjectMapper()

    object Endpoints {

        var UPDATE = "getUpdates"

        var SEND_MESSAGE = "sendMessage"
        var EDIT_MESSAGE_TEXT = "editMessageText"

        var SEND_LOCATION = "sendLocation"
        var GET_ME = "getMe"
        var SEND_CHAT_ACTION = "sendChatAction"
        var SET_WEBHOOK = "setWebhook"

        var SEND_STICKER = "sendSticker"
    }

    val listenerHandler = ListenerHandler(System.currentTimeMillis() / 1000, this)


    var provider: Provider
        internal set

    var adminChat: String = ""
        internal set

    internal var replyMarkup = Optional.empty<String>()

    init {

        val okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(101, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build()

        telegramClient = Retrofit.Builder()
                .baseUrl(API_URL + apiKey + "/")
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .client(okHttpClient)
                .build()
                .create(TelegramClient::class.java)
        this.provider = PullProvider(1000, this)
        this.provider.setApiKey(apiKey)
        if (startPulling) {
            this.provider.start()
        }


    }


    constructor(apiKey: String, adminChat: String) : this(apiKey, true) {
        this.adminChat = adminChat

        sendMessage(adminChat, "booted\n" + me)
    }

    constructor(apiKey: String, adminChat: Int) : this(apiKey, true) {
        this.adminChat = adminChat.toString()

        sendMessage(adminChat.toString(), "booted\n" + me)
    }

    constructor(apiKey: String, adminChat: String, startPulling: Boolean) : this(apiKey, startPulling) {
        this.adminChat = adminChat

        sendMessage(adminChat, "booted\n" + me)
    }

    constructor(apiKey: String, adminChat: Int, startPulling: Boolean) : this(apiKey, startPulling) {
        this.adminChat = adminChat.toString()

        sendMessage(adminChat.toString(), "booted\n" + me)
    }


    constructor(apiKey: String, adminChat: Int, errorApiKey: String) : this(apiKey, adminChat) {
        errorTelegramBot = Telegram(errorApiKey, false)

    }

    constructor(apiKey: String, adminChat: Int, errorApiKey: String, startPulling: Boolean) : this(apiKey, adminChat, startPulling) {
        errorTelegramBot = Telegram(errorApiKey, false)
    }

    @JvmOverloads fun sendExceptionErrorMessage(e: Exception, additionalMessage: String = "") {
        val result = StringWriter()
        val printWriter = PrintWriter(result)
        e.printStackTrace(printWriter)
        sendErrorMessage(if (additionalMessage != "") additionalMessage + "\n\n" + result.toString() else result.toString())
    }

    fun sendErrorMessage(message: String) {
        var message = message
        val telegramToUse = errorTelegramBot ?: this

        message = "Error in Bot '" + me.username + "':\n" + message
        telegramToUse.sendMessage(adminChat, message, ParseMode.NONE, Optional.empty<Boolean>(), Optional.empty<Boolean>(), Optional.empty<Int>())

    }

    fun addListener(listener: Any) {
        listenerHandler.addReflectionListener(listener)
    }


    fun acceptUpdate(update: Update) {
        listenerHandler.acceptUpdate(update)
    }


    val update: List<Update>
        get() = getUpdates(-1, -1, -1)

    fun getUpdates(offset: Long, limit: Long, timeout: Long): List<Update> {
        val updateRequest = UpdateRequest()
        if (offset != -1L) {
            updateRequest.offset = offset
        }

        if (limit != -1L) {
            updateRequest.limit = limit
        }

        if (timeout != -1L) {
            updateRequest.timeout = timeout
        }
        return telegramClient.getUpdates(updateRequest).execute().body().result
    }


    val me: User by lazy { telegramClient.getMe().execute().body().result }

    fun setReplyMarkupToHideKeyboard(selective: Boolean) {
        replyMarkup = Optional.of("{force_reply:true,selective:$selective}")
    }

    fun setReplyMarkupToCustomKeyboard(keys: Array<Array<String>>, selective: Boolean) {
        setReplyMarkupToCustomKeyboard(keys, Optional.empty<Boolean>(), Optional.empty<Boolean>(), selective)
    }

    fun setReplyMarkupToCustomKeyboard(keys: Array<Array<String>>, resizeKeyboard: Optional<Boolean>, oneTimeKeyboard: Optional<Boolean>, selective: Boolean) {
        val reply = StringBuilder("{\"keyboard\":[")
        for (row in keys) {
            reply.append("[")
            for (key in row) {
                reply.append("\"").append(key).append("\",")
            }
            reply.deleteCharAt(reply.length - 1).append("],")
        }
        reply.deleteCharAt(reply.length - 1).append("]")
        if (resizeKeyboard.isPresent) {
            reply.append(",\"resize_keyboard\":").append(resizeKeyboard.get())
        }
        if (oneTimeKeyboard.isPresent) {
            reply.append(",\"one_time_keyboard\":").append(oneTimeKeyboard.get())
        }
        reply.append(",\"selective\":").append(selective)
        reply.append("}")
        replyMarkup = Optional.of(reply.toString())
    }

    fun setReplyMarkupToForceReply(selective: Boolean) {
        replyMarkup = Optional.of("{force_reply:true,selective:$selective}")
    }

    fun sendChatAction(chatId: String, action: ChatAction) {

        val chatActionRequest = ChatActionRequest()
        chatActionRequest.id = chatId
        chatActionRequest.action = action

        telegramClient.sendChatAction(chatActionRequest).execute()
    }

    @JvmOverloads fun sendSticker(chatId: String, fileId: String, disableNotifications: Optional<Boolean> = Optional.empty<Boolean>(), replyToMessageId: Optional<Int> = Optional.empty<Int>()): Message {
        val request = StickerRequest()
        request.chatId = chatId
        request.sticker = fileId
        if (disableNotifications.isPresent) {
            request.disableNotifications = disableNotifications.get()
        }
        if (replyToMessageId.isPresent) {
            request.replyTo = replyToMessageId.get().toLong()
        }
        return telegramClient.sendSticker(request).execute().body().result
    }

    @JvmOverloads fun sendSticker(chatId: String, file: File, disableNotifications: Optional<Boolean> = Optional.empty<Boolean>(), replyToMessageId: Optional<Int> = Optional.empty<Int>()): Message {

        return telegramClient.sendSticker(chatId, file).execute().body().result // TODO optional stuff
    }

    @JvmOverloads fun sendLocation(chatId: Long, location: Location, disableNotification: Optional<Boolean> = Optional.empty<Boolean>()): Message {
        val locationRequest = LocationRequest()

        locationRequest.id = chatId
        locationRequest.latitude = location.latitude
        locationRequest.longitude = location.longitude

        if (disableNotification.isPresent) {
            locationRequest.disableNotification = disableNotification.get()
        }

        return telegramClient.sendLocation(locationRequest).execute().body().result
    }

    fun buildMessage(): MessageBuilder = MessageBuilder(this)

    fun buildMessage(init: MessageBuilder.() -> Unit): MessageBuilder {
        var ret = buildMessage();
        ret.init()
        return ret
    }

    fun buildEditMessage(message: Message) = UpdateMessageBuilder(message, this)

    fun buildEditMessage(message: Message, init: UpdateMessageBuilder.() -> Unit): UpdateMessageBuilder {
        val ret = UpdateMessageBuilder(message, this)
        ret.init()
        return ret
    }

    fun sendEditMessage(message: Message, init: UpdateMessageBuilder.() -> Unit) = buildEditMessage(message, init).send()


    fun sendMessage(init: MessageBuilder.() -> Unit): Message {
        return buildMessage(init).send()
    }


    fun sendMessage(messageRequest: MessageRequest): Message {

        val response = telegramClient.sendMessage(messageRequest).execute()
        if (response.isSuccessful) {
            return response.body().result
        }
        val error = mapper.readValue(response.errorBody().charStream().readText(), Error::class.java)


        if (error.errorCode == 400 && error.description.contains("Message is too long")) {
            val firstHalf = MessageRequest(messageRequest)
            val secondHalf = MessageRequest(messageRequest)
            val text = messageRequest.text
            var text1 = text.substring(0, text.length / 2)
            var text2 = text.substring(text1.length, text.length)

            text1 += text2.split("\n".toRegex(), 2).toTypedArray()[0]
            text2 = text2.split("\n".toRegex(), 2).toTypedArray()[1]

            println("splitting message of length " + text.length + " into " + text1.length + " and " + text2.length)

            firstHalf.text = text1
            secondHalf.text = text2

            sendMessage(firstHalf)
            return sendMessage(secondHalf)

        } else if (error.errorCode == 400 && error.description.startsWith("[Error]: Bad Request: Can't parse message text:")) {
            //System.out.println("Markdown parsing is wrong! Please have a look at '" + text + "'")
            messageRequest.parseMode = ParseMode.NONE
            return sendMessage(messageRequest)
        } else {
            throw RuntimeException("error sending message! " + error.description + " (" + error.errorCode + ")")
        }

    }

    /**
     * @param chatId             chat id to send to
     * *
     * @param text               text t send
     * *
     * @param disableWebPageView if web page view is dissabled
     * *
     * @param replyToMessageId   if this is a resopnd set to resondId
     * *
     * @return the messag send.
     */
    @JvmOverloads fun sendMessage(chatId: String, text: String, parseMode: ParseMode = ParseMode.NONE, disableNotification: Optional<Boolean> = Optional.empty<Boolean>(), disableWebPageView: Optional<Boolean> = Optional.empty<Boolean>(), replyToMessageId: Optional<Int> = Optional.empty<Int>()): Message {

        val messageRequest = MessageRequest()
        messageRequest.id = chatId
        messageRequest.text = text
        messageRequest.parseMode = parseMode
        if (disableNotification.isPresent) {
            messageRequest.disableNotification = disableNotification.get()
        }
        if (disableWebPageView.isPresent) {
            messageRequest.isDisableWebPagePreview = disableWebPageView.get()
        }
        if (replyToMessageId.isPresent) {
            messageRequest.replyTo = replyToMessageId.get()
        }
        return sendMessage(messageRequest)
    }

    @JvmOverloads fun editMessage(message: Message, parseMode: ParseMode = ParseMode.NONE, disableWebPageView: Optional<Boolean> = Optional.empty<Boolean>()) {
        editMessage(message.chat.id, message.id.toLong(), message.text!!, parseMode, disableWebPageView)
    }


    fun editMessage(updateMessageRequest: UpdateMessageRequest) {

        telegramClient.editMessage(updateMessageRequest).execute()
    }

    @JvmOverloads fun editMessage(chatId: String, messageId: Long, text: String, parseMode: ParseMode = ParseMode.NONE, disableWebPageView: Optional<Boolean> = Optional.empty<Boolean>()) {

        val messageRequest = UpdateMessageRequest()

        messageRequest.id = chatId.toString()
        messageRequest.setMessageId(messageId)
        messageRequest.text = text
        messageRequest.parseMode = parseMode
        if (disableWebPageView.isPresent) {
            messageRequest.isDisableWebPagePreview = disableWebPageView.get()
        }

        editMessage(messageRequest)

    }


    fun answerCallback(callbackQuery: CallbackQuery, text: String?? = null, url: String? = null, showAlert: Boolean? = null) {
        answerCallback(AnswerCallbackQuery().apply {
            this.queryId = callbackQuery.id
            this.text = text
            this.url = url
            this.showAlert = showAlert
        })
    }

    fun answerCallback(answerCallbackQuery: AnswerCallbackQuery) {
        telegramClient.answerCallbackQuery(answerCallbackQuery).execute()
    }


    fun getErrorTelegramBot(): Telegram {
        return errorTelegramBot ?: this
    }

    companion object {

        private val LOG = LoggerFactory.getLogger(Telegram::class.java)

        private var errorTelegramBot: Telegram? = null


        var API_URL = "https://api.telegram.org/bot"
    }
}

