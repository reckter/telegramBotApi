package me.reckter.telegram.requests;

/**
 * Created by hannes on 12.02.16.
 */
public class UpdateRequest {

    long offset;

    long limit;

    long timeout;


    public UpdateRequest() {
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
