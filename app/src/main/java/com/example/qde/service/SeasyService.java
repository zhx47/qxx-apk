package com.example.qde.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class SeasyService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        startForeground(TbsLog.TBSLOG_CODE_SDK_INIT, new NotificationCompat.Builder(this).build());
        startForeground(999, new NotificationCompat.Builder(this).build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}