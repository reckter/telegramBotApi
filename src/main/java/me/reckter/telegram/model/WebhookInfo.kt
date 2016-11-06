package me.reckter.telegram.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *  @author Hannes Güdelhöfer
 */
class WebhookInfo {

    lateinit var url: String

    @JsonProperty("has_custom_certificate")
    var hasCustomCertificate: Boolean = false

    @JsonProperty("pending_update_count")
    var pendingUpdates: Int = 0

    @JsonProperty("last_error_date")
    var lastErrorTime: Int = 0

    @JsonProperty("last_error_message")
    var lastErrorMessage: String? = null

}