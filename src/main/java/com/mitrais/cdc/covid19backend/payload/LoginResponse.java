package com.mitrais.cdc.covid19backend.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private boolean success;
    private String message;
    private TokenPayload data;

    public LoginResponse(boolean success, String message, TokenPayload data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
