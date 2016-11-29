package me.reckter.telegram.model.update

import com.fasterxml.jackson.annotation.JsonProperty
import me.reckter.telegram.model.BaseModel
import me.reckter.telegram.model.InlineQuery
import me.reckter.telegram.model.Message

/**
 * @author hannes
 */
class Update : BaseModel() {

    @JsonProperty("update_id")
    var id: Int = 0

    var message: Message? = null

    @JsonProperty("edited_message")
    var editedMessage: Message? = null


    @JsonProperty("callback_query")
    var callbackQuery: CallbackQuery? = null

    @JsonProperty("inline_query")
    var inlineQuery: InlineQuery? = null

    @JsonProperty("chosen_inline_result")
    var chosenInlineResult: ChosenInlineResult? = null

}
