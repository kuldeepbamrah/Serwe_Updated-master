package com.example.serwe.Model;

import android.net.Uri;

public class GoogleUser
{
    private String email;
    private String name;
    private String token;
    private Uri img;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public Uri getImg() {
        return img;
    }

    public void setImg(Uri img) {
        this.img = img;
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

    public GoogleUser(String email, String name, String token, Uri img) {
        this.email = email;
        this.name = name;
        this.token = token;
        this.img = img;
    }

    public GoogleUser() {
    }
}
