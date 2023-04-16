package com.example.qde.typer.bean;

public class SendWsBean {
    private String action;
    private String data;
    private String socketId;

    public String getSocketId() {
        return this.socketId;
    }

    public void setSocketId(String str) {
        this.socketId = str;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String str) {
        this.action = str;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String str) {
        this.data = str;
    }
}