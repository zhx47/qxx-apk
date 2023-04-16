package com.example.qde.service

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.qde.R
import com.example.qde.activity.MainActivity
import com.example.qde.utils.FRONT_CHANNEL_ID
import com.example.qde.utils.FRONT_CHANNEL_NAME
import com.example.qde.utils.FRONT_NOTIFY_ID
import com.example.qde.utils.SettingUtils
import io.reactivex.disposables.CompositeDisposable

@Suppress("PrivatePropertyName", "DEPRECATION")
class ForegroundService : Service() {

    private val TAG: String = "ForegroundService"
    private val compositeDisposable = CompositeDisposable()
    private var notificationManager: NotificationManager? = null

    companion object {
        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()

        try {
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            startForeground(FRONT_NOTIFY_ID, createForegroundNotification())
            isRunning = true
        } catch (e: Exception) {
            e.printStackTrace()
            isRunning = false
        }

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isRunning = true
        return START_STICKY
    }

    override fun onDestroy() {
        try {
            stopForeground(true)
            compositeDisposable.dispose()
            isRunning = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createForegroundNotification(): Notification {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(FRONT_CHANNEL_ID, FRONT_CHANNEL_NAME, importance)
            notificationChannel.description = "Frpc Foreground Service"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            if (notificationManager != null) {
                notificationManager!!.createNotificationChannel(notificationChannel)
            }
        }
        val builder = NotificationCompat.Builder(this, FRONT_CHANNEL_ID)
        builder.setSmallIcon(R.mipmap.ic_launcher_foreground)
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        // TODO: 部分机型标题会重复待排除
        // if (DeviceUtils.getDeviceBrand().contains("Xiaomi")) {
        builder.setContentTitle(getString(R.string.app_name))
        //}
        builder.setContentText(SettingUtils.notifyContent)
        builder.setWhen(System.currentTimeMillis())
        val activityIntent = Intent(this, MainActivity::class.java)
        val flags = if (Build.VERSION.SDK_INT >= 30) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, flags)
        builder.setContentIntent(pendingIntent)
        return builder.build()
    }
}