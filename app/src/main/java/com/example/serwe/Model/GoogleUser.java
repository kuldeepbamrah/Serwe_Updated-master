package com.example.serwe.Model;

public class GoogleUser
{
    private String email;
    private String name;
    private String token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GoogleUser(String email, String name, String token) {
        this.email = email;
        this.name = name;
        this.token = token;
    }

    public GoogleUser() {
    }
}
