package me.reckter.telegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hannes
 */
public class Error extends BaseModel{

    public String description;

    @JsonProperty("error_code")
    public int errorCode;

    public boolean ok;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
