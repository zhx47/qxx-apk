package com.example.qde.fragment

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.qde.App
import com.example.qde.R
import com.example.qde.core.BaseFragment
import com.example.qde.databinding.FragmentSettingsBinding
import com.example.qde.receiver.BootReceiver
import com.example.qde.utils.KeepAliveUtils
import com.example.qde.utils.SettingUtils
import com.example.qde.utils.XToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.xuexiang.xpage.annotation.Page
import com.xuexiang.xui.widget.actionbar.TitleBar
import com.xuexiang.xui.widget.button.SmoothCheckBox
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton
import com.xuexiang.xutil.XUtil
import com.xuexiang.xutil.app.AppUtils.getAppPackageName
import java.util.*

@Suppress("PropertyName", "SpellCheckingInspection")
@Page(name = "通用设置")
class SettingsFragment : BaseFragment<FragmentSettingsBinding?>(), View.OnClickListener {

    /**
     * 日志TAG
     */
    val TAG: String = SettingsFragment::class.java.simpleName

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    fun switchEnableCactus(sbEnableCactus: SwitchButton, scbPlaySilenceMusic: SmoothCheckBox, scbOnePixelActivity: SmoothCheckBox) {
        val layoutCactusOptional: LinearLayout = binding!!.layoutCactusOptional
        val isEnable: Boolean = SettingUtils.enableCactus
        sbEnableCactus.isChecked = isEnable
        layoutCactusOptional.visibility = if (isEnable) View.VISIBLE else View.GONE

        sbEnableCactus.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            layoutCactusOptional.visibility = if (isChecked) View.VISIBLE else View.GONE
            SettingUtils.enableCactus = isChecked
            XToastUtils.warning(getString(R.string.need_to_restart))
        }

        scbPlaySilenceMusic.isChecked = SettingUtils.enablePlaySilenceMusic
        scbPlaySilenceMusic.setOnCheckedChangeListener { _: SmoothCheckBox, isChecked: Boolean ->
            SettingUtils.enablePlaySilenceMusic = isChecked
            XToastUtils.warning(getString(R.string.need_to_restart))
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            binding!!.layoutOnePixelActivity.visibility = View.VISIBLE
        }
        scbOnePixelActivity.isChecked = SettingUtils.enableOnePixelActivity
        scbOnePixelActivity.setOnCheckedChangeListener { _: SmoothCheckBox, isChecked: Boolean ->
            SettingUtils.enableOnePixelActivity = isChecked
            XToastUtils.warning(getString(R.string.need_to_restart))
        }
    }

    override fun viewBindingInflate(inflater: LayoutInflater, container: ViewGroup): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    @SuppressLint("NewApi", "SetTextI18n")
    override fun initViews() {
        //开机启动
        checkWithReboot(binding!!.sbWithReboot, binding!!.tvAutoStartup)
        //忽略电池优化设置
        batterySetting(binding!!.sbBatterySetting)
        //不在最近任务列表中显示
        switchExcludeFromRecents(binding!!.layoutExcludeFromRecents, binding!!.sbExcludeFromRecents)
        //Cactus增强保活措施
        switchEnableCactus(binding!!.sbEnableCactus, binding!!.scbPlaySilenceMusic, binding!!.scbOnePixelActivity)
        binding!!.btnMainRequestNotificationServicePermission.setOnClickListener(this)
        binding!!.btnMainRequestPostNotification.setOnClickListener(this)
        binding!!.btnMainRequestLocationPermission.setOnClickListener(this)
        binding!!.btnMainRequestReadMediaPermission.setOnClickListener(this)
        binding!!.btnMainRequestManageStoragePermission.setOnClickListener(this)
    }

    /**
     * @return 返回为 null意为不需要导航栏
     */
    override fun initTitle(): TitleBar? {
        return null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_main_request_notification_service_permission -> {
                XXPermissions.with(this)
                    .permission(Permission.NOTIFICATION_SERVICE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: List<String>, all: Boolean) {
                            if (all) {
                                XToastUtils.success(R.string.toast_granted_all)
                            } else {
                                XToastUtils.warning(R.string.toast_granted_part)
                            }
                        }

                        override fun onDenied(permissions: List<String>, never: Boolean) {
                            if (never) {
                                XToastUtils.error(R.string.toast_denied_never)
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(requireContext(), permissions)
                            } else {
                                XToastUtils.error(R.string.toast_denied)
                            }
                        }
                    })
            }
            R.id.btn_main_request_post_notification -> {
                var delayMillis: Long = 0
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    delayMillis = 2000
                    XToastUtils.normal(getString(R.string.demo_android_13_post_notification_permission_hint))
                }
                view.postDelayed({
                    XXPermissions.with(this@SettingsFragment)
                        .permission(Permission.POST_NOTIFICATIONS)
                        .request(object : OnPermissionCallback {
                            override fun onGranted(permissions: List<String>, all: Boolean) {
                                if (all) {
                                    XToastUtils.success(R.string.toast_granted_all)
                                } else {
                                    XToastUtils.warning(R.string.toast_granted_part)
                                }
                            }

                            override fun onDenied(permissions: List<String>, never: Boolean) {
                                if (never) {
                                    XToastUtils.error(R.string.toast_denied_never)
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(requireContext(), permissions)
                                } else {
                                    XToastUtils.error(R.string.toast_denied)
                                }
                            }
                        })
                }, delayMillis)
            }
            R.id.btn_main_request_location_permission -> {
                XXPermissions.with(this)
                    .permission(Permission.ACCESS_COARSE_LOCATION)
                    .permission(Permission.ACCESS_FINE_LOCATION) // 如果不需要在后台使用定位功能，请不要申请此权限
                    .permission(Permission.ACCESS_BACKGROUND_LOCATION)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: List<String>, all: Boolean) {
                            if (all) {
                                XToastUtils.success(R.string.toast_granted_all)
                            } else {
                                XToastUtils.warning(R.string.toast_granted_part)
                            }
                        }

                        override fun onDenied(permissions: List<String>, never: Boolean) {
                            if (never) {
                                XToastUtils.error(R.string.toast_denied_never)
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(requireContext(), permissions)
                            } else {
                                XToastUtils.error(R.string.toast_denied)
                            }
                        }
                    })
            }
            R.id.btn_main_request_read_media_permission -> {
                var delayMillis: Long = 0
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    delayMillis = 2000
                    XToastUtils.info(getString(R.string.demo_android_13_read_media_permission_hint))
                }
                view.postDelayed({
                    XXPermissions.with(this@SettingsFragment)
                        .permission(Permission.READ_MEDIA_IMAGES)
                        .permission(Permission.READ_MEDIA_VIDEO)
                        .permission(Permission.READ_MEDIA_AUDIO)
                        .request(object : OnPermissionCallback {
                            override fun onGranted(permissions: List<String>, all: Boolean) {
                                if (all) {
                                    XToastUtils.success(R.string.toast_granted_all)
                                } else {
                                    XToastUtils.warning(R.string.toast_granted_part)
                                }
                            }

                            override fun onDenied(permissions: List<String>, never: Boolean) {
                                if (never) {
                                    XToastUtils.error(R.string.toast_denied_never)
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(requireContext(), permissions)
                                } else {
                                    XToastUtils.error(R.string.toast_denied)
                                }
                            }
                        })
                }, delayMillis)
            }
            R.id.btn_main_request_manage_storage_permission -> {
                var delayMillis: Long = 0
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    delayMillis = 2000
                    XToastUtils.info(getString(R.string.demo_android_11_manage_storage_permission_hint))
                }
                view.postDelayed({
                    XXPermissions.with(this@SettingsFragment)
                        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                        .request(object : OnPermissionCallback {
                            override fun onGranted(permissions: List<String>, all: Boolean) {
                                if (all) {
                                    XToastUtils.success(R.string.toast_granted_all)
                                } else {
                                    XToastUtils.warning(R.string.toast_granted_part)
                                }
                            }

                            override fun onDenied(permissions: List<String>, never: Boolean) {
                                if (never) {
                                    XToastUtils.error(R.string.toast_denied_never)
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(requireContext(), permissions)
                                } else {
                                    XToastUtils.error(R.string.toast_denied)
                                }
                            }
                        })
                }, delayMillis)
            }
        }
    }

    /**
     * 开机启动
     */
    private fun checkWithReboot(@SuppressLint("UseSwitchCompatOrMaterialCode") sbWithReboot: SwitchButton, tvAutoStartup: TextView) {
        tvAutoStartup.text = getAutoStartTips()
        //获取组件
        val cm = ComponentName(getAppPackageName(), BootReceiver::class.java.name)
        val pm: PackageManager = XUtil.getPackageManager()
        val state = pm.getComponentEnabledSetting(cm)
        sbWithReboot.isChecked =
            !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER)
        sbWithReboot.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            try {
                val newState =
                    if (isChecked) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                pm.setComponentEnabledSetting(cm, newState, PackageManager.DONT_KILL_APP)
                if (isChecked) startToAutoStartSetting(requireContext())
            } catch (e: Exception) {
                XToastUtils.error(e.message.toString())
            }
        }
    }

    /**
     * 电池优化设置
     */
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    fun batterySetting(sbBatterySetting: SwitchButton) {
        //安卓6.0以下没有忽略电池优化
        try {
            val isIgnoreBatteryOptimization: Boolean = KeepAliveUtils.isIgnoreBatteryOptimization(requireActivity())
            sbBatterySetting.isChecked = isIgnoreBatteryOptimization
            sbBatterySetting.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked && !isIgnoreBatteryOptimization) {
                    KeepAliveUtils.ignoreBatteryOptimization(requireActivity())
                } else if (isChecked) {
                    XToastUtils.info(R.string.isIgnored)
                    sbBatterySetting.isChecked = true
                } else {
                    XToastUtils.info(R.string.isIgnored2)
                    sbBatterySetting.isChecked = isIgnoreBatteryOptimization
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * 不在最近任务列表中显示
     */
    @SuppressLint("ObsoleteSdkInt,UseSwitchCompatOrMaterialCode")
    fun switchExcludeFromRecents(layoutExcludeFromRecents: LinearLayout, sbExcludeFromRecents: SwitchButton) {
        //安卓6.0以下没有不在最近任务列表中显示
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            layoutExcludeFromRecents.visibility = View.GONE
            return
        }
        sbExcludeFromRecents.isChecked = SettingUtils.enableExcludeFromRecents
        sbExcludeFromRecents.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            SettingUtils.enableExcludeFromRecents = isChecked
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val am = App.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                am.let {
                    val tasks = it.appTasks
                    if (!tasks.isNullOrEmpty()) {
                        tasks[0].setExcludeFromRecents(true)
                    }
                }
            }
        }
    }

    /**
     * 获取当前手机品牌
     */
    private fun getAutoStartTips(): String {
        return when (Build.BRAND.lowercase(Locale.ROOT)) {
            "huawei" -> getString(R.string.auto_start_huawei)
            "honor" -> getString(R.string.auto_start_honor)
            "xiaomi" -> getString(R.string.auto_start_xiaomi)
            "oppo" -> getString(R.string.auto_start_oppo)
            "vivo" -> getString(R.string.auto_start_vivo)
            "meizu" -> getString(R.string.auto_start_meizu)
            "samsung" -> getString(R.string.auto_start_samsung)
            "letv" -> getString(R.string.auto_start_letv)
            "smartisan" -> getString(R.string.auto_start_smartisan)
            else -> getString(R.string.auto_start_unknown)
        }
    }

    //Intent跳转到[自启动]页面全网最全适配机型解决方案
    private val hashMap = object : HashMap<String?, List<String?>?>() {
        init {
            put(
                "Xiaomi", listOf(
                    "com.miui.securitycenter/com.miui.permcenter.autostart.AutoStartManagementActivity",  //MIUI10_9.8.1(9.0)
                    "com.miui.securitycenter"
                )
            )
            put(
                "samsung",
                listOf(
                    "com.samsung.android.sm_cn/com.samsung.android.sm.ui.ram.AutoRunActivity",
                    "com.samsung.android.sm_cn/com.samsung.android.sm.ui.appmanagement.AppManagementActivity",
                    "com.samsung.android.sm_cn/com.samsung.android.sm.ui.cstyleboard.SmartManagerDashBoardActivity",
                    "com.samsung.android.sm_cn/.ui.ram.RamActivity",
                    "com.samsung.android.sm_cn/.app.dashboard.SmartManagerDashBoardActivity",
                    "com.samsung.android.sm/com.samsung.android.sm.ui.ram.AutoRunActivity",
                    "com.samsung.android.sm/com.samsung.android.sm.ui.appmanagement.AppManagementActivity",
                    "com.samsung.android.sm/com.samsung.android.sm.ui.cstyleboard.SmartManagerDashBoardActivity",
                    "com.samsung.android.sm/.ui.ram.RamActivity",
                    "com.samsung.android.sm/.app.dashboard.SmartManagerDashBoardActivity",
                    "com.samsung.android.lool/com.samsung.android.sm.ui.battery.BatteryActivity",
                    "com.samsung.android.sm_cn",
                    "com.samsung.android.sm"
                )
            )
            put(
                "HUAWEI", listOf(
                    "com.huawei.systemmanager/.startupmgr.ui.StartupNormalAppListActivity",  //EMUI9.1.0(方舟,9.0)
                    "com.huawei.systemmanager/.appcontrol.activity.StartupAppControlActivity",
                    "com.huawei.systemmanager/.optimize.process.ProtectActivity",
                    "com.huawei.systemmanager/.optimize.bootstart.BootStartActivity",
                    "com.huawei.systemmanager" //最后一行可以写包名, 这样如果签名的类路径在某些新版本的ROM中没找到 就直接跳转到对应的安全中心/手机管家 首页.
                )
            )
            put(
                "vivo", listOf(
                    "com.iqoo.secure/.ui.phoneoptimize.BgStartUpManager",
                    "com.iqoo.secure/.safeguard.PurviewTabActivity",
                    "com.vivo.permissionmanager/.activity.BgStartUpManagerActivity",  //"com.iqoo.secure/.ui.phoneoptimize.AddWhiteListActivity", //这是白名单, 不是自启动
                    "com.iqoo.secure",
                    "com.vivo.permissionmanager"
                )
            )
            put(
                "Meizu", listOf(
                    "com.meizu.safe/.permission.SmartBGActivity",  //Flyme7.3.0(7.1.2)
                    "com.meizu.safe/.permission.PermissionMainActivity",  //网上的
                    "com.meizu.safe"
                )
            )
            put(
                "OPPO",
                listOf(
                    "com.coloros.safecenter/.startupapp.StartupAppListActivity",
                    "com.coloros.safecenter/.permission.startup.StartupAppListActivity",
                    "com.oppo.safe/.permission.startup.StartupAppListActivity",
                    "com.coloros.oppoguardelf/com.coloros.powermanager.fuelgaue.PowerUsageModelActivity",
                    "com.coloros.safecenter/com.coloros.privacypermissionsentry.PermissionTopActivity",
                    "com.coloros.safecenter",
                    "com.oppo.safe",
                    "com.coloros.oppoguardelf"
                )
            )
            put(
                "oneplus",
                listOf("com.oneplus.security/.chainlaunch.view.ChainLaunchAppListActivity", "com.oneplus.security")
            )
            put(
                "letv", listOf(
                    "com.letv.android.letvsafe/.AutobootManageActivity",
                    "com.letv.android.letvsafe/.BackgroundAppManageActivity",  //应用保护
                    "com.letv.android.letvsafe"
                )
            )
            put("zte", listOf("com.zte.heartyservice/.autorun.AppAutoRunManager", "com.zte.heartyservice"))

            //金立
            put("F", listOf("com.gionee.softmanager/.MainActivity", "com.gionee.softmanager"))

            //以下为未确定(厂商名也不确定)
            put(
                "smartisanos",
                listOf("com.smartisanos.security/.invokeHistory.InvokeHistoryActivity", "com.smartisanos.security")
            )

            //360
            put(
                "360",
                listOf(
                    "com.yulong.android.coolsafe/.ui.activity.autorun.AutoRunListActivity",
                    "com.yulong.android.coolsafe"
                )
            )

            //360
            put(
                "ulong",
                listOf(
                    "com.yulong.android.coolsafe/.ui.activity.autorun.AutoRunListActivity",
                    "com.yulong.android.coolsafe"
                )
            )

            //酷派
            put(
                "coolpad" /*厂商名称不确定是否正确*/,
                listOf(
                    "com.yulong.android.security/com.yulong.android.seccenter.tabbarmain",
                    "com.yulong.android.security"
                )
            )

            //联想
            put(
                "lenovo" /*厂商名称不确定是否正确*/,
                listOf("com.lenovo.security/.purebackground.PureBackgroundActivity", "com.lenovo.security")
            )
            put(
                "htc" /*厂商名称不确定是否正确*/,
                listOf("com.htc.pitroad/.landingpage.activity.LandingPageActivity", "com.htc.pitroad")
            )

            //华硕
            put(
                "asus" /*厂商名称不确定是否正确*/,
                listOf("com.asus.mobilemanager/.MainActivity", "com.asus.mobilemanager")
            )
        }
    }

    /**
     * 跳转自启动页面
     */
    private fun startToAutoStartSetting(context: Context) {
        Log.e("Util", "******************The current phone model is:" + Build.MANUFACTURER)
        val entries: MutableSet<MutableMap.MutableEntry<String?, List<String?>?>> = hashMap.entries
        var has = false
        for ((manufacturer, actCompatList) in entries) {
            if (Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) && actCompatList != null) {
                for (act in actCompatList) {
                    try {
                        var intent: Intent?
                        if (act?.contains("/") == true) {
                            intent = Intent()
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val componentName = ComponentName.unflattenFromString(act)
                            intent.component = componentName
                        } else {
                            //找不到? 网上的做法都是跳转到设置... 这基本上是没意义的 基本上自启动这个功能是第三方厂商自己写的安全管家类app
                            //所以我是直接跳转到对应的安全管家/安全中心
                            intent = act?.let { context.packageManager.getLaunchIntentForPackage(it) }
                        }
                        context.startActivity(intent)
                        has = true
                        break
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        if (!has) {
            XToastUtils.info(R.string.tips_compatible_solution)
            try {
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", context.packageName, null)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                val intent = Intent(Settings.ACTION_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }
}