package me.reckter.telegram

import me.reckter.telegram.model.Chat
import me.reckter.telegram.model.ChatMember
import me.reckter.telegram.model.Message
import me.reckter.telegram.model.Response
import me.reckter.telegram.model.User
import me.reckter.telegram.model.WebhookInfo
import me.reckter.telegram.model.update.Update
import me.reckter.telegram.requests.AnswerCallbackQuery
import me.reckter.telegram.requests.ChatActionRequest
import me.reckter.telegram.requests.DocumentRequest
import me.reckter.telegram.requests.ForwardMessageRequest
import me.reckter.telegram.requests.LocationRequest
import me.reckter.telegram.requests.MessageRequest
import me.reckter.telegram.requests.PhotoRequest
import me.reckter.telegram.requests.ReplyMarkup
import me.reckter.telegram.requests.StickerRequest
import me.reckter.telegram.requests.UpdateCaptionRequest
import me.reckter.telegram.requests.UpdateMessageRequest
import me.reckter.telegram.requests.UpdateRequest
import me.reckter.telegram.requests.VideoRequest
import me.reckter.telegram.requests.WebhookRequest
import me.reckter.telegram.requests.inlineMode.InlineQueryAnswer
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.io.File

interface TelegramClient {

    @POST("sendMessage")
    fun sendMessage(@Body messageRequest: MessageRequest): Call<Response<Message>>

    @POST("sendPhoto")
    fun sendPhoto(@Body photoRequest: PhotoRequest): Call<Response<Message>>

    @POST("sendPhoto")
    @Multipart
    fun uploadPhoto(
        @Part("chat_id") chatId: String,
        @Part() photo: MultipartBody.Part,
        @Part("caption") caption: String? = null,
        @Part("disable_notification") disableNotification: Boolean? = null,
        @Part("reply_to_message_id") replyTo: Int? = null,
        @Part("reply_markup") replyMarkup: ReplyMarkup? = null
    ): Call<Response<Message>>

    @POST("sendVideo")
    fun sendVideo(@Body videoRequest: VideoRequest): Call<Response<Message>>

    @POST("sendDocument")
    fun sendDocument(@Body documentRequest: DocumentRequest): Call<Response<Message>>

    @POST("forwardMessage")
    fun forwardMessage(@Body forwardMessageRequest: ForwardMessageRequest): Call<Response<Message>>

    @POST("getUpdates")
    fun getUpdates(@Body updateRequest: UpdateRequest): Call<Response<List<Update>>>

    @POST("editMessageText")
    fun editMessage(@Body updateMessageRequest: UpdateMessageRequest): Call<Any>

    @POST("editMessageCaption")
    fun editCaption(@Body updateCaptionRequest: UpdateCaptionRequest): Call<Any>

    @POST("sendLocation")
    fun sendLocation(@Body locationRequest: LocationRequest): Call<Response<Message>>

    @GET("getMe")
    fun getMe(): Call<Response<User>>

    @GET("getFile")
    fun getFile(@Query("file_id") fileId: String): Call<Response<me.reckter.telegram.model.File>>

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

    @GET("getChat")
    fun getChat(@Query("chat_id") id: String): Call<Response<Chat>>
}
