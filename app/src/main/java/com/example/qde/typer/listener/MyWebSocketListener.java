package com.example.qde.typer.listener;

import com.example.qde.typer.callback.WebSocketCallBack;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.apache.commons.lang3.concurrent.AbstractCircuitBreaker;

public class MyWebSocketListener extends WebSocketListener {
    private final WebSocketCallBack mWebSocketCallBack;
    private final String socketId;

    public MyWebSocketListener(String str, WebSocketCallBack webSocketCallBack) {
        this.mWebSocketCallBack = webSocketCallBack;
        this.socketId = str;
    }

    @Override
    public void onClosed(WebSocket webSocket, int i, String str) {
        super.onClosed(webSocket, i, str);
        this.mWebSocketCallBack.sendMessage(this.socketId, "close", str);
    }

    @Override
    public void onClosing(WebSocket webSocket, int i, String str) {
        super.onClosing(webSocket, i, str);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable th, Response response) {
        super.onFailure(webSocket, th, response);
        this.mWebSocketCallBack.sendMessage(this.socketId, "failure", th.toString());
    }

    @Override
    public void onMessage(WebSocket webSocket, String str) {
        super.onMessage(webSocket, str);
        this.mWebSocketCallBack.sendMessage(this.socketId, "message", str);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString byteString) {
        super.onMessage(webSocket, byteString);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        this.mWebSocketCallBack.addWebSocket(this.socketId, webSocket);
        this.mWebSocketCallBack.sendMessage(this.socketId, AbstractCircuitBreaker.PROPERTY_NAME, "connect");
    }
}