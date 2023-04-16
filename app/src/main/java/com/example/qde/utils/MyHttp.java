package com.example.qde.utils;

import android.app.Activity;
import android.util.Log;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyHttp {
    private static final String TAG = "HTTP";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 5.1.1; Agile_Client_Error Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/39.0.0.0 Mobile Safari/537.36 Html5Plus/1.0";
    private static Activity mActivity;
    private static WebView mWebView;

    public MyHttp(Activity activity, WebView webView) {
        mActivity = activity;
        mWebView = webView;
    }

    public static String Login(String str, String str2, String str3, String str4, String str5) throws IOException {
        new StringBuffer();
        StringBuffer stringBuffer = new StringBuffer();
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(6000);
        httpURLConnection.setReadTimeout(6000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpURLConnection.setRequestProperty("Accept-Encoding", "");
        httpURLConnection.addRequestProperty("Accept-Language", "h-CN,zh;q=0.9");
        httpURLConnection.addRequestProperty("Connection", "keep-alive");
        httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.addRequestProperty("Cookie", ";" + str5);
        httpURLConnection.addRequestProperty("Content-Length", str4.length() + "");
        httpURLConnection.addRequestProperty("Referer", str3);
        httpURLConnection.addRequestProperty("User-Agent", USER_AGENT);
        httpURLConnection.connect();
        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(str4.getBytes(StandardCharsets.UTF_8));
        outputStream.close();
        httpURLConnection.getResponseCode();
        Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
        if (headerFields != null && headerFields.get("Set-Cookie") != null) {
            for (String str6 : httpURLConnection.getHeaderFields().get("Set-Cookie")) {
                String str7 = str6.split(";")[0];
                PrintStream printStream = System.out;
                printStream.println("loginCooike:" + str7);
                stringBuffer.append(str7 + ";");
            }
        }
        return stringBuffer.toString();
    }

    public void get(String str, String str2, String str3) {
        new OkHttpClient().newCall(new Request.Builder().url(str3).addHeader("Accept", "text/plain, */*; q=0.01").addHeader("Accept-Encoding", "").addHeader("Referer", str3).addHeader("User-Agent", USER_AGENT).build()).enqueue(new AnonymousClass1(str3, str2, str));
    }


    public Object post(int i, String str, String str2, String str3, String str4) throws IOException {
        Response execute = new OkHttpClient().newCall(new Request.Builder().url(str).addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").addHeader("Accept-Encoding", "").addHeader("Referer", str2).addHeader("User-Agent", USER_AGENT).addHeader("Cookie", str4).post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), str3)).build()).execute();
        if (!execute.isSuccessful()) {
            if (i == 0) {
                return "请求adbr";
            }
            return "请求Login页失败,状态码：" + execute.code();
        }
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<String> it = execute.headers("Set-Cookie").iterator();
        while (it.hasNext()) {
            stringBuffer.append(it.next() + ";");
        }
        HashMap hashMap = new HashMap();
        hashMap.put("cookie", stringBuffer.toString());
        hashMap.put("body", execute.body().string());
        return hashMap;
    }


    public void errorToHtml(final String str, final String str2) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = MyHttp.mWebView;
                webView.loadUrl("javascript:errorToHtml('" + str + "," + str2 + "')");
            }
        });
    }


    public void onProxyCallback(final String str, final String str2, final String str3) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = MyHttp.mWebView;
                webView.loadUrl("javascript:onProxyCallback('" + ("{\"id\":\"" + str + "\",\"cookie\":\"" + str2 + "\",\"referer\":\"" + str3 + "\"}") + "')");
            }
        });
    }


    public class AnonymousClass1 implements Callback {
        final String val$projectId;
        final String val$url;
        final String val$urlAction;

        AnonymousClass1(String str, String str2, String str3) {
            this.val$url = str;
            this.val$urlAction = str2;
            this.val$projectId = str3;
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            new StringBuffer();
            StringBuffer stringBuffer = new StringBuffer();
            for (String str : response.headers("Set-Cookie")) {
                Log.d("HTTP", "onResponse: " + str);
                stringBuffer.append(str);
            }
            String string = response.body().string();
            String regexMatch = HttpUtils.regexMatch(string, "action=\"", "\"");
            String regexMatch2 = HttpUtils.regexMatch(string, "name=\"jschl_vc\" value=\"", "\"");
            if (regexMatch2.contains("<!DOCTYPE HTML>")) {
                regexMatch2 = HttpUtils.regexMatch(string, "type=\"hidden\" value=\"", "\" id=\"jschl-vc\" name");
            }
            String regexMatch3 = HttpUtils.regexMatch(string, "name=\"pass\" value=\"", "\"");
            String regexMatch4 = HttpUtils.regexMatch(string, "name=\"r\" value=\"", "\"");
            Log.d("HTTP", "onResponse: " + regexMatch);
            MyHttp.mActivity.runOnUiThread(new RunnableC00091(HttpUtils.detachmentJavaScript(string, this.val$url), KuaiDuan.getParamBody(this.val$urlAction, this.val$url), regexMatch4, regexMatch2, regexMatch3, regexMatch, stringBuffer));
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException iOException) {
            MyHttp myHttp = MyHttp.this;
            String str = this.val$projectId;
            myHttp.errorToHtml(str, "解析Html错误！原因：" + iOException.getMessage());
        }

        class RunnableC00091 implements Runnable {
            final String val$action;
            final String val$adbrBody;
            final StringBuffer val$cookieBuffer;
            final String val$finalJschlVc;
            final String val$js;
            final String val$pass;
            final String val$r;

            RunnableC00091(String str, String str2, String str3, String str4, String str5, String str6, StringBuffer stringBuffer) {
                this.val$js = str;
                this.val$adbrBody = str2;
                this.val$r = str3;
                this.val$finalJschlVc = str4;
                this.val$pass = str5;
                this.val$action = str6;
                this.val$cookieBuffer = stringBuffer;
            }

            @Override
            public void run() {
                WebView webView = MyHttp.mWebView;
                webView.loadUrl("javascript:" + this.val$js);
                MyHttp.mWebView.evaluateJavascript("javascript:btest()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String str) {
                        final String substring = str.substring(1, str.length() - 1);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1500L);
                                    Object post = MyHttp.this.post(0, "https://anti-bot.baidu.com/abdr", AnonymousClass1.this.val$url, RunnableC00091.this.val$adbrBody, "");
                                    if (post.toString().contains("请求adbr失败")) {
                                        if (!AnonymousClass1.this.val$url.contains("renqi999")) {
                                            MyHttp.this.errorToHtml(AnonymousClass1.this.val$projectId, post.toString());
                                        } else {
                                            Thread.sleep(1500L);
                                            String Login = MyHttp.Login(AnonymousClass1.this.val$urlAction + RunnableC00091.this.val$action.substring(1), AnonymousClass1.this.val$url, AnonymousClass1.this.val$url, "r=" + URLEncoder.encode(RunnableC00091.this.val$r, StandardCharsets.UTF_8) + "&jschl_vc=" + URLEncoder.encode(RunnableC00091.this.val$finalJschlVc, StandardCharsets.UTF_8) + "&pass=" + URLEncoder.encode(RunnableC00091.this.val$pass, StandardCharsets.UTF_8) + "&jschl_answer=" + substring.replace("\"", ""), RunnableC00091.this.val$cookieBuffer.toString());
                                            MyHttp.this.onProxyCallback(AnonymousClass1.this.val$projectId, RunnableC00091.this.val$cookieBuffer + ";" + Login, AnonymousClass1.this.val$urlAction + RunnableC00091.this.val$action.substring(1));
                                        }
                                    } else {
                                        HashMap hashMap = (HashMap) post;
                                        if (((String) hashMap.get("cookie")).length() <= 10 || ((String) hashMap.get("body")).length() <= 20) {
                                            MyHttp.this.errorToHtml(AnonymousClass1.this.val$projectId, "未知请求响应失败");
                                        } else {
                                            JSONObject jSONObject = new JSONObject((String) hashMap.get("body"));
                                            String optString = jSONObject.optString("key_id");
                                            String optString2 = jSONObject.optString("sign");
                                            JSONObject jSONObject2 = new JSONObject(jSONObject.optString("data"));
                                            String format = String.format("__yjsv3_shitong=%s_%s_%s_%s_%s_%s_%s", jSONObject2.optString("ver"), optString, jSONObject2.optString("lid"), jSONObject2.optString("ret_code"), jSONObject2.optString("server_time"), jSONObject2.optString("ip"), optString2);
                                            Thread.sleep(1500L);
                                            String Login2 = MyHttp.Login(AnonymousClass1.this.val$urlAction + RunnableC00091.this.val$action.substring(1), AnonymousClass1.this.val$url, AnonymousClass1.this.val$url, "r=" + URLEncoder.encode(RunnableC00091.this.val$r, StandardCharsets.UTF_8) + "&jschl_vc=" + URLEncoder.encode(RunnableC00091.this.val$finalJschlVc, StandardCharsets.UTF_8) + "&pass=" + URLEncoder.encode(RunnableC00091.this.val$pass, StandardCharsets.UTF_8) + "&jschl_answer=" + substring.replace("\"", ""), format);
                                            MyHttp.this.onProxyCallback(AnonymousClass1.this.val$projectId, hashMap.get("cookie") + ";" + Login2 + ";" + format, AnonymousClass1.this.val$urlAction + RunnableC00091.this.val$action.substring(1));
                                        }
                                    }
                                } catch (IOException | InterruptedException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
            }
        }
    }
}