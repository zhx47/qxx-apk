package com.example.qde.utils;

import android.content.Context;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {
    public static void synCookies(Context context, String str, String str2) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        for (String str3 : str2.split(";")) {
            cookieManager.setCookie(str, str3);
        }
        cookieManager.setCookie(str, "domain=" + str);
        cookieManager.setCookie(str, "path=" + str);
        CookieSyncManager.getInstance().sync();
    }

    public static String getVaptchaJs(Context context) throws IOException {
        InputStream open = context.getAssets().open("AjaxHook.js");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(open));
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                stringBuffer.append(readLine);
            } else {
                open.close();
                bufferedReader.close();
                return stringBuffer.toString();
            }
        }
    }
}