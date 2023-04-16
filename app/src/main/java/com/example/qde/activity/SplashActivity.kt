package com.example.qde.activity

import android.annotation.SuppressLint
import android.util.Log
import android.view.KeyEvent
import com.example.qde.R
import com.xuexiang.xui.utils.KeyboardUtils
import com.xuexiang.xui.widget.activity.BaseSplashActivity
import com.xuexiang.xutil.app.ActivityUtils

@Suppress("PropertyName")
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseSplashActivity() {

    val TAG: String = SplashActivity::class.java.simpleName

    override fun getSplashDurationMillis(): Long {
        return 500
    }

    /**
     * activity启动后的初始化
     */
    override fun onCreateActivity() {
        initSplashView(R.drawable.ic_launcher)
        startSplash(false)
    }

    /**
     * 启动页结束后的动作
     */
    override fun onSplashFinished() {
        Log.d(TAG, "SplashActivity onSplashFinished")
        ActivityUtils.startActivity(MainActivity::class.java)
        finish()
    }

    /**
     * 菜单、返回键响应
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return KeyboardUtils.onDisableBackKeyDown(keyCode) && super.onKeyDown(keyCode, event)
    }
}