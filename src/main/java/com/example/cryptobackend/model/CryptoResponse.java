package com.example.cryptobackend.model;

public class CryptoResponse {
    private String result;
    private String error;

    // Constructors
    public CryptoResponse() {
    }

    public CryptoResponse(String result) {
        this.result = result;
    }

    public CryptoResponse(String result, String error) {
        this.result = result;
        this.error = error;
    }

    // Getters and setters
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}