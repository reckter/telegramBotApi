package me.reckter.telegram.requests.inlineMode

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 *  @author Hannes Güdelhöfer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class InlineQueryAnswer(

    @field:JsonProperty("inline_query_id")
    val id: String,

    val results: MutableList<InlineQueryResult> = mutableListOf(),

    @field:JsonProperty("cache_time")
    var cacheTime: Int? = null,

    @field:JsonProperty("is_personal")
    var isPersonal: Boolean? = null,

    @field:JsonProperty("next_offset")
    var nextOffset: String? = null,

    @field:JsonProperty("switch_pm_text")
    var switchPmText: String? = null,

    @field:JsonProperty("switch_pm_parameter")
    var switchPmParameter: String? = null

)