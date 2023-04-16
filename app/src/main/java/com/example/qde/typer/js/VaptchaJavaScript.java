package com.example.qde.typer.js;

import android.app.Activity;
import android.app.Dialog;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import com.tencent.smtt.sdk.WebView;

public class VaptchaJavaScript {
    private static final String TAG = "VaptchaJavaScript";
    private final Activity activity;
    private final Dialog dialog;
    private final String name;
    private final String userAgent;
    private final WebView webView;

    public VaptchaJavaScript(Activity activity, WebView webView, Dialog dialog, String str, String str2) {
        this.activity = activity;
        this.webView = webView;
        this.dialog = dialog;
        this.name = str;
        this.userAgent = str2;
    }

    @JavascriptInterface
    public void log(final String str) {
        Log.d(TAG, "log: " + str);
        this.activity.runOnUiThread(() -> {
            String str2 = "{\"id\":\"" + VaptchaJavaScript.this.name + "\",\"token\":\"" + str + "\",\"userAgent\":\"" + VaptchaJavaScript.this.userAgent + "\"}";
            VaptchaJavaScript.this.webView.loadUrl("javascript:onVaptchaCallBack('" + Base64.encodeToString(str2.getBytes(), 0) + "')");
            VaptchaJavaScript.this.dialog.dismiss();
        });
    }
}