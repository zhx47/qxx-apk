package com.example.qde.utils

import com.example.qde.R
import com.xuexiang.xui.utils.ResUtils.getString

class SettingUtils private constructor() {
    companion object {
        //是否转发应用通知
        var enableCactus: Boolean by SharedPreference(SP_ENABLE_CACTUS, false)

        //通知内容
        var notifyContent: String by SharedPreference(SP_NOTIFY_CONTENT, getString(R.string.notification_content))

        //是否播放静音音乐
        var enablePlaySilenceMusic: Boolean by SharedPreference(SP_ENABLE_PLAY_SILENCE_MUSIC, false)

        //是否启用1像素
        var enableOnePixelActivity: Boolean by SharedPreference(SP_ENABLE_ONE_PIXEL_ACTIVITY, false)

        //是否不在最近任务列表中显示
        var enableExcludeFromRecents: Boolean by SharedPreference(SP_ENABLE_EXCLUDE_FROM_RECENTS, false)
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}