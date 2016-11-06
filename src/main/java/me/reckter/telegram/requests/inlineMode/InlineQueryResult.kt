package me.reckter.telegram.requests.inlineMode

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import me.reckter.telegram.requests.InlineKeyboardMarkup

/**
 *  @author Hannes Güdelhöfer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class InlineQueryResult {

    lateinit var id: String

    abstract val type: InlineQueryResultType

    @JsonProperty("input_message_content")
    var inputMessageContent: InputMessageContent? = null


    @JsonProperty("reply_markup")
    var replyMarkup: InlineKeyboardMarkup? = null
}

enum class InlineQueryResultType {
    CachedAudio,
    CachedDocument,
    CachedGif,
    CachedMpeg4Gif,
    CachedPhoto,
    CachedSticker,
    CachedVideo,
    CachedVoice,
    Article,
    Audio,
    Contact,
    Game,
    Document,
    Gif,
    Location,
    Mpeg4Gif,
    Photo,
    Venue,
    Video,
    Voice
}