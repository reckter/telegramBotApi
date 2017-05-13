package me.reckter.telegram

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import me.reckter.telegram.listener.ListenerHandler
import me.reckter.telegram.model.*
import me.reckter.telegram.model.update.CallbackQuery
import me.reckter.telegram.model.update.ChosenInlineResult
import me.reckter.telegram.model.update.Update
import me.reckter.telegram.requests.*
import me.reckter.telegram.requests.inlineMode.InlineQueryAnswer
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

    var shouldSendErrors = true

    internal var mapper = ObjectMapper()

    val listenerHandler = ListenerHandler(System.currentTimeMillis() / 1000, this)

    var puller: Puller
        internal set

    var adminChat: String = ""

    init {

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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

        this.puller = Puller(1000, this)
        if (startPulling) {
            this.puller.start()
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

    constructor(apiKey: String, adminChat: String, errorApiKey: String) : this(apiKey, adminChat) {
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

        if (!shouldSendErrors) {
            println(message)
        }
        try {
            telegramToUse.sendMessage(adminChat, message, ParseMode.NONE, Optional.empty<Boolean>(), Optional.empty<Boolean>(), Optional.empty<Int>())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun inlineQueryHandler(handler: (InlineQuery) -> InlineQueryAnswer): InlineResultHandlerBuilder {
        listenerHandler.inlineQueryHandler = handler
        return InlineResultHandlerBuilder(this)
    }

    fun inlineResultHandler(handler: (ChosenInlineResult) -> Unit) {
        listenerHandler.inlineResultHandler = handler
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
        try {
            val result = telegramClient.getUpdates(updateRequest).execute()
            if (!result.isSuccessful) {
                return listOf()
            }
            return result.body().result
        } catch(e: Exception) {
            sendExceptionErrorMessage(e, "error while trying to pull updates")
            return listOf()
        }
    }


    val me: User by lazy { telegramClient.getMe().execute().body().result }


    fun forwardMessage(message: Message, chat: Chat, disableNotification: Boolean? = null)
            = forwardMessage(message, chat.id, disableNotification)


    fun forwardMessage(message: Message, chatId: String, disableNotification: Boolean? = null)
            = forwardMessage(message.id, message.chat.id, chatId, disableNotification)

    fun forwardMessage(messageId: Int, fromChat: String, toChat: String, disableNotification: Boolean? = null): Message {
        return telegramClient.forwardMessage(ForwardMessageRequest().apply {
            this.messageId = messageId
            this.fromChat = fromChat
            this.toChat = toChat
            this.disableNotification = disableNotification
        }).execute().body().result
    }


    fun sendInlineQueryAnswer(inlineQueryAnswer: InlineQueryAnswer) {
        val response = telegramClient.answerInlineQuery(inlineQueryAnswer).execute()
        if (!response.isSuccessful)
            throw RuntimeException("inline query answer exception: ${response.errorBody().string()} result: ${ObjectMapper().writeValueAsString(inlineQueryAnswer)}")
    }

    fun sendChatAction(chatId: String, action: ChatAction) {

        val chatActionRequest = ChatActionRequest()
        chatActionRequest.id = chatId
        chatActionRequest.action = action

        telegramClient.sendChatAction(chatActionRequest).execute()
    }

    fun sendPhoto(photoRequest: PhotoRequest): Message {
        val response = telegramClient.sendPhoto(photoRequest).execute()

        if (response.isSuccessful) {
            return response.body().result
        }
        val error = mapper.readValue(response.errorBody().charStream().readText(), Error::class.java)

        throw RuntimeException("error sending photo! " + error.description + " (" + error.errorCode + ")")
    }

    fun sendPhoto(
            chatId: String,
            photo: String,
            caption: String? = null,
            disableNotification: Boolean? = null,
            replyTo: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Message {
        val request = PhotoRequest(
                id = chatId,
                photo = photo,
                caption = caption,
                disableNotification = disableNotification,
                replyTo = replyTo,
                replyMarkup = replyMarkup
        )

        return sendPhoto(request)
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

    fun buildEditMessage(message: Message) = UpdateMessageBuilder(message.id, message.chat.id, this)


    fun buildEditMessage(inlineMessageId: String, init: UpdateMessageBuilder.() -> Unit): UpdateMessageBuilder {
        val ret = UpdateMessageBuilder(inlineMessageId, this)
        ret.init()
        return ret
    }

    fun buildEditMessage(message: Message, init: UpdateMessageBuilder.() -> Unit): UpdateMessageBuilder {
        val ret = UpdateMessageBuilder(message.id, message.chat.id, this)
        ret.init()
        return ret
    }

    fun buildEditMessage(chatId: String, messageId: Int, init: UpdateMessageBuilder.() -> Unit): UpdateMessageBuilder {
        val ret = UpdateMessageBuilder(messageId, chatId, this)
        ret.init()
        return ret
    }


    fun sendEditMessage(inlineMessageId: String, init: UpdateMessageBuilder.() -> Unit) = buildEditMessage(inlineMessageId, init).send()

    fun sendEditMessage(message: Message, init: UpdateMessageBuilder.() -> Unit) = buildEditMessage(message, init).send()

    fun sendEditMessage(chatId: String, messageId: Int, init: UpdateMessageBuilder.() -> Unit) = buildEditMessage(chatId, messageId, init).send()


    fun getChat(id: String): Chat? {
        val response = telegramClient.getChat(id).execute()
        val body = response.body()
        return body?.result
    }

    fun getAdministrator(chat: Chat) = getAdministrator(chat.id)


    fun getChatMember(chat: Chat, user: User) = getChatMember(chat.id, user.id)

    fun getChatMemberCount(chat: Chat) = getChatMemberCount(chat.id)


    fun getAdministrator(chatId: String) = telegramClient.getChatAdministrators(chatId).execute().body().result

    fun getChatMember(chatId: String, userId: String): ChatMember? {
        val response = telegramClient.getChatMember(chatId, userId).execute()
        val body = response.body()
        return body?.result
    }

    fun getChatMemberCount(chatId: String) = telegramClient.getChatMembersCount(chatId).execute().body().result


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

            val split = text2.split("\n".toRegex(), 2)

            if (split.size == 2) {
                text1 += split.toTypedArray()[0]
                text2 = split.toTypedArray()[1]
            }

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
        messageRequest.messageId = messageId
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

    fun setWebhook(url: String) {
        telegramClient.setWebhook(WebhookRequest().apply {
            this.url = url
        }).execute()
        puller.end()
    }

    fun setWebhook(url: String, certificate: File) {
        telegramClient.setWebhook(url, certificate).execute()
        puller.end()
    }


    private fun deleteWebhook() {
        telegramClient.deleteWebhook()
    }

    fun getWebhookInfo(): WebhookInfo = telegramClient.getWebhookInfo().execute().body().result

    fun getErrorTelegramBot() = errorTelegramBot ?: this

    companion object {

        private val LOG = LoggerFactory.getLogger(Telegram::class.java)

        var errorTelegramBot: Telegram? = null


        var API_URL = "https://api.telegram.org/bot"
    }

    class Builder {
        lateinit var apiToken: String
        var errorToken: String? = null
        var adminChat: String? = null
        var webHook: String? = null
        var certificate: File? = null

        fun apiToken(apiToken: String): Builder {
            this.apiToken = apiToken
            return this
        }

        fun certificate(certificate: File): Builder {
            this.certificate = certificate
            return this;
        }

        fun adminChat(adminChat: String): Builder {
            this.adminChat = adminChat
            return this;
        }

        fun errorToken(errorToken: String): Builder {
            this.errorToken = errorToken
            return this;
        }

        fun webHook(webHook: String): Builder {
            this.webHook = webHook
            return this;
        }

        fun build(): Telegram {
            val ret = Telegram(apiToken, false)
            if (errorToken != null) {
                Telegram.errorTelegramBot = Telegram(errorToken!!, false)
            }
            if (adminChat != null) {
                ret.adminChat = adminChat!!
                ret.sendMessage {
                    recipient(adminChat!!)
                    text("booted")
                }
            }
            if (webHook != null) {
                if (certificate != null) {
                    ret.setWebhook(webHook!!, certificate!!)
                } else {
                    ret.setWebhook(webHook!!)
                }
                Thread {
                    val time = System.currentTimeMillis()
                    Thread.sleep(5 * 1000)
                    val info = ret.getWebhookInfo()
                    if (info.lastErrorTime > time) {
                        LOG.error("webhook doesn't seem to work! Defaulting to pulling")
                        ret.deleteWebhook()
                        ret.puller.start()
                    }
                }
            } else {
                ret.puller.start()
            }

            return ret

        }


    }

}

class InlineResultHandlerBuilder(val telegram: Telegram) {
    fun onResult(handler: (ChosenInlineResult) -> Unit) = telegram.inlineResultHandler(handler)
}

