package me.reckter.telegram

import me.reckter.telegram.listener.OnCallBack
import me.reckter.telegram.listener.OnCommand
import me.reckter.telegram.listener.OnLocation
import me.reckter.telegram.listener.OnMessage
import me.reckter.telegram.model.Message
import me.reckter.telegram.model.update.CallbackQuery

/**
 * @author Hannes Güdelhöfer
 */
open class Test {

    lateinit var telegram: Telegram


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

    companion object {


        @JvmStatic fun main(args: Array<String>) {
            val test = Test()
            val telegram = Telegram(System.getenv("telegram.bot-token"), System.getenv("telegram.admin-chat"))
            test.telegram = telegram;
            telegram.addListener(test)

        }
    }
}
