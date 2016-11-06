package me.reckter.telegram.requests.inlineMode

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *  @author Hannes Güdelhöfer
 */
class InlineQueryResultArticle: InlineQueryResult() {
    override val type: InlineQueryResultType = InlineQueryResultType.Article

    lateinit var title: String

    var url: String? = null
    @JsonProperty("hide_url")
    var hideUrl: Boolean? = null

    var description: String? = null

    @JsonProperty("thumb_url")
    var thumbUrl: String? = null

    @JsonProperty("thumb_width")
    var thumbWidth: Int? = null

    @JsonProperty("thumb_height")
    var thumbHeight: Int? = null
}