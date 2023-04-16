package com.example.qde.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import com.example.qde.R;
import com.xuexiang.xutil.XUtil;

public class KeepAliveUtils {
    private KeepAliveUtils() {
    }

    public static Boolean isIgnoreBatteryOptimization(Activity activity) {
        //安卓6.0以下没有忽略电池优化
        try {
            PowerManager powerManager = XUtil.getSystemService(Context.POWER_SERVICE, PowerManager.class);
            return powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
        } catch (Exception e) {
            XToastUtils.error(R.string.unsupport);
            e.printStackTrace();
            return false;
        }
    }

    public static void ignoreBatteryOptimization(Activity activity) {
        try {
            if (isIgnoreBatteryOptimization(activity)) {
                return;
            }
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            ResolveInfo resolveInfo = activity.getPackageManager().resolveActivity(intent, 0);
            if (resolveInfo != null) {
                activity.startActivity(intent);
            } else {
                XToastUtils.error(R.string.unsupport);
            }
        } catch (Exception e) {
            XToastUtils.error(R.string.unsupport);
            e.printStackTrace();
        }
    }
}
