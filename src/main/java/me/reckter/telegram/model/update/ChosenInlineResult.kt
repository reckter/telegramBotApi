package me.reckter.telegram.model.update

import com.fasterxml.jackson.annotation.JsonProperty
import me.reckter.telegram.model.Location
import me.reckter.telegram.model.User

/**
 *  @author Hannes Güdelhöfer
 */
class ChosenInlineResult {

    @JsonProperty("result_id")
    lateinit var id: String

    lateinit var from: User

    var location: Location? = null

    @JsonProperty("inline_message_id")
    var inlineMessageId: String? = null

    lateinit var query:	String

}