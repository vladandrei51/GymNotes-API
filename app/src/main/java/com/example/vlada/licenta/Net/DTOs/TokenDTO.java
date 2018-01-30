package com.example.vlada.licenta.Net.DTOs;
import com.google.gson.annotations.SerializedName;


public class TokenDTO {
    @SerializedName("token")
    private String token;

    public TokenDTO(String token) {
        this.token = token;
    }

    public TokenDTO() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
