package com.example.qde.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.qde.R;
import com.example.qde.fragment.LoginFragment;
import com.example.qde.impl.CallBackValue;
import com.example.qde.typer.bean.SendWsBean;
import com.example.qde.typer.callback.WebSocketCallBack;
import com.example.qde.typer.js.VaptchaJavaScript;
import com.example.qde.typer.listener.MyWebSocketListener;
import com.example.qde.typer.platform.WuHuaGuo;
import com.google.gson.Gson;
import com.sd.shuadan.utils.JniUtils;
import com.sky.testlibray.Afd;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavascriptBridge implements TextToSpeech.OnInitListener, WebSocketCallBack {
    private static final String TAG = "JavascriptBridge";
    public static String cookie = null;
    public static int isSpeak = 0;
    public static Dialog loginDialog = null;
    public static PinKeDuoHttp pinKeDuoHttp = null;
    public static String r = "";
    private static FragmentActivity activity = null;
    private static WebView hideWebView = null;
    private static boolean isShow = false;
    private static WebView webView;
    private final TextToSpeech tts;
    private final JSONObject timers = new JSONObject();
    private final HashMap<String, WebSocket> hashMapWebSocket = new HashMap<>();
    MediaPlayer mediaPlayer;

    public JavascriptBridge(FragmentActivity fragmentActivity, WebView webView2) {
        activity = fragmentActivity;
        webView = webView2;
        pinKeDuoHttp = new PinKeDuoHttp(fragmentActivity, webView2);
        this.tts = new TextToSpeech(fragmentActivity, this);
        hideWebView = new WebView(fragmentActivity);
        hideWebView.getSettings().setJavaScriptEnabled(true);
        hideWebView.setWebChromeClient(new WebChromeClient());
        hideWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView3, String str) {
                webView3.loadUrl(str);
                return true;
            }
        });
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", e.getMessage());
            return "";
        }
    }

    private static ByteArrayOutputStream cloneInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read > -1) {
                byteArrayOutputStream.write(bArr, 0, read);
            } else {
                byteArrayOutputStream.flush();
                return byteArrayOutputStream;
            }
        }
    }

    public static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustManagerArr = {new miTM()};
        SSLContext sSLContext = SSLContext.getInstance("SSL");
        sSLContext.init(null, trustManagerArr, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory());
    }

    public static String getFormatName(String str) {
        String[] split = str.trim().split("\\.");
        return split.length >= 2 ? split[split.length - 1] : "";
    }

    @JavascriptInterface
    public String checkVaptcha() {
        Log.d(TAG, "checkVaptcha调用");
        return "1";
    }

    @JavascriptInterface
    public String formDataCheck() {
        Log.d(TAG, "formDataCheck调用");
        return "1";
    }

    @JavascriptInterface
    public String jumpVaptchaCodePro() {
        Log.d(TAG, "jumpVaptchaCodePro调用");
        return "1";
    }

    @JavascriptInterface
    public String needCheck() {
        Log.d(TAG, "needCheck调用");
        return "1";
    }

    @JavascriptInterface
    public String needVaptcha() {
        Log.d(TAG, "needVaptcha调用");
        return "1";
    }

    @JavascriptInterface
    public String updateApk(String str) {
        Log.d(TAG, "updateApk调用");
        return "调用成功";
    }

    @JavascriptInterface
    public String updateVaptcha() {
        Log.d(TAG, "updateVaptcha调用");
        return "1";
    }

    @JavascriptInterface
    public String useVaptcha() {
        Log.d(TAG, "useVaptcha调用");
        return "1";
    }

    @JavascriptInterface
    public String verifyVaptcha() {
        Log.d(TAG, "verifyVaptcha调用");
        return "1";
    }

    @Override
    public void onInit(int i) {
        if (i == 0) {
            int language = this.tts.setLanguage(Locale.CHINESE);
            if (language == -1 || language == -2) {
                isSpeak = 0;
            } else {
                isSpeak = 1;
            }
        }
    }

    @JavascriptInterface
    public String speak(String str) {
        Log.d(TAG, "speak调用");
        if (isSpeak == 1) {
            this.tts.speak(str, 0, null);
            return "调用成功";
        }
        sound();
        return "调用成功";
    }

    @JavascriptInterface
    public void onFinishActivity() {
        Log.d(TAG, "onFinishActivity调用");
        activity.finish();
    }

    @JavascriptInterface
    public String showToast(String str) {
        XToastUtils.info(str);
        return "调用成功";
    }

    @JavascriptInterface
    public String getRegistrationID() {
        Log.d(TAG, "getRegistrationID调用");
        return "";
    }

    @JavascriptInterface
    public String getVersion() {
        Log.d(TAG, "getVersion调用");
        return getAppVersionName(activity);
    }

    @JavascriptInterface
    public String getBbbSign(String str, String str2, String str3) {
        Log.d(TAG, "getBbbSign调用");
        return Bbb.a(str, str2, str3);
    }

    @JavascriptInterface
    public String getAaaSign(String str, String str2, String str3) {
        Log.d(TAG, "getAaaSign调用");
        String rep = Afd.rep("", 0L);
        Log.d("aaa" + rep, rep);
        Afd.loadSo();
        return Afd.goj(str);
    }

    @JavascriptInterface
    public String getCccSign(String str, String str2) {
        Log.d(TAG, "getCccSign调用");
        return Bbb.cc(Bbb.cc(str.getBytes(), str2.getBytes()));
    }

    @JavascriptInterface
    public void openUrl(String str) {
        Log.d(TAG, "openUrl调用");
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(Uri.parse(str));
        activity.startActivity(intent);
    }

    @JavascriptInterface
    public void rock() {
        Log.d(TAG, "rock调用");
        ((Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(new long[]{500, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000}, -1);
    }

    @JavascriptInterface
    public void rockShort() {
        Log.d(TAG, "rockShort调用");
        ((Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(new long[]{500, 1000, 500, 1000}, -1);
    }

    @JavascriptInterface
    public void rockMiddle() {
        Log.d(TAG, "rockMiddle调用");
        ((Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(new long[]{500, 1000, 500, 1000, 500, 1000, 500, 1000}, -1);
    }

    @JavascriptInterface
    public void sound() {
        Log.d(TAG, "sound调用");
        destoryMediaPlayer();
        this.mediaPlayer = new MediaPlayer();
        try {
            this.mediaPlayer.setDataSource(activity, Uri.parse("android.resource://" + activity.getPackageName() + "/raw/" + R.raw.success));
            this.mediaPlayer.prepareAsync();
            this.mediaPlayer.setOnPreparedListener(mediaPlayer -> JavascriptBridge.this.mediaPlayer.start());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void playAudio(String str) {
        Log.d(TAG, "playAudio调用");
        destoryMediaPlayer();
        this.mediaPlayer = new MediaPlayer();
        try {
            this.mediaPlayer.setDataSource(activity, Uri.parse(str));
            this.mediaPlayer.prepareAsync();
            this.mediaPlayer.setOnPreparedListener(mediaPlayer -> JavascriptBridge.this.mediaPlayer.start());
            this.mediaPlayer.setOnErrorListener((mediaPlayer, i, i2) -> {
                Log.e(JavascriptBridge.TAG, "IOException无法播放");
                return false;
            });
        } catch (IOException e) {
            Log.e(TAG, "IOException无法播放");
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            Log.e(TAG, "IllegalArgumentException无法播放");
        } catch (Exception unused) {
            Log.e(TAG, "Exception无法播放");
        }
    }

    public void destoryMediaPlayer() {
        try {
            if (this.mediaPlayer != null) {
                this.mediaPlayer.setOnCompletionListener(null);
                this.mediaPlayer.setOnPreparedListener(null);
                this.mediaPlayer.reset();
                this.mediaPlayer.release();
                this.mediaPlayer = null;
            }
        } catch (Exception unused) {
        }
    }

    @JavascriptInterface
    public void log(String str) {
        Log.d(TAG, "log调用");
        Log.e(TAG, "rock: WebView:" + str);
    }

    @JavascriptInterface
    public void copy(String str) {
        Log.d(TAG, "copy调用");
        ((ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("text", str));
    }

    public String is2String(InputStream inputStream, String str) throws IOException {
        ByteArrayOutputStream cloneInputStream = cloneInputStream(inputStream);
        String imageType = getImageType(new ByteArrayInputStream(cloneInputStream.toByteArray()));
        if (StringUtils.isNotEmpty(imageType)) {
            String imageBase64 = getImageBase64(new ByteArrayInputStream(cloneInputStream.toByteArray()));
            return "data:" + imageType + ";base64," + imageBase64;
        }
        return getResponseStr(new ByteArrayInputStream(cloneInputStream.toByteArray()), str);
    }

    private String getResponseStr(InputStream inputStream, String str) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, str));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to close BufferedReader: " + e.getMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to close InputStream: " + e.getMessage());
                }
            }
        }
    }

    public String getImageBase64(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byte[] bArr = new byte[100];
            int read;
            while ((read = inputStream.read(bArr, 0, 100)) > 0) {
                byteArrayOutputStream.write(bArr, 0, read);
            }
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
        }
    }

    public String getImageType(InputStream inputStream) throws IOException {
        try {
            byte[] bArr = new byte[4];
            inputStream.read(bArr, 0, bArr.length);
            String upperCase = bytesToHexString(bArr).toUpperCase();
            if (upperCase.length() > 6) {
                upperCase = upperCase.substring(0, upperCase.length() - 2);
            }
            Log.e("fileHeader=", upperCase);
            String str = "";
            if (upperCase.contains("FFD8FF")) {
                str = "image/jpeg";
            } else if (upperCase.contains("89504E")) {
                str = "image/png";
            } else if (upperCase.contains("474946")) {
                str = "image/gif";
            } else if (upperCase.contains("424D")) {
                str = "application/x-bmp";
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused) {
                }
            }
            return str;
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused2) {
                }
            }
            throw th;
        }
    }

    private String bytesToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        for (byte b : bArr) {
            String upperCase = Integer.toHexString(b & 255).toUpperCase();
            if (upperCase.length() < 2) {
                sb.append(0);
            }
            sb.append(upperCase);
        }
        return sb.toString();
    }

    public boolean needStop(String str) {
        try {
            return Integer.valueOf(1).equals(this.timers.getInteger(str));
        } catch (JSONException e) {
            Log.d("json3", e.toString());
        }
        return false;
    }

    @JavascriptInterface
    public String http(final String str, final String str2, final String str3, final String str4, final String str5, final String str6, final long j) {
        Log.d(TAG, "http调用");
        new Thread(() -> {
            HttpURLConnection httpURLConnection;
            Log.d("type", str);
            Log.d("url", str2);
            Log.d("data", str3);
            Log.d("serial", str6);
            InputStream inputStream = null;
            try {
                Thread.sleep(j);
                if (JavascriptBridge.this.needStop(str6)) {
                    return;
                }
                String str7 = str3;
                URL url = new URL(str2);
                if (url.getProtocol().equalsIgnoreCase("HTTPS")) {
                    JavascriptBridge.trustAllHttpsCertificates();
                    HttpsURLConnection.setDefaultHostnameVerifier((str8, sSLSession) -> {
                        PrintStream printStream = System.out;
                        printStream.println("Warning: URL Host: " + str8 + " vs. " + sSLSession.getPeerHost());
                        return true;
                    });
                    httpURLConnection = (HttpsURLConnection) url.openConnection();
                } else {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                }
                try {
                    try {
                        httpURLConnection.setConnectTimeout(15000);
                        httpURLConnection.setReadTimeout(15000);
                        httpURLConnection.setRequestMethod(str);
                        if (str.equalsIgnoreCase("POST")) {
                            httpURLConnection.setDoOutput(true);
                            httpURLConnection.setDoInput(true);
                            httpURLConnection.setUseCaches(false);
                        }
                        if (!str4.trim().equals("")) {
                            JSONObject jSONObject = JSONObject.parseObject(str4);
                            Iterator<String> keys = jSONObject.keySet().iterator();
                            while (keys.hasNext()) {
                                String next = keys.next();
                                String string = jSONObject.getString(next);
                                Log.d(next, string);
                                httpURLConnection.setRequestProperty(next, string);
                            }
                            if ("multipart/form-data".equals(jSONObject.get("Content-Type"))) {
                                String str8 = "----WebKitFormBoundary" + UUID.randomUUID().toString();
                                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + str8);
                                StringBuffer stringBuffer = new StringBuffer();
                                JSONObject jSONObject2 = JSONObject.parseObject(str3);
                                Iterator<String> keys2 = jSONObject2.keySet().iterator();
                                while (keys2.hasNext()) {
                                    String next2 = keys2.next();
                                    String string2 = jSONObject2.getString(next2);
                                    stringBuffer.append("--");
                                    stringBuffer.append(str8);
                                    stringBuffer.append("\r\n");
                                    stringBuffer.append("Content-Disposition: form-data; name=\"" + next2 + "\"");
                                    stringBuffer.append("\r\n");
                                    stringBuffer.append("\r\n");
                                    stringBuffer.append(string2);
                                    stringBuffer.append("\r\n");
                                }
                                stringBuffer.append("--");
                                stringBuffer.append(str8);
                                stringBuffer.append("--");
                                stringBuffer.append("\r\n");
                                str7 = stringBuffer.toString();
                            }
                        }
                        httpURLConnection.setInstanceFollowRedirects(false);
                        httpURLConnection.connect();
                        if (StringUtils.isNotEmpty(str7)) {
                            Log.d("data", str7);
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8));
                            bufferedWriter.write(str7);
                            bufferedWriter.close();
                        }
                        inputStream = httpURLConnection.getResponseCode() >= 400 ? httpURLConnection.getErrorStream() : httpURLConnection.getInputStream();
                        Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
                        Set<String> keySet = headerFields.keySet();
                        HashMap hashMap = new HashMap();
                        for (String str9 : keySet) {
                            if (!StringUtils.isEmpty(str9)) {
                                ArrayList arrayList = new ArrayList();
                                for (String str10 : headerFields.get(str9)) {
                                    arrayList.add(str10.split(";")[0]);
                                }
                                hashMap.put(str9, arrayList);
                            }
                        }
                        String is2String = JavascriptBridge.this.is2String(inputStream, str5);
                        JSONObject jSONObject3 = new JSONObject();
                        jSONObject3.put("data", is2String);
                        jSONObject3.put("auth", JSONObject.toJSONString(hashMap));
                        jSONObject3.put("serial", str6);
                        String jSONObject4 = jSONObject3.toString();
                        final String encodeToString = Base64.encodeToString(jSONObject4.getBytes(StandardCharsets.UTF_8), 0);
                        Log.d("ret", jSONObject4);
                        if (!JavascriptBridge.this.needStop(str6)) {
                            JavascriptBridge.activity.runOnUiThread(() -> {
                                WebView webView2 = JavascriptBridge.webView;
                                webView2.loadUrl("javascript:httpCallBack('" + encodeToString + "')");
                            });
                            inputStream.close();
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                                return;
                            }
                            return;
                        }
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                    } catch (Throwable th) {
                        th = th;
                        if (0 != 0) {
                            try {
                                inputStream.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                                throw th;
                            }
                        }
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                        throw th;
                    }
                } catch (Exception e3) {
                    e3.printStackTrace();
                    if (JavascriptBridge.this.needStop(str6)) {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e4) {
                                e4.printStackTrace();
                                return;
                            }
                        }
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                            return;
                        }
                        return;
                    }
                    if (e3 instanceof ConnectException) {
                        JavascriptBridge.activity.runOnUiThread(() -> {
                            String encodeToString2 = Base64.encodeToString(("{\"data\" : \"ConnectException\",\"serial\" : \"" + str6 + "\"}").getBytes(), 0);
                            WebView webView2 = JavascriptBridge.webView;
                            webView2.loadUrl("javascript:httpCallBack('" + encodeToString2 + "')");
                        });
                    } else {
                        if (!(e3 instanceof UnknownHostException) && !(e3 instanceof IOException)) {
                            if (!(e3 instanceof SocketTimeoutException) && !(e3 instanceof ConnectTimeoutException)) {
                                JavascriptBridge.activity.runOnUiThread(() -> {
                                    String encodeToString2 = Base64.encodeToString(("{\"serial\" : \"" + str6 + "\"}").getBytes(), 0);
                                    WebView webView2 = JavascriptBridge.webView;
                                    webView2.loadUrl("javascript:httpCallBack('" + encodeToString2 + "')");
                                });
                            }
                            JavascriptBridge.activity.runOnUiThread(() -> {
                                String encodeToString2 = Base64.encodeToString(("{\"data\" : \"SocketTimeoutException\",\"serial\" : \"" + str6 + "\"}").getBytes(), 0);
                                WebView webView2 = JavascriptBridge.webView;
                                webView2.loadUrl("javascript:httpCallBack('" + encodeToString2 + "')");
                            });
                        }
                        JavascriptBridge.activity.runOnUiThread(() -> {
                            String encodeToString2 = Base64.encodeToString(("{\"data\" : \"UnknownHostException\",\"serial\" : \"" + str6 + "\"}").getBytes(), 0);
                            WebView webView2 = JavascriptBridge.webView;
                            webView2.loadUrl("javascript:httpCallBack('" + encodeToString2 + "')");
                        });
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            } catch (Exception e5) {
                httpURLConnection = null;
            } catch (Throwable th2) {
                httpURLConnection = null;
            }
        }).start();
        return "{}";
    }

    public void clearCookies() {
        CookieSyncManager.createInstance(activity);
        CookieManager.getInstance().removeAllCookie();
    }

    @JavascriptInterface
    public String uploadImage(String str, String str2, String str3, String str4) {
        Log.d(TAG, "uploadImage调用");
        String uuid = UUID.randomUUID().toString();
        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(str).openConnection();
            httpsURLConnection.setConnectTimeout(30000);
            httpsURLConnection.setReadTimeout(30000);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setRequestProperty("Charset", "utf-8");
            httpsURLConnection.setRequestProperty("Cookie", str4);
            httpsURLConnection.setRequestProperty("connection", "keep-alive");
            httpsURLConnection.setRequestProperty("Referer", "https://sk0606.com/webapp/faceLoginView");
            httpsURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + uuid);
            byte[] decode = Base64.decode(str2.split(",")[1], 0);
            DataOutputStream dataOutputStream = new DataOutputStream(httpsURLConnection.getOutputStream());
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("--");
            stringBuffer.append(uuid);
            stringBuffer.append("\r\n");
            stringBuffer.append("Content-Disposition: form-data; name=\"picType\"\r\n");
            stringBuffer.append("\r\n");
            stringBuffer.append("7");
            stringBuffer.append("\r\n");
            stringBuffer.append("--");
            stringBuffer.append(uuid);
            stringBuffer.append("\r\n");
            stringBuffer.append("Content-Disposition: form-data; name=\"fileName\"; filename=\"" + str3 + "\"\r\n");
            String sb = "Content-Type: image/" +
                    getFormatName(str3) +
                    "\r\n";
            stringBuffer.append(sb);
            stringBuffer.append("\r\n");
            dataOutputStream.write(stringBuffer.toString().getBytes());
            dataOutputStream.write(decode);
            dataOutputStream.write("\r\n".getBytes());
            dataOutputStream.write(("--" + uuid + "--\r\n").getBytes());
            dataOutputStream.flush();
            int responseCode = httpsURLConnection.getResponseCode();
            Log.d("uploadFile", "response code:" + responseCode);
            if (httpsURLConnection.getResponseCode() != 200) {
                return null;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            StringBuilder sb2 = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb2.append(readLine);
                    sb2.append(StringUtils.LF);
                } else {
                    Log.i("uploadFile", sb2.toString());
                    return sb2.toString();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    @JavascriptInterface
    public String recognizeCode(final String str, final String str2, final String str3) {
        Log.d(TAG, "recognizeCode调用");
        new Thread(() -> {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
                httpURLConnection.setConnectTimeout(30000);
                httpURLConnection.setReadTimeout(30000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("connection", "keep-alive");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                byte[] decode = Base64.decode(str2.split(",")[1], 0);
                DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.write(decode);
                dataOutputStream.flush();
                int responseCode = httpURLConnection.getResponseCode();
                StringBuilder sb = new StringBuilder();
                Log.d(JavascriptBridge.TAG, "response code:" + responseCode);
                if (httpURLConnection.getResponseCode() == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        sb.append(readLine);
                    }
                }
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("data", sb);
                jSONObject.put("serial", str3);
                final String encodeToString = Base64.encodeToString(jSONObject.toString().getBytes(StandardCharsets.UTF_8), 0);
                JavascriptBridge.activity.runOnUiThread(() -> {
                    WebView webView2 = JavascriptBridge.webView;
                    webView2.loadUrl("javascript:codeCallBack('" + encodeToString + "')");
                });
            } catch (Exception e) {
                e.printStackTrace();
                JavascriptBridge.activity.runOnUiThread(() -> {
                    String encodeToString2 = Base64.encodeToString(("{\"data\" : \"6789\",\"serial\" : \"" + str3 + "\"}").getBytes(), 0);
                    WebView webView2 = JavascriptBridge.webView;
                    webView2.loadUrl("javascript:codeCallBack('" + encodeToString2 + "')");
                });
            }
        }).start();
        return "{}";
    }

    @JavascriptInterface
    public void clearTimer(String str) throws JSONException {
        Log.d(TAG, "clearTimer调用");
        this.timers.put(str, 1);
    }

    @JavascriptInterface
    public String getDeviceID() {
        Log.d(TAG, "getDeviceID调用");
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method method = telephonyManager.getClass().getMethod("getImei", Integer.TYPE);
            String str = (String) method.invoke(telephonyManager, 0);
            String str2 = (String) method.invoke(telephonyManager, 1);
            if (TextUtils.isEmpty(str2)) {
                return str;
            }
            if (TextUtils.isEmpty(str)) {
                return str2;
            }
            return str + "," + str2;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return telephonyManager.getDeviceId();
            } catch (Exception unused) {
                return "";
            }
        }
    }

    @JavascriptInterface
    public String getMac() {
        Log.d(TAG, "getMac调用");
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.getName().equalsIgnoreCase("wlan0")) {
                    byte[] hardwareAddress = networkInterface.getHardwareAddress();
                    if (hardwareAddress == null) {
                        return "";
                    }
                    StringBuilder sb = new StringBuilder();
                    int length = hardwareAddress.length;
                    for (int i = 0; i < length; i++) {
                        sb.append(String.format("%02X:", Byte.valueOf(hardwareAddress[i])));
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    return sb.toString();
                }
            }
            return "02:00:00:00:00:00";
        } catch (Exception e) {
            e.printStackTrace();
            return "02:00:00:00:00:00";
        }
    }

    @JavascriptInterface
    public String getEeeSign() {
        Log.d(TAG, "getEeeSign调用");
        return Bbb.d(JniUtils.getToken(), JniUtils.getKeys(), JniUtils.getIvs());
    }

    @JavascriptInterface
    public String getYueXiangZhuan() {
        Log.d(TAG, "getYueXiangZhuan调用");
        return Bbb.d(JniUtils.getToken(), JniUtils.getKeys(), JniUtils.getIvs());
    }

    @JavascriptInterface
    public void proxyHtml(String str, String str2, String str3) {
        Log.d(TAG, "proxyHtml调用");
        new MyHttp(activity, webView).get(str, str2, str3);
    }

    @JavascriptInterface
    public String getWuHuaGuoTotal(String str) {
        Log.d(TAG, "getWuHuaGuoTotal调用");
        Log.d(TAG, "proxyHtml: " + str);
        return WuHuaGuo.encrypt(str);
    }

    @JavascriptInterface
    public void proxyPkdHtml(final String str, String str2) {
        Log.d(TAG, "proxyPkdHtml调用");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(JavascriptBridge.activity);
                WebView webView2 = new WebView(JavascriptBridge.activity);
                builder.setView(webView2);
                webView2.setVisibility(View.GONE);
                webView2.getSettings().setJavaScriptEnabled(true);
                webView2.getSettings().setDomStorageEnabled(true);
                webView2.getSettings().setUserAgent("qxxapp;android;3.0.2;Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
                webView2.getSettings().setUserAgentString("qxxapp;android;3.0.2;Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
                webView2.loadUrl("https://wap.pinke888.com/task/index");
                webView2.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView webView3, String str3) {
                        webView3.loadUrl(str3);
                        return super.shouldOverrideUrlLoading(webView3, str3);
                    }

                    @Override
                    public void onPageFinished(WebView webView3, String str3) {
                        JavascriptBridge.cookie = CookieManager.getInstance().getCookie(str3);
                        Log.d(JavascriptBridge.TAG, JavascriptBridge.cookie);
                        if (JavascriptBridge.cookie.contains("__jsl_clearance")) {
                            JavascriptBridge.pinKeDuoHttp.getPingKeCode(str, JavascriptBridge.cookie);
                            JavascriptBridge.loginDialog.dismiss();
                        }
                        super.onPageFinished(webView3, str3);
                    }
                });
                builder.setPositiveButton("获取验证码", (dialogInterface, i) -> JavascriptBridge.pinKeDuoHttp.getPingKeCode(str, JavascriptBridge.cookie));
                JavascriptBridge.loginDialog = builder.show();
            }
        });
    }

    @JavascriptInterface
    public void setJs(final String str, String str2, String str3) {
        Log.d(TAG, "setJs调用");
        Log.d(TAG, "setJs: " + str2);
        String str4 = str2.split(";")[0];
        String replaceAll = str3.replaceAll("chars='(.*)',f", "chars='JgSe0upZ%%rOm9XFMtA3QKV7nYsPGT4lifyWwkq5vcjH2IdxUoCbhERLaz81DNB6',f");
        Log.d(TAG, "setJs: " + replaceAll);
        Matcher matcher = Pattern.compile("(?<=document\\.cookie\\=).*?(?=\\};if\\(\\(function\\(\\)\\{try\\{return)").matcher(replaceAll);
        while (matcher.find()) {
            replaceAll = matcher.group();
            Log.d(TAG, "onReceiveValue: ============================================");
            Log.d(TAG, "onReceiveValue: " + replaceAll);
        }
        if (replaceAll.contains("__jsl_clearance")) {
            String finalReplaceAll = replaceAll;
            activity.runOnUiThread(() -> {
                WebView webView2 = JavascriptBridge.webView;
                webView2.evaluateJavascript("javascript: getClearance(" + finalReplaceAll + ")", str5 -> {
                    PinKeDuoHttp pinKeDuoHttp2 = JavascriptBridge.pinKeDuoHttp;
                    String str6 = str;
                    pinKeDuoHttp2.getPingKeHtml(str6, "Login2", "https://wap.pinke888.com/login/login", str5.substring(1, str5.length() - 1) + ";" + str4);
                });
            });
        }
    }

    @JavascriptInterface
    public void onAllUrlLogin(String id, String url) {
        Log.d(TAG, "onAllUrlLogin调用");
        Intent intent = new Intent(activity, LoginFragment.class);
        intent.putExtra("id", id);
        intent.putExtra("url", url);
        activity.startActivityForResult(intent, 0);
    }

    @JavascriptInterface
    public void proxyYcqHtml(final String str, final String str2, final String str3) {
        Log.d(TAG, "proxyYcqHtml调用");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ProgressDialog show = ProgressDialog.show(JavascriptBridge.activity, "提示", "正在解析赢创客（没反应请返回重试）");
                show.setCancelable(true);
                WebView webView2 = new WebView(JavascriptBridge.activity);
                webView2.setVisibility(View.GONE);
                webView2.getSettings().setJavaScriptEnabled(true);
                webView2.getSettings().setDomStorageEnabled(true);
                webView2.addJavascriptInterface(new JavaScript(webView2, str, str2, str3), "$app");
                webView2.getSettings().setUserAgent("qxxapp;android;3.0.2;Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
                webView2.getSettings().setUserAgentString("qxxapp;android;3.0.2;Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
                webView2.loadUrl("http://mobile.xgkst.com/#/login");
                webView2.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView webView3, String str4) {
                        webView3.loadUrl(str4);
                        return super.shouldOverrideUrlLoading(webView3, str4);
                    }

                    @Override
                    public void onPageFinished(WebView webView3, String str4) {
                        show.dismiss();
                        XToastUtils.info("赢创客解析成功");
                        webView3.loadUrl("javascript:grecaptcha.execute(\"6Ld-AHEUAAAAAISByIBia_8-aCo7ttIrbMVHEu8Q\", {\n\t\t\t\t\taction: \"mobileBuyerLogin\"\n\t\t\t\t}).then(function(t) {\n\t\t\t\t\twindow.$app.recaptchaRsp(t);\n\t\t\t\t})");
                        super.onPageFinished(webView3, str4);
                    }
                });
            }
        });
    }

    @JavascriptInterface
    public void openLink(String url, String title) {
        Log.d(TAG, "openLink调用");
        Log.d(TAG, url);
        ((CallBackValue) activity).initTaskActivity(title, url);
    }

    @JavascriptInterface
    public String getGggSign(String str, String str2) throws Exception {
        Log.d(TAG, "getGggSign调用");
        return Base64.encodeToString(RSAUtils.encryptData(str.getBytes(), RSAUtils.loadPublicKey(str2)), 0).replace(StringUtils.LF, "");
    }

    @JavascriptInterface
    public String getDddEnSign(String str, String str2) {
        Log.d(TAG, "getDddEnSign调用");
        return Bbb.encryptByPublic(str, str2);
    }

    @JavascriptInterface
    public String getDddDeSign(String str, String str2) {
        Log.d(TAG, "getDddDeSign调用");
        return Bbb.decryptByPublic(str, str2);
    }

    @JavascriptInterface
    public void urlCheckLogin(final String str, final String str2) {
        Log.d(TAG, "urlCheckLogin调用");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean unused = JavascriptBridge.isShow = true;
                final AlertDialog.Builder builder = new AlertDialog.Builder(JavascriptBridge.activity);
                WebView webView2 = new WebView(JavascriptBridge.activity);
                WebSettings settings = webView2.getSettings();
                settings.setJavaScriptEnabled(true);
                final String userAgentString = settings.getUserAgentString();
                settings.setBuiltInZoomControls(true);
                settings.setAppCacheEnabled(false);
                webView2.setWebChromeClient(new WebChromeClient());
                webView2.loadUrl(str2);
                builder.setView(webView2);
                webView2.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView webView3, String str3) {
                        webView3.loadUrl(str3);
                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView webView3, String str3) {
                        super.onPageFinished(webView3, str3);
                        String cookie2 = CookieManager.getInstance().getCookie(str3);
                        Log.d(JavascriptBridge.TAG, "onPageFinished: " + str3);
                        Log.d(JavascriptBridge.TAG, "onPageFinished : " + cookie2);
                        if (JavascriptBridge.isShow) {
                            JavascriptBridge.loginDialog = builder.show();
                            boolean unused2 = JavascriptBridge.isShow = false;
                        }
                        if (!JavascriptBridge.isShow && cookie2.contains("yjs_js_security_passport") && cookie2.contains("yjs_js_security_passport") && cookie2.contains("yjs_js_security_passport")) {
                            String str4 = "{\"id\":\"" + str + "\",\"cookie\":\"" + cookie2 + "\",\"userAgent\":\"" + userAgentString + "\"}";
                            JavascriptBridge.webView.loadUrl("javascript:onUrlCheckLogin('" + Base64.encodeToString(str4.getBytes(), 0) + "')");
                            JavascriptBridge.loginDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @JavascriptInterface
    public void jumpVaptchaCode(final String str, final String str2, final String str3, final String str4, final String str5, final String str6, final String str7) {
        Log.d(TAG, "jumpVaptchaCode: 你调用我");
        activity.runOnUiThread(() -> {
            Utils.synCookies(JavascriptBridge.activity, str4, str5);
            JavascriptBridge.this.showVaptchaDialog(str, str2, str3, str6, str7);
        });
    }

    public void showVaptchaDialog(final String str, String str2, String str3, final String str4, final String str5) {
        isShow = true;
        loginDialog = new Dialog(activity, R.style.LoadingDialogStyle);
        View inflate = LayoutInflater.from(activity).inflate(R.layout.dialog_vaptcha_layout, null);
        final WebView webView2 = inflate.findViewById(R.id.dialog_vaptcha_webview);
        WebSettings settings = webView2.getSettings();
        String userAgentString = settings.getUserAgentString();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        settings.setDisplayZoomControls(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        webView2.addJavascriptInterface(new VaptchaJavaScript(activity, webView, loginDialog, str, userAgentString), "$app");
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(0);
        }
        TextView textView = inflate.findViewById(R.id.dialog_vaptcha_title);
        textView.setText(str2);
        textView.setOnClickListener(view -> {
            WebView webView3 = webView2;
            webView3.loadUrl("javascript:" + (("var newscript = document.createElement(\"script\");newscript.src=\"http://127.0.0.1/AjaxHook.js\";") + "document.body.appendChild(newscript);"));
        });
        inflate.findViewById(R.id.dialog_vaptcha_cancel).setOnClickListener(view -> {
            String str6 = "{\"id\":\"" + str + "\",\"token\":\"stop\"}";
            JavascriptBridge.webView.loadUrl("javascript:onVaptchaCallBack('" + Base64.encodeToString(str6.getBytes(), 0) + "')");
            JavascriptBridge.loginDialog.dismiss();
        });
        settings.setDomStorageEnabled(true);
        webView2.setWebChromeClient(new WebChromeClient());
        webView2.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView3, String str6) {
                if (str6.contains("AjaxHook")) {
                    try {
                        return new WebResourceResponse("text/javascript", "UTF-8", JavascriptBridge.activity.getAssets().open("AjaxHook.js"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return super.shouldInterceptRequest(webView3, str6);
                    }
                } else if (str6.trim().contains(".vaptcha.net/validate?v")) {
                    return super.shouldInterceptRequest(webView3, "vaptcha.net/validate?v");
                } else {
                    return super.shouldInterceptRequest(webView3, str6);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView3, String str6) {
                webView3.loadUrl(str6);
                return true;
            }

            @Override
            public void onPageFinished(WebView webView3, String str6) {
                super.onPageFinished(webView3, str6);
                if (str5.length() > 0) {
                    webView2.loadUrl("javascript:" + str5 + ";");
                } else {
                    webView2.loadUrl("javascript: $('#ddlAccount').val('" + str4 + "');$('#autoGet').click();");
                }
                if (!str6.contains("vaptcha.com")) {
                    WebView webView4 = webView2;
                    webView4.loadUrl("javascript:" + (("var newscript = document.createElement(\"script\");newscript.src=\"http://127.0.0.1/AjaxHook.js\";") + "document.body.appendChild(newscript);"));
                }
                if (JavascriptBridge.isShow) {
                    JavascriptBridge.loginDialog.show();
                    JavascriptBridge.loginDialog.setCanceledOnTouchOutside(false);
                    boolean unused = JavascriptBridge.isShow = false;
                }
            }

            @Override
            public void onReceivedSslError(WebView webView3, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
                super.onReceivedSslError(webView3, sslErrorHandler, sslError);
            }
        });
        webView2.loadUrl(str3);
        loginDialog.setCanceledOnTouchOutside(false);
        loginDialog.setContentView(inflate, new LinearLayout.LayoutParams(-1, (int) (((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight() * 0.9d)));
        Window window = loginDialog.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.height = -2;
        window.setGravity(17);
        window.setAttributes(attributes);
        window.setWindowAnimations(R.style.PopWindowAnimStyle);
    }

    @JavascriptInterface
    public void createWebSocket(String str, String str2) {
        Log.d(TAG, "createWebSocket调用");
        new OkHttpClient.Builder().connectTimeout(6L, TimeUnit.SECONDS).readTimeout(6L, TimeUnit.SECONDS).build().newWebSocket(new Request.Builder().url(str2).build(), new MyWebSocketListener(str, this));
    }

    @JavascriptInterface
    public void webSocketSendMessage(String str, String str2) {
        Log.d(TAG, "webSocketSendMessage调用");
        this.hashMapWebSocket.get(str).send(str2);
    }

    @JavascriptInterface
    public void webSocketClose(String str) {
        Log.d(TAG, "webSocketClose调用");
        this.hashMapWebSocket.get(str).close(1000, "user exit");
        this.hashMapWebSocket.remove(str);
    }

    @Override
    @JavascriptInterface
    public void addWebSocket(String str, WebSocket webSocket) {
        this.hashMapWebSocket.put(str, webSocket);
    }

    @Override
    @JavascriptInterface
    public void sendMessage(String str, String str2, String str3) {
        SendWsBean sendWsBean = new SendWsBean();
        sendWsBean.setSocketId(str);
        sendWsBean.setAction(str2);
        sendWsBean.setData(str3);
        String json = new Gson().toJson(sendWsBean);
        webView.post(() -> JavascriptBridge.webView.loadUrl("javascript:onWebsocketCallback('" + Base64.encodeToString(json.getBytes(), 0) + "')"));
    }

    @JavascriptInterface
    public void savaData(String str, String str2) {
        Log.d(TAG, "savaData调用");
        SharedPreferences.Editor edit = SharedPreference.preference.edit();
        edit.putString(str, str2);
        edit.commit();
    }

    @JavascriptInterface
    public String getData(String str) {
        Log.d(TAG, "getData调用");
        androidx.fragment.app.FragmentActivity fragmentActivity = activity;
        return fragmentActivity.getSharedPreferences(fragmentActivity.getString(R.string.account_data), 0).getString(str, "");
    }

    @JavascriptInterface
    public String getTaoFaXiangDataSign(String str) {
        Log.d(TAG, "getTaoFaXiangDataSign调用");
        return TaoFaXiangUtils.encryptByPublic(str);
    }

    @JavascriptInterface
    public void onXiaoFeiZhuInit() {
        Log.d(TAG, "onXiaoFeiZhuInit调用");
        activity.runOnUiThread(() -> JavascriptBridge.hideWebView.loadUrl("http://mm.yidiangong.com/sfUser/login/2147483615"));
    }

    @JavascriptInterface
    public void getXiaoFeiZhuIK(final String str) {
        Log.d(TAG, "getXiaoFeiZhuIK调用");
        activity.runOnUiThread(() -> JavascriptBridge.hideWebView.evaluateJavascript("javascript:(function(){return window.navigator.localenc;})()", str2 -> {
            WebView webView2 = JavascriptBridge.webView;
            webView2.loadUrl("javascript:onXiaoFeiZhuCallBack('" + Base64.encodeToString(str2.getBytes(), 0) + "','" + str + "')");
        }));
    }

    public static class miTM implements TrustManager, X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isClientTrusted(X509Certificate[] x509CertificateArr) {
            return true;
        }

        public boolean isServerTrusted(X509Certificate[] x509CertificateArr) {
            return true;
        }
    }

    private static class Capture {
        private static String d;
        private static String u;

        private Capture() {
        }
    }

    public class JavaScript {
        private String id;
        private WebView mJavaScriptWebView;
        private String phone;
        private String pwd;

        public JavaScript(WebView webView, String str, String str2, String str3) {
            this.phone = null;
            this.pwd = null;
            this.id = null;
            this.mJavaScriptWebView = webView;
            this.phone = str2;
            this.pwd = str3;
            this.id = str;
        }

        @JavascriptInterface
        public void recaptchaRsp(final String str) {
            Log.d(TAG, "recaptchaRsp调用");
            Log.d(JavascriptBridge.TAG, "recaptchaRsp: " + str);
            new Thread(() -> {
                try {
                    final String Login = PinKeDuoHttp.Login(JavaScript.this.id, "http://api.xgkst.com/api/brushuser/login", "http://mobile.xgkst.com/", "http://mobile.xgkst.com/", "phone=" + JavaScript.this.phone + "&password=" + JavaScript.this.pwd + "&recaptchaRsp=" + str, "");
                    JavascriptBridge.activity.runOnUiThread(() -> {
                        Log.d(JavascriptBridge.TAG, "run: " + Login);
                        WebView webView = JavascriptBridge.webView;
                        webView.loadUrl("javascript:onProxyYckCallback('" + JavaScript.this.id + "','" + Base64.encodeToString(Login.getBytes(), 1) + "')");
                    });
                    JavaScript.this.mJavaScriptWebView = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}