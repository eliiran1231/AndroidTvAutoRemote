package com.example.androidtvremote.logic;

import android.graphics.drawable.Drawable;

import java.nio.charset.StandardCharsets;

public class App {
    private int image;
    private String name;
    private String content;

    public App(int image, String name, String link){
        this.image = image;
        this.name = name;
        this.content = link;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }
}

