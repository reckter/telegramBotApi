package me.reckter.telegram.listener

import me.reckter.telegram.model.update.CallbackQuery

/**
 *  @author Hannes Güdelhöfer
 */
interface CallBackListener {

    fun OnCallBack(callbackQuery: CallbackQuery)
}