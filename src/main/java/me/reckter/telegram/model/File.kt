package me.reckter.telegram.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author hannes
 */
open class File : BaseModel() {

    @JsonProperty("file_id")
    var id: String? = null

    @JsonProperty("file_size")
    var size: Int = 0

    @JsonProperty("file_path")
    var path: String? = null
}
