package me.reckter.telegram

import jdk.nashorn.internal.ir.RuntimeNode
import me.reckter.telegram.model.*
import me.reckter.telegram.model.update.Update
import me.reckter.telegram.requests.*
import me.reckter.telegram.requests.inlineMode.InlineQueryAnswer
import retrofit2.Call
import retrofit2.http.*
import java.io.File

interface TelegramClient {


    @POST("sendMessage")
    fun sendMessage(@Body messageRequest: MessageRequest): Call<Response<Message>>

    @POST("forwardMessage")
    fun forwardMessage(@Body forwardMessageRequest: ForwardMessageRequest): Call<Response<Message>>

    @POST("getUpdates")
    fun getUpdates(@Body updateRequest: UpdateRequest): Call<Response<List<Update>>>

    @POST("editMessageText")
    fun editMessage(@Body updateMessageRequest: UpdateMessageRequest): Call<Any>

    @POST("sendLocation")
    fun sendLocation(@Body locationRequest: LocationRequest): Call<Response<Message>>

    @GET("getMe")
    fun getMe(): Call<Response<User>>

    @POST("sendChatAction")
    fun sendChatAction(@Body chatActionRequest: ChatActionRequest): Call<Any>

    @POST("setWebhook")
    fun setWebhook(@Body webhookRequest: WebhookRequest): Call<Any>

    @GET("getChatMember")
    fun getChatMember(@Query("chat_id") chatId: String, @Query("user_id") userId: String): Call<Response<ChatMember>>

    @GET("getChatMembersCount")
    fun getChatMembersCount(@Query("chat_id") chatId: String): Call<Response<Int>>

    @GET("getChatAdministrators")
    fun getChatAdministrators(@Query("chat_id") chatId: String): Call<Response<List<ChatMember>>>


    @POST("setWebhook")
    fun deleteWebhook(): Call<Any>

    @Multipart
    @POST("setWebhook")
    fun setWebhook(@Part("url") url: String, @Part("certificate") file: File): Call<Any>

    @GET("getWebhookInfo")
    fun getWebhookInfo(): Call<Response<WebhookInfo>>

    @POST("sendSticker")
    fun sendSticker(@Body stickerRequest: StickerRequest): Call<Response<Message>>


    @Multipart
    @POST("sendSticker")
    fun sendSticker(@Part("chat_id") chatId: String, @Part("file") file: File): Call<Response<Message>>


    @POST("answerCallbackQuery")
    fun answerCallbackQuery(@Body answerCallbackQuery: AnswerCallbackQuery): Call<Any>

    @POST("answerInlineQuery")
    fun answerInlineQuery(@Body inlineQueryAnswer: InlineQueryAnswer): Call<Any>

}
