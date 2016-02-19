package me.reckter.telegram.model;


/**
 * @author hannes
 */
public class Response<T> extends BaseModel {


    private boolean ok;

    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

}
