package me.reckter.telegram.requests.inlineMode

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 *  @author Hannes Güdelhöfer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class InlineQueryResultAudio : InlineQueryResult() {
    override val type = InlineQueryResultType.Audio

    @JsonProperty("audio_url")
    var url: String? = null

    lateinit var title: String

    var caption: String? = null

    var performer: String? = null

    @JsonProperty("audio_duration")
    var duration: Int? = null
}