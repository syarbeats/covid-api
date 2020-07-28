package com.mitrais.cdc.covid19backend.payload;

public class ResponseWrapper{

    private String message;
    private Object data;

    public ResponseWrapper() {
    }


    public ResponseWrapper(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


}