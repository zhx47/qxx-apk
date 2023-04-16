package com.example.qde.utils;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import com.tencent.smtt.sdk.WebView;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinKeDuoHttp {
    private static Activity mActivity;
    private static WebView mWebView;

    public PinKeDuoHttp(Activity activity, WebView webView) {
        mActivity = activity;
        mWebView = webView;
    }

    public static String Login(String str, String str2, String str3, String str4, String str5, String str6) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str2).openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(6000);
        httpURLConnection.setReadTimeout(6000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setInstanceFollowRedirects(false);
        String str7 = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
        if (str2.contains("https://wap.pinke888.com/login/logins")) {
            httpURLConnection.addRequestProperty("Host", "wap.pinke888.com");
            httpURLConnection.addRequestProperty("Connection", "keep-alive");
            httpURLConnection.addRequestProperty("Pragma", "no-cache");
            httpURLConnection.addRequestProperty("Cache-Control", "no-cache");
            httpURLConnection.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            httpURLConnection.addRequestProperty("Origin", "https://wap.pinke888.com");
            httpURLConnection.addRequestProperty("X-Requested-With", "XMLHttpRequest");
            httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
            httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpURLConnection.addRequestProperty("Referer", "https://wap.pinke888.com/login/login");
            httpURLConnection.addRequestProperty("Accept-Encoding", "");
            httpURLConnection.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            httpURLConnection.addRequestProperty("Cookie", ";" + str6);
        } else {
            httpURLConnection.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            httpURLConnection.setRequestProperty("Accept-Encoding", "");
            httpURLConnection.addRequestProperty("Accept-Language", "h-CN,zh;q=0.9");
            httpURLConnection.addRequestProperty("Connection", "keep-alive");
            httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.addRequestProperty("Cookie", ";" + str6);
            httpURLConnection.addRequestProperty("Content-Length", str5.length() + "");
            httpURLConnection.addRequestProperty("Referer", str4);
            httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        }
        httpURLConnection.connect();
        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(str5.getBytes(StandardCharsets.UTF_8));
        outputStream.close();
        httpURLConnection.getResponseCode();
        Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
        if (headerFields != null && headerFields.get("Set-Cookie") != null) {
            Iterator<String> it = httpURLConnection.getHeaderFields().get("Set-Cookie").iterator();
            while (it.hasNext()) {
                String str8 = it.next().split(";")[0];
                System.out.println("loginCooike:" + str8);
                stringBuffer2.append(str8 + ";");
                it = it;
                str7 = str7;
            }
        }
        String str9 = str7;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8));
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                break;
            }
            stringBuffer.append(readLine);
        }
        String str10 = "HTTP";
        Log.d("HTTP", "Login: " + stringBuffer);
        bufferedReader.close();
        if (str2.contains("https://wap.pinke888.com/login/logins")) {
            if (stringBuffer.toString().contains("跳转中")) {
                Matcher matcher = Pattern.compile("(?<=window.location.href =\").*?(?=\"; </script>)").matcher(stringBuffer.toString());
                while (matcher.find()) {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request.Builder builder = new Request.Builder();
                    StringBuffer stringBuffer3 = stringBuffer;
                    String str11 = str10;
                    String sb = "https://wap.pinke888.com" +
                            matcher.group();
                    Response execute = okHttpClient.newCall(builder.url(sb).addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8").addHeader("Accept-Encoding", "").addHeader("Referer", str2).addHeader("Cookie", str6 + ";" + stringBuffer2).addHeader("User-Agent", str9).build()).execute();
                    String sb2 = "Login: " +
                            execute.body().string();
                    Log.d(str11, sb2);
                    stringBuffer = stringBuffer3;
                    str10 = str11;
                }
            }
            String str12 = "{\"type\":\"" + str + "\",\"cookie\":\"" + Base64.encodeToString((str6 + ";" + stringBuffer2).getBytes(), 1) + "\",\"loginJson\":" + stringBuffer + "}";
            Log.d(str10, "Login: " + str12);
            return str12;
        } else if (str2.contains("http://api.xgkst.com/api/brushuser/login")) {
            return stringBuffer.toString();
        } else {
            return stringBuffer2.toString();
        }
    }

    public void getPingKeHtml(final String str, final String str2, String str3, final String str4) {
        new OkHttpClient().newBuilder().followRedirects(false).followSslRedirects(false).build().newCall(new Request.Builder().url(str3).addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8").addHeader("Accept-Encoding", "").addHeader("Referer", str3).addHeader("Cookie", str4).addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException iOException) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String trim = response.body().string().trim();
                StringBuffer stringBuffer = new StringBuffer();
                if (str2.contains("Login1")) {
                    for (String str5 : response.headers("Set-Cookie")) {
                        Log.d("HTTP", "onResponse: " + str5);
                        stringBuffer.append(str5);
                    }
                    String replace = trim.replace("<script>", "function getJs(cookie){").replace("</script>", "").replace("while(z++)try{", "z++;").replace("eval(", "setJs('" + str + "',cookie,").replace("break}catch(_){}", " }");
                    String sb = "onResponse: " +
                            replace;
                    Log.d("HTTP", sb);
                    PinKeDuoHttp.this.runJS(replace, stringBuffer.toString());
                } else if (str2.contains("Login2")) {
                    Log.d("HTTP", "onResponse: " + trim);
                    PinKeDuoHttp.this.getPingKeCode(str, str4);
                } else if (str2.contains("Login3")) {
                    Log.d("HTTP", "onResponse: " + trim);
                    PinKeDuoHttp.this.onProxyPkdLoginCallback(PinKeDuoHttp.Login(str, "https://wap.pinke888.com/login/logins", "", "https://wap.pinke888.com/login/login", "pmd=" + str2.split(";-")[2] + "&user=" + str2.split(";-")[1], str4 + ";" + stringBuffer));
                } else if (str2.contains("Login4")) {
                    PinKeDuoHttp.Login(str, "https://wap.pinke888.com/login/logins", "", "https://wap.pinke888.com/login/login", "pmd=hdp910302&user=13799419261", str4);
                }
            }
        });
    }

    public void getPingKeCode(final String str, final String str2) {
        final String str3 = ((int) (Math.random() * 10.0d)) + "k" + ((int) (Math.random() * 10.0d)) + "k" + ((int) (Math.random() * 10.0d)) + "m";
        new OkHttpClient().newCall(new Request.Builder().url("https://wap.pinke888.com/cdn-cgi/captcha/" + str3 + "/1").addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8").addHeader("Accept-Encoding", "").addHeader("Referer", "https://wap.pinke888.com/login/login").addHeader("Cookie", str2).addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException iOException) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final byte[] bytes = response.body().bytes();
                final StringBuffer stringBuffer = new StringBuffer();
                for (String str4 : response.headers("Set-Cookie")) {
                    Log.d("HTTP", "onResponse: " + str4);
                    stringBuffer.append(str4);
                }
                PinKeDuoHttp.mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String encodeToString = Base64.encodeToString(bytes, 0);
                        PinKeDuoHttp pinKeDuoHttp = PinKeDuoHttp.this;
                        String str5 = str;
                        String str6 = str3;
                        pinKeDuoHttp.onProxyPkdCodeCallback(str5, encodeToString, str6, Base64.encodeToString((str2 + ";" + stringBuffer).getBytes(), 0));
                    }
                });
            }
        });
    }


    public void runJS(final String str, final String str2) {
        mActivity.runOnUiThread(() -> {
            WebView webView = PinKeDuoHttp.mWebView;
            webView.loadUrl("javascript: " + str);
            WebView webView2 = PinKeDuoHttp.mWebView;
            webView2.evaluateJavascript("javascript:getJs('" + str2 + "')", str3 -> {
            });
        });
    }

    private void errorToHtml(final String str) {
        mActivity.runOnUiThread(() -> {
            WebView webView = PinKeDuoHttp.mWebView;
            webView.loadUrl("javascript:errorToHtml('" + str + "')");
        });
    }


    public void onProxyPkdCodeCallback(final String str, final String str2, final String str3, final String str4) {
        mActivity.runOnUiThread(() -> {
            Log.d("HTTP", "run: " + str2);
            WebView webView = PinKeDuoHttp.mWebView;
            webView.loadUrl("javascript:onProxyPkdCodeCallback('" + ("{\"type\":\"" + str + "\",\"base64Code\":\"" + str2 + "\",\"mathCode\":\"" + str3 + "\",\"cookie\":\"" + str4 + "\"}") + "')");
        });
    }


    public void onProxyPkdLoginCallback(final String str) {
        mActivity.runOnUiThread(() -> {
            Log.d("HTTP", "Login: " + str);
            WebView webView = PinKeDuoHttp.mWebView;
            webView.loadUrl("javascript:onProxyPkdLoginCallback('" + str + "')");
        });
    }
}