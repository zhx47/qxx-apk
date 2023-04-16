package com.example.qde.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.qde.core.BaseFragment
import com.example.qde.databinding.FragmentTaskBinding
import com.example.qde.impl.CallBackValue
import com.example.qde.utils.XToastUtils
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.xuexiang.xpage.annotation.Page
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


@Suppress("SpellCheckingInspection")
@Page(name = "做单大厅")
class TaskFragment : BaseFragment<FragmentTaskBinding?>() {

    private var uploadMessage: ValueCallback<Uri>? = null
    private var uploadMessageAboveL: ValueCallback<Array<Uri>>? = null

    override fun viewBindingInflate(inflater: LayoutInflater, container: ViewGroup): FragmentTaskBinding {
        return FragmentTaskBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        binding!!.activityWebviewToolbar.title = "任务大厅"
        with(binding!!.taskWebview.settings) {
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = false
            displayZoomControls = false
            domStorageEnabled = true
            javaScriptEnabled = true
            mixedContentMode = 0
        }
        binding!!.taskWebview.webChromeClient = object : WebChromeClient() {
            override fun openFileChooser(
                valueCallback: ValueCallback<Uri>, str: String, str2: String
            ) {
                val webViewActivity: TaskFragment = this@TaskFragment
                webViewActivity.uploadMessage = valueCallback
                webViewActivity.openImageChooserActivity()
            }

            override fun onShowFileChooser(
                webView: WebView,
                valueCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                val webViewActivity: TaskFragment = this@TaskFragment
                webViewActivity.uploadMessageAboveL = valueCallback
                webViewActivity.openImageChooserActivity()
                return true
            }
        }
        binding!!.taskWebview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, str: String?): Boolean {
                webView.loadUrl(str)
                return true
            }

            override fun onReceivedSslError(webView: WebView?, sslErrorHandler: SslErrorHandler, sslError: SslError?) {
                sslErrorHandler.proceed()
                super.onReceivedSslError(webView, sslErrorHandler, sslError)
            }
        }
        binding!!.taskWebview.setOnLongClickListener { _ ->
            val hitTestResult: WebView.HitTestResult =
                binding!!.taskWebview.hitTestResult
            if (hitTestResult.type == 5 || hitTestResult.type == 8) {
                val extra = hitTestResult.extra
                Thread { this@TaskFragment.imgToBit(extra) }.start()
                return@setOnLongClickListener true
            }
            false
        }

        binding!!.activityWebviewToolbar.setNavigationOnClickListener { _ -> (this@TaskFragment.activity as CallBackValue).initMainActivity() }
    }

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

    private fun saveBitmap(bitmap: Bitmap) {
        try {
            val path =
                Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Screenshots/" + System.currentTimeMillis() + ".jpg"
            XToastUtils.success("保存到$path")
            val file2 = File(path)
            val fileOutputStream = FileOutputStream(file2)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            val fromFile = Uri.fromFile(file2)
            val intent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
            intent.setData(fromFile)
            requireActivity().sendBroadcast(intent)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e2: IOException) {
            e2.printStackTrace()
        }
    }

    private fun imgToBit(str: String?) {
        try {
            val httpURLConnection = URL(str).openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.connectTimeout = 5000
            val inputStream = httpURLConnection.inputStream
            val decodeStream = BitmapFactory.decodeStream(inputStream)
            if (decodeStream != null) {
                saveBitmap(decodeStream)
            }
            inputStream.close()
        } catch (e: Exception) {
            requireActivity().runOnUiThread {
                XToastUtils.error("读取失败")
            }
            e.printStackTrace()
        }
    }

    fun init(title: String?, url: String?) {
        binding!!.taskWebview.post { binding!!.taskWebview.loadUrl(url) }
        binding!!.activityWebviewToolbar.title = title
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding!!.taskWebview.canGoBack()) {
            binding!!.taskWebview.goBack()
        } else if (activity is CallBackValue) {
            (activity as CallBackValue?)!!.initMainActivity()
        }
        return true
    }
}