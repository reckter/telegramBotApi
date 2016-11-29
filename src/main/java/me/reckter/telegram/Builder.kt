package me.reckter.telegram

import me.reckter.telegram.model.Chat
import me.reckter.telegram.model.Message
import me.reckter.telegram.requests.*

/**
 *  @author Hannes Güdelhöfer
 */


interface InlineBuildable<out T> {
    fun addInlineKeyboard(buttons: List<List<InlineKeyboardButton>>): T
}

class MessageBuilder(val telegram: Telegram): InlineBuildable<MessageBuilder> {
    val messageRequest = MessageRequest()


    fun recipient(id: String): MessageBuilder {
        messageRequest.id = id
        return this
    }

    fun id(id:String): MessageBuilder {
        messageRequest.id = id
        return this
    }

    fun chat(chat: Chat) = recipient(chat.id.toString())

    fun text(text: String): MessageBuilder {
        messageRequest.text = text
        return this
    }

    fun replyTo(replyTo: Int): MessageBuilder {
        messageRequest.replyTo = replyTo
        return this
    }

    fun replyTo(message: Message): MessageBuilder {
        chat(message.chat)
        return replyTo(message.id)
    }

    fun parseMode(parseMode: ParseMode): MessageBuilder {
        messageRequest.parseMode = parseMode
        return this
    }

    override fun addInlineKeyboard(buttons: List<List<InlineKeyboardButton>>): MessageBuilder {
        val keyboard = InlineKeyboardMarkup()
        keyboard.inlineKeyboard = buttons
        messageRequest.replyMarkup = keyboard
        return this
    }

    fun buildInlineKeyboard() = InlineKeyboardBuilder(this)

    fun buildInlineKeyboard(init: InlineKeyboardBuilder<MessageBuilder>.() -> Unit): MessageBuilder {
        val ret = InlineKeyboardBuilder(this)
        ret.init()
        return ret.build()
    }

    fun addReplyKeyboard(buttons: List<List<KeyboardButton>>, resizeKeyboard: Boolean? = null, oneTimeKeyboard: Boolean? = null, selective: Boolean? = null): MessageBuilder {
        val keyboard = ReplyKeyboardMarkup()
        keyboard.keyboard = buttons
        keyboard.resizeKeyboard = resizeKeyboard
        keyboard.oneTimeKeyboard = oneTimeKeyboard
        keyboard.selective = selective
        messageRequest.replyMarkup = keyboard
        return this
    }


    fun buildReplyKeyboard() = ReplyKeyboardBuilder(this)

    fun buildReplyKeyboard(init: ReplyKeyboardBuilder.() -> Unit): MessageBuilder {
        val ret = ReplyKeyboardBuilder(this)
        ret.init()
        return ret.build()
    }


    fun addHideReplyKeyboard(selective: Boolean? = null): MessageBuilder {
        val keyboard = ReplyKeyboardHide()
        keyboard.selective = selective
        messageRequest.replyMarkup = keyboard
        return this
    }

    fun addForceReply(selective: Boolean? = null): MessageBuilder {
        val keyboard = ForceReply()
        keyboard.selective = selective
        messageRequest.replyMarkup = keyboard
        return this
    }


    fun build(): MessageRequest {
        return messageRequest
    }

    fun send(): Message {
        return telegram.sendMessage(messageRequest)
    }
}


class UpdateMessageBuilder(val telegram: Telegram): InlineBuildable<UpdateMessageBuilder> {

    val messageRequest = UpdateMessageRequest()

    constructor(id: Int, chatId: String, telegram: Telegram) : this(telegram) {
        messageRequest.messageId = id.toLong()
        messageRequest.id = chatId
    }

    constructor(inlineMessageId: String, telegram: Telegram) : this(telegram) {
        messageRequest.inlineMessageId = inlineMessageId
    }



    fun text(text: String): UpdateMessageBuilder {
        messageRequest.text = text
        return this
    }

    fun parseMode(parseMode: ParseMode): UpdateMessageBuilder {
        messageRequest.parseMode = parseMode
        return this
    }

    override fun addInlineKeyboard(buttons: List<List<InlineKeyboardButton>>): UpdateMessageBuilder {
        val keyboard = InlineKeyboardMarkup()
        keyboard.inlineKeyboard = buttons
        messageRequest.replyMarkup = keyboard
        return this
    }

    fun buildInlineKeyboard() = InlineKeyboardBuilder(this)

    fun buildInlineKeyboard(init: InlineKeyboardBuilder<UpdateMessageBuilder>.() -> Unit): UpdateMessageBuilder {
        val ret = InlineKeyboardBuilder(this)
        ret.init()
        return ret.build()
    }


    fun build(): UpdateMessageRequest {
        return messageRequest
    }

    fun send() = telegram.editMessage(messageRequest)
}

class InlineKeyboardBuilder<T: InlineBuildable<T>>(val messageBuilder: T){

    val buttons = mutableListOf<MutableList<InlineKeyboardButton>>()

    fun build() = messageBuilder.addInlineKeyboard(buttons)

    fun nextRow(): InlineKeyboardBuilder<T> {
        buttons.add(mutableListOf<InlineKeyboardButton>())
        return this
    }

    fun button(text: String, url: String? = null, callBackData: String? = null, switchInlineQuery: String? = null): InlineKeyboardBuilder<T> {

        var optionalsEnabled = 0
        if(url != null) optionalsEnabled ++
        if(callBackData != null) optionalsEnabled ++
        if(switchInlineQuery!= null) optionalsEnabled ++
        if(optionalsEnabled != 1) {
            throw RuntimeException("You must specify exactly one of: url, callbackData or swichInlineQuery")
        }

        return button(InlineKeyboardButton(text, url, callBackData, switchInlineQuery))
    }

    fun button(button: InlineKeyboardButton): InlineKeyboardBuilder<T>  {
        if(buttons.isEmpty()) {
            nextRow()
        }
        buttons.last().add(button)
        return this
    }

    fun row(init: InlineKeyboardBuilder<T>.() -> Unit) {
        this.init()
        this.nextRow()
    }
}

class ReplyKeyboardBuilder(val messageBuilder: MessageBuilder) {
    fun build() = messageBuilder.addReplyKeyboard(buttons)


    val buttons = mutableListOf<MutableList<KeyboardButton>>()


    fun nextRow(): ReplyKeyboardBuilder {
        buttons.add(mutableListOf<KeyboardButton>())
        return this
    }

    fun button(text: String, requestContact: Boolean? = null, requestLocation: Boolean? = null): ReplyKeyboardBuilder {
        if(requestContact != null && requestContact && requestLocation != null && requestLocation) {
            throw RuntimeException("You can not request a Location and a Contact on the same Button!")
        }
        return button(KeyboardButton(text, requestContact, requestLocation))
    }


    fun button(button: KeyboardButton): ReplyKeyboardBuilder  {
        if(buttons.isEmpty()) {
            nextRow()
        }
        buttons.last().add(button)
        return this
    }

    fun row(init: ReplyKeyboardBuilder.() -> Unit) {
        this.init()
        this.nextRow()
    }

}