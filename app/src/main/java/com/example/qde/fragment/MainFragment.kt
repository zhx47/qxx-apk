package com.example.qde.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.alibaba.fastjson.JSON
import com.example.qde.core.BaseFragment
import com.example.qde.databinding.FragmentMainBinding
import com.example.qde.utils.JavascriptBridge
import com.example.qde.utils.XToastUtils
import com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension
import com.tencent.smtt.sdk.*
import com.xuexiang.xpage.annotation.Page


@Page(name = "抢夕夕")
class MainFragment : BaseFragment<FragmentMainBinding?>() {

    private val TAG: String = MainFragment::class.java.simpleName

    private var uploadMessage: ValueCallback<Uri>? = null
    private var uploadMessageAboveL: ValueCallback<Array<Uri>>? = null

    override fun viewBindingInflate(inflater: LayoutInflater, container: ViewGroup): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews() {
        binding!!.webview.clearCache(true)
        WebView.setWebContentsDebuggingEnabled(true)
        with(binding!!.webview.settings) {
            javaScriptEnabled = true
            mixedContentMode = WebSettings.LOAD_NORMAL
            setAppCacheEnabled(false)
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = false
            displayZoomControls = false
            displayZoomControls = true
            userAgentString = "qxxapp;android;3.0.2;Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36;"
        }
        binding!!.webview.webChromeClient = object : WebChromeClient() {
            override fun openFileChooser(valueCallback: ValueCallback<Uri>, str: String, str2: String) {
                this@MainFragment.uploadMessage = valueCallback
                this@MainFragment.openImageChooserActivity()
            }

            override fun onShowFileChooser(
                webView: WebView, valueCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams
            ): Boolean {
                this@MainFragment.uploadMessageAboveL = valueCallback
                this@MainFragment.openImageChooserActivity()
                return true
            }
        }

        binding!!.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                val activity: FragmentActivity? = this@MainFragment.activity
                if (url.startsWith("alipays:") || url.startsWith("alipay")) {
                    try {
                        activity?.startActivity(Intent("android.intent.action.VIEW", Uri.parse(url)))
                    } catch (unused: Exception) {
                        AlertDialog.Builder(activity).setMessage("未检测到支付宝客户端，请安装后重试。")
                            .setPositiveButton(
                                "立即安装"
                            ) { _: DialogInterface?, _: Int ->
                                activity!!.startActivity(
                                    Intent("android.intent.action.VIEW", Uri.parse("https://d.alipay.com"))
                                )
                            }.setNegativeButton("取消", null).show()
                    }
                    return true
                }
                Log.d(TAG, "加载:$url")
                if (url.startsWith("http") || url.startsWith("https")) {
                    webView.loadUrl(url)
                    return true
                }
                return false
            }
        }
        binding!!.webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        // 设置JavascriptBridge
        binding!!.webview.addJavascriptInterface(JavascriptBridge(activity, binding!!.webview), "\$app")

        // 打印返回值信息

        // 打印返回值信息
        if (binding!!.webview.isX5Core) {
            val bundle = Bundle()
            binding!!.webview.x5WebViewExtension.invokeMiscMethod("setResponseHeadersCallbackEnabled", bundle)
            binding!!.webview.webViewClientExtension = object : ProxyWebViewClientExtension() {
                override fun onReportResponseHeaders(url: String, resourceType: Int, headers: HashMap<String, String>) {
                    //网络请求响应头
                    Log.d(
                        "MainActivity", """
             url:$url
             resourceType:$resourceType
             网络请求响应头:${JSON.toJSONString(headers)}
             """.trimIndent()
                    )
                    super.onReportResponseHeaders(url, resourceType, headers)
                }
            }
        }

        binding!!.webview.loadUrl("http://a.zhx47.top/index.html?r=" + System.currentTimeMillis())
    }

    /**
     * 自定义图像选择方法
     */
    fun openImageChooserActivity() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("image/*")
        startActivityForResult(Intent.createChooser(intent, "Image Chooser"), 10000)
    }


    /**
     * 重写onActivityResult方法，处理子页面返回的数据，这里处理openImageChooserActivity
     *
     * @param requestCode 是以便确认返回的数据是从哪个Activity返回的
     * @param resultCode  来标识到底是哪一个activity返回的值
     * @param intent      数据
     */
    @Deprecated("或许应该换一个方法，后续处理")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 10000) {
            if (uploadMessage == null && uploadMessageAboveL == null) {
                return
            }
            val data = if (intent == null || resultCode != -1) null else intent.data
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, intent)
                return
            }
            val valueCallback = uploadMessage
            valueCallback!!.onReceiveValue(data)
            uploadMessage = null
            return
        }
        val valueCallback2 = uploadMessage
        if (valueCallback2 != null) {
            valueCallback2.onReceiveValue(null)
            uploadMessage = null
            return
        }
        val valueCallback3: ValueCallback<Array<Uri>>? = uploadMessageAboveL
        if (valueCallback3 != null) {
            valueCallback3.onReceiveValue(null)
            uploadMessageAboveL = null
        }
    }

    /**
     * 处理结果
     *
     * @param requestCode 是以便确认返回的数据是从哪个Activity返回的
     * @param resultCode  来标识到底是哪一个activity返回的值
     * @param intent      数据
     */
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {
        var uriArr: Array<Uri>? = null
        var clipDataArr: Array<Uri>? = null
        if (requestCode != 10000 || uploadMessageAboveL == null) {
            return
        }
        if (resultCode == Activity.RESULT_OK && intent != null) {
            val dataString = intent.dataString
            val clipData = intent.clipData
            if (clipData != null) {
                clipDataArr = Array(clipData.itemCount) { i ->
                    clipData.getItemAt(i).uri
                }
            }
            uriArr = if (dataString != null) {
                arrayOf(Uri.parse(dataString))
            } else {
                clipDataArr
            }
        }
        uploadMessageAboveL?.let {
            it.onReceiveValue(uriArr)
            uploadMessageAboveL = null
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding!!.webview.canGoBack()) {
            binding!!.webview.goBack()
        } else {
            XToastUtils.info("小心退出程序")
        }
        return true
    }
}