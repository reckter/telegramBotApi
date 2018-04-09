package me.reckter.telegram.requests.inlineMode

import com.fasterxml.jackson.annotation.JsonProperty

class InlineQueryResultLocation(): InlineQueryResult() {
    override val type: InlineQueryResultType = InlineQueryResultType.Location

    var latitude: Float? = null
    var longitude: Float? = null

    lateinit var title: String

    @JsonProperty("live_period")
    var livePeriod: Int? = null

    @JsonProperty("thumb_url")
    var thumbUrl: String? = null

    @JsonProperty("thumb_width")
    var thumbWidth: Int? = null

    @JsonProperty("thumb_height")
    var thumbHeight: Int? = null
}