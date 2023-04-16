package com.example.qde.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.qde.R;
import com.tencent.smtt.sdk.*;

/**
 * 由onAllUrlLogin方法调用，暂时未找到调用方法。。。
 */
public class LoginFragment extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ActivityLogin";
    private String cookie = null;
    private String id = null;
    private String userAgent = null;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.fragment_login);
        Intent intent = getIntent();
        this.id = intent.getStringExtra("id");
        Toolbar mToolbar = findViewById(R.id.activity_login_toolbar);
        WebView mWebView = findViewById(R.id.activity_login_webview);
        findViewById(R.id.button6).setOnClickListener(this);
        setSupportActionBar(mToolbar);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        this.userAgent = settings.getUserAgentString();
        Log.d(TAG, "onCreate: " + settings.getUserAgentString());
        settings.setBuiltInZoomControls(true);
        settings.setAppCacheEnabled(false);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl(intent.getStringExtra("url"));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                webView.loadUrl(str);
                return true;
            }

            @Override
            public void onPageFinished(WebView webView, String str) {
                super.onPageFinished(webView, str);
                CookieManager cookieManager = CookieManager.getInstance();
                LoginFragment.this.cookie = cookieManager.getCookie(str);
                Log.d(TAG, "onPageFinished : " + LoginFragment.this.cookie);
                Log.d(TAG, "onPageFinished: " + str);
            }
        });
        mToolbar.setNavigationOnClickListener(view -> {
            LoginFragment.this.setResult(1, new Intent(LoginFragment.this, MainFragment.class));
            LoginFragment.this.finish();
        });
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId != R.id.button6) {
            return;
        }
        Intent intent = new Intent(this, MainFragment.class);
        intent.putExtra("id", this.id);
        intent.putExtra("cookie", this.cookie);
        intent.putExtra("userAgent", this.userAgent);
        setResult(0, intent);
        finish();
    }
}