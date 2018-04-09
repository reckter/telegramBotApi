package me.reckter.telegram

import me.reckter.telegram.listener.OnCallBack
import me.reckter.telegram.listener.OnCommand
import me.reckter.telegram.listener.OnDocument
import me.reckter.telegram.listener.OnLocation
import me.reckter.telegram.listener.OnMessage
import me.reckter.telegram.model.Location
import me.reckter.telegram.model.Message
import me.reckter.telegram.model.MessageType
import me.reckter.telegram.model.update.CallbackQuery
import me.reckter.telegram.requests.ChatAction
import me.reckter.telegram.requests.inlineMode.InlineQueryAnswer
import me.reckter.telegram.requests.inlineMode.InlineQueryResultLocation
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

/**
 * @author Hannes Güdelhöfer
 */
open class Test {

    lateinit var telegram: Telegram


    @OnMessage(MessageType.EDITED)
    fun messagEdited(message: Message) {

        fun sendLocation(chatId: String, location: Location): Message {
            return telegram.sendLocation(
                    chatId = chatId,
                    location = location
            )
        }
        println("reseved message update from @${message.user.username} at ${message.date}")
        if(message.location != null) {
            telegram.sendMessage(telegram.adminChat, "You were here at ${message.date}")
            sendLocation(message.chat.id, message.location!!)
            telegram.sendMessage(telegram.adminChat, "@${message.user.username} is here at ${message.date}")
            sendLocation(telegram.adminChat, message.location!!)
        }
    }

    @OnLocation
    fun lcation(message: Message) {
        message.reply("You are here: (" + message.location!!.latitude + "|" + message.location!!.longitude + ")")
    }


    @OnCallBack
    fun callBack(callbackQuery: CallbackQuery) {
        telegram.answerCallback(callbackQuery, "you just pressed " + callbackQuery.data)
    }

    @OnCommand("inline")
    fun inlineKeyboard(message: Message, args: List<String>) {

        telegram.buildMessage()
                .chat(message.chat)
                .text("test")
                .buildInlineKeyboard()
                .button("weiter", callBackData = "next")
                .nextRow()
                .button("zurück", null, "back", null)
                .button("abbrechen", null, "cancel", null)
                .nextRow()
                .button("website", "http://google.com", null, null)
                .build()
                .send()

        telegram.sendMessage {
            chat(message.chat)
            text("test2")
            buildInlineKeyboard {
                row {
                    button("weiter", callBackData = "next")
                }
                row {
                    button("zurück", null, "back", null)
                    button("abbrechen", null, "cancel", null)
                }
                row {
                    button("website", "http://google.com", null, null)
                }
            }
        }

    }

    @OnMessage(MessageType.ALL)
    fun receivePhoto(message: Message) {
        val sizes = message.photo

        if (sizes?.isNotEmpty() == true) {
            telegram.sendChatAction(message.chat.id, ChatAction.upload_photo)
            val size = sizes.maxBy { it.height * it.width }
            if (size == null) {
                message.respond("illegal file size")
                return
            }

            val file = telegram.getFile(size.id ?: "error")

            val urlString = "https://api.telegram.org/file/bot${telegram.apiKey}/${file.path}"

            val image = ImageIO.read(URL(urlString))

//            val graphics = image.createGraphics()
//            graphics.drawImage(
//                image,
//                image.width / 3 * 2,
//                image.height / 3 * 2,
//                image.width / 3,
//                image.height / 3,
//                { _, _, _, _, _, _ -> true}
//            )
//
            val output = File.createTempFile("changed", ".png")
            ImageIO.write(image, "png", output)

            telegram.sendChatAction(message.chat.id, ChatAction.upload_photo)
            telegram.uploadPhoto(
                message.chat.id,
                output,
                "created this!"
            )
            message.respond("done")
        }
    }

    @OnCommand("test")
    fun testCommand(message: Message, args: List<String>) {

        var out = "Your args are:\n"
        for (i in args.indices) {
            out += i.toString() + ": " + args[i] + "\n"
        }
        message.reply(out)
    }

    @OnMessage
    fun test(message: Message) {
        message.reply("passed!")
    }


    @OnDocument()
    fun onVideo(message: Message) {
        if (message.document != null) {
            val document = message.document!!
            if (document.mimeType.contains("video")) {
                val id = document.id ?: ""
                val message = telegram.sendDocument(message.chat.id, id, "lol")

                Thread.sleep(3000)

                telegram.editCaption(message.chat.id, message.id, "test")
            }
        }

    }


    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val test = Test()
            val telegram = Telegram.Builder()
                    .apiToken(System.getenv("telegram.bot-token"))
                    .adminChat(System.getenv("telegram.admin-chat"))
                    .build()
            test.telegram = telegram;
            telegram.addListener(test)

//            telegram.inlineQueryHandler { query ->
//                val answer = InlineQueryAnswer(query.id, cacheTime = 0)
//                (1..10).forEach {
//                    val article = InlineQueryResultArticle()
//                    article.title = "result $it"
//                    article.inputMessageContent = InputTextMessageContent().apply {
//                        text = "result $it text"
//                    }
//                    article.replyMarkup = InlineKeyboardMarkup().apply {
//                        this.inlineKeyboard = mutableListOf(mutableListOf(
//                                InlineKeyboardButton().apply {
//                                    text = "test"
//                                    callbackData = "test"
//                                }
//                        ))
//                    }
//                    article.id = it.toString()
//                    answer.results.add(article)
//
//                }
//
//                answer
//            }.onResult { result ->
//                print("result!")
//            }


            telegram.inlineQueryHandler { query ->


                InlineQueryAnswer(
                        id = query.id,
                        cacheTime = 0,
                        results = mutableListOf(
                                InlineQueryResultLocation()
                                        .apply {
                                            this.id = "test"
                                            if(query.location != null) {
                                                this.latitude = query.location?.latitude
                                                this.longitude = query.location?.longitude
                                            } else {
                                                this.latitude = 0f
                                                this.longitude = 0f
                                            }
                                            this.title = "test"
                                            this.livePeriod = 18000
                                            this.thumbUrl = "https://cdn.pixabay.com/photo/2014/04/03/10/11/exclamation-mark-310101_1280.png"
                                            this.thumbHeight = 100
                                            this.thumbWidth = 100
                                        }
                        )

                )
            }

        }
    }
}
