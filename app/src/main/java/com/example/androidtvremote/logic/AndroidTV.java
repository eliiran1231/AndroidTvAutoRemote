package com.example.androidtvremote.logic;

public class AndroidTV{
    private String ip;
    private String name;
    public AndroidTV(String ip, String name){
        this.ip = ip;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }
}
