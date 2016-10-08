package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Hannes Güdelhöfer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForceReply extends ReplyMarkup {

    @JsonProperty("force_reply")
    boolean forceReply = true;


    Boolean selective;


    public Boolean getSelective() {
        return selective;
    }

    public void setSelective(Boolean selective) {
        this.selective = selective;
    }
}
