package com.example.qde

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.jpush.android.api.JPushInterface
import com.example.qde.activity.MainActivity
import com.example.qde.core.BaseActivity
import com.example.qde.receiver.CactusReceiver
import com.example.qde.service.ForegroundService
import com.example.qde.utils.*
import com.gyf.cactus.Cactus
import com.gyf.cactus.callback.CactusCallback
import com.gyf.cactus.ext.cactus
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.xuexiang.xpage.PageConfig
import com.xuexiang.xrouter.launcher.XRouter
import com.xuexiang.xui.XUI
import com.xuexiang.xutil.XUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("PrivatePropertyName")
class App : Application(), CactusCallback {
    companion object {
        const val TAG: String = "QXX"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        //Cactus结束时间
        val mEndDate = MutableLiveData<String>()

        //Cactus上次存活时间
        val mLastTimer = MutableLiveData<String>()

        //Cactus存活时间
        val mTimer = MutableLiveData<String>()

        //Cactus运行状态
        val mStatus = MutableLiveData<Boolean>().apply { value = true }

        var mDisposable: Disposable? = null
    }

    override fun onCreate() {
        super.onCreate()
        try {
            context = applicationContext
            // 配置文件初始化
            SharedPreference.init(applicationContext)
            initXUI()
            initJG()
            initX5Core()

            //启动前台服务
            Intent(this, ForegroundService::class.java).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(it)
                } else {
                    startService(it)
                }
            }

            //Cactus 集成双进程前台服务，JobScheduler，onePix(一像素)，WorkManager，无声音乐
            if (SettingUtils.enableCactus) {
                //注册广播监听器
                registerReceiver(CactusReceiver(), IntentFilter().apply {
                    addAction(Cactus.CACTUS_WORK)
                    addAction(Cactus.CACTUS_STOP)
                    addAction(Cactus.CACTUS_BACKGROUND)
                    addAction(Cactus.CACTUS_FOREGROUND)
                })
                //设置通知栏点击事件
                val activityIntent = Intent(this, MainActivity::class.java)
                val flags = if (Build.VERSION.SDK_INT >= 30) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
                val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, flags)
                cactus {
                    setServiceId(FRONT_NOTIFY_ID) //服务Id
                    setChannelId(FRONT_CHANNEL_ID) //渠道Id
                    setChannelName(FRONT_CHANNEL_NAME) //渠道名
                    setTitle(getString(R.string.app_name))
                    setContent(SettingUtils.notifyContent)
                    setSmallIcon(R.mipmap.ic_launcher_foreground)
                    setLargeIcon(R.mipmap.ic_launcher)
                    setPendingIntent(pendingIntent)
                    //无声音乐
                    if (SettingUtils.enablePlaySilenceMusic) {
                        setMusicEnabled(true)
                        setBackgroundMusicEnabled(true)
                        setMusicId(R.raw.silence)
                        //设置音乐间隔时间，时间间隔越长，越省电
                        setMusicInterval(10)
                        isDebug(true)
                    }
                    //是否可以使用一像素，默认可以使用，只有在android p以下可以使用
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && SettingUtils.enableOnePixelActivity) {
                        setOnePixEnabled(true)
                    }
                    //奔溃是否可以重启用户界面
                    setCrashRestartUIEnabled(true)
                    addCallback({
                        Log.d(TAG, "Cactus保活：onStop回调")
                    }) {
                        Log.d(TAG, "Cactus保活：doWork回调")
                    }
                    //切后台切换回调
                    addBackgroundCallback {
                        Log.d(TAG, if (it) "SmsForwarder 切换到后台运行" else "SmsForwarder 切换到前台运行")
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 自动初始化X5内核
     */
    private fun initX5Core() {
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        QbSdk.setDownloadWithoutWifi(true)
        QbSdk.initX5Environment(applicationContext, object : PreInitCallback {
            /**
             * 内核初始化完成，可能为系统内核，也可能为系统内核
             */
            override fun onCoreInitFinished() {
                XToastUtils.success("内核初始化完毕")
            }

            /**
             * 预初始化结束:由于X5内核体积较大，需要依赖网络动态下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             *
             * @param isX5 是否使用X5内核
             */
            override fun onViewInitFinished(isX5: Boolean) {
                Log.e("X5 TBS", "X5内核是否成功加载= $isX5")
                if (!isX5) {
                    XToastUtils.info("非X5内核，使用系统自带的webview内核")
                }
            }
        })
        // 尝试清理所有缓存，解决开启WAF后，请求JS、CSS等静态文件需要跳转时还在解析静态资源文件内容导致浏览器报错无法自动跳转的问题(会把平台账号密码清理了。。。)
//        QbSdk.clearAllWebViewCache(getApplicationContext(), true);
    }

    private fun initXUI() {
        XUI.init(this)
        XUI.debug(true)
        PageConfig.getInstance()
                .debug(true)
                .setContainActivityClazz(BaseActivity::class.java)
                .init(this)
        XUtil.init(this)
        XUtil.debug(true)
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        XRouter.openLog() // 打印日志
        XRouter.openDebug() // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        XRouter.init(this)
    }

    /**
     * 初始化极光SDK
     */
    private fun initJG() {
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
    }


    override fun doWork(times: Int) {
        Log.d(TAG, "doWork:$times")
        mStatus.postValue(true)
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+00:00")
        var oldTimer = CactusSave.timer
        if (times == 1) {
            CactusSave.lastTimer = oldTimer
            CactusSave.endDate = CactusSave.date
            oldTimer = 0L
        }
        mLastTimer.postValue(dateFormat.format(Date(CactusSave.lastTimer * 1000)))
        mEndDate.postValue(CactusSave.endDate)
        mDisposable = Observable.interval(1, TimeUnit.SECONDS).map {
            oldTimer + it
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { aLong ->
            CactusSave.timer = aLong
            CactusSave.date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).run {
                format(Date())
            }
            mTimer.value = dateFormat.format(Date(aLong * 1000))
        }
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        mStatus.postValue(false)
        mDisposable?.apply {
            if (!isDisposed) {
                dispose()
            }
        }
    }
}