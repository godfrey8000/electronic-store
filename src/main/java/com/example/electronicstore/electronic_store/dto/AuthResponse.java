package com.example.electronicstore.electronic_store.dto;

import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private String expireDay;
    public AuthResponse(String token, String expireDay)
    {
        this.token = token;
        this.expireDay = expireDay;
    }
}