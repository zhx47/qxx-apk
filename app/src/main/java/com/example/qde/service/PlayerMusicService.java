package com.example.qde.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.example.qde.R;

public class PlayerMusicService extends Service {
    private static final String TAG = "PlayerMusicService";
    private MediaPlayer mMediaPlayer;

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "PlayerMusicService---->onCreate,启动服务");
        this.mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.no_notice);
        this.mMediaPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        new Thread(PlayerMusicService.this::startPlayMusic).start();
        return Service.START_STICKY;
    }

    public void startPlayMusic() {
        if (this.mMediaPlayer != null) {
            Log.d(TAG, "启动后台播放音乐");
            this.mMediaPlayer.start();
        }
    }

    private void stopPlayMusic() {
        if (this.mMediaPlayer != null) {
            Log.d(TAG, "关闭后台播放音乐");
            this.mMediaPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayMusic();
        Log.d(TAG, "PlayerMusicService---->onCreate,停止服务");
        startService(new Intent(getApplicationContext(), PlayerMusicService.class));
    }
}