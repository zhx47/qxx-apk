package com.example.qde.typer.callback;

import okhttp3.WebSocket;

public interface WebSocketCallBack {
    void addWebSocket(String str, WebSocket webSocket);

    void sendMessage(String str, String str2, String str3);
}