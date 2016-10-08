package me.reckter.telegram.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 *  @author Hannes Güdelhöfer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class AnswerCallbackQuery {

    @JsonProperty("callback_query_id")
    lateinit var queryId: String

    var text: String? = null

    @JsonProperty("show_alert")
    var showAlert: Boolean? = null

    var url: String? = null
}