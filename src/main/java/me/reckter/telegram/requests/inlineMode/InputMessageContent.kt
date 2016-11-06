package me.reckter.telegram.requests.inlineMode

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import me.reckter.telegram.requests.ParseMode

/**
 *  @author Hannes Güdelhöfer
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class InputMessageContent


class InputTextMessageContent: InputMessageContent() {

    @JsonProperty("message_text")
    lateinit var text: String

    @JsonProperty("parse_mode")
    var parseMode: ParseMode? = null

    @JsonProperty("disable_web_page_preview")
    var disableWebPagePreview: Boolean? = null
}