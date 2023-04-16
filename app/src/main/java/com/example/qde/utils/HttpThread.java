package com.example.qde.utils;

import android.os.Handler;
import android.os.Message;

class HttpThread extends Thread {
    private final Handler handler;
    String contentType;
    String data;
    String type;
    String url;

    public HttpThread(Handler handler, String str, String str2, String str3, String str4) {
        this.type = str;
        this.url = str2;
        this.data = str3;
        this.contentType = str4;
        this.handler = handler;
    }

    @Override
    public void run() {
        Message obtainMessage = this.handler.obtainMessage();
        try {
            try {
                obtainMessage.what = 200;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            this.handler.sendMessage(obtainMessage);
        }
    }
}