package com.example.qde.activity

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import com.example.qde.App
import com.example.qde.R
import com.example.qde.core.BaseActivity
import com.example.qde.databinding.ActivityMainBinding
import com.example.qde.fragment.MainFragment
import com.example.qde.fragment.SettingsFragment
import com.example.qde.fragment.TaskFragment
import com.example.qde.impl.CallBackValue
import com.example.qde.service.ForegroundService
import com.example.qde.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.xuexiang.xpage.base.XPageFragment
import com.xuexiang.xpage.core.PageOption
import com.xuexiang.xpage.model.PageInfo
import com.xuexiang.xui.adapter.FragmentAdapter
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder
import com.xuexiang.xui.utils.ResUtils
import com.xuexiang.xui.utils.WidgetUtils
import java.io.File


@Suppress("DEPRECATION", "PrivatePropertyName")
class MainActivity : BaseActivity<ActivityMainBinding?>(),
    View.OnClickListener,
    BottomNavigationView.OnNavigationItemSelectedListener,
    Toolbar.OnMenuItemClickListener,
    RecyclerViewHolder.OnItemClickListener<PageInfo>,
    CallBackValue {

    private val TAG: String = MainActivity::class.java.simpleName
    private lateinit var mTitles: Array<String>

    private var taskFragment: TaskFragment? = null
    private var mainFragment: MainFragment? = null
    private var settingsFragment: SettingsFragment? = null

    override fun viewBindingInflate(inflater: LayoutInflater?): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity onCreate")
        initViews()
        initListeners()

        //不在最近任务列表中显示
        if (SettingUtils.enableExcludeFromRecents) {
            val am = App.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.let {
                val tasks = it.appTasks
                if (!tasks.isNullOrEmpty()) {
                    tasks[0].setExcludeFromRecents(true)
                }
            }
        }

        //检查通知权限是否获取
        XXPermissions.with(this)
            .permission(Permission.NOTIFICATION_SERVICE)
            .permission(Permission.POST_NOTIFICATIONS)
            .request(OnPermissionCallback { _, allGranted ->
                if (!allGranted) {
                    XToastUtils.error(R.string.tips_notification)
                    return@OnPermissionCallback
                }

                //启动前台服务
                if (!ForegroundService.isRunning) {
                    Intent(this, ForegroundService::class.java).also {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(it)
                        } else {
                            startService(it)
                        }
                    }
                }
            })

        val file = File(Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Screenshots")
        if (file.exists()) {
            return
        }
        file.mkdirs()
    }

    override val isSupportSlideBack: Boolean
        get() = false

    private fun initViews() {
        WidgetUtils.clearActivityBackground(this)
        mTitles = ResUtils.getStringArray(R.array.home_titles)
        binding!!.includeMain.toolbar.setOnMenuItemClickListener(this)
        mainFragment = MainFragment()
        taskFragment = TaskFragment()
        settingsFragment = SettingsFragment()
        //主页内容填充
        val fragments = arrayOf(
            mainFragment,
            taskFragment,
            settingsFragment
        )
        val adapter = FragmentAdapter(supportFragmentManager, fragments)
        binding!!.includeMain.viewPager.offscreenPageLimit = mTitles.size - 1
        binding!!.includeMain.viewPager.adapter = adapter
    }


    private fun initListeners() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding!!.drawerLayout,
            binding!!.includeMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding!!.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //侧边栏点击事件
        binding!!.navView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            if (menuItem.isCheckable) {
                binding!!.drawerLayout.closeDrawers()
                return@setNavigationItemSelectedListener handleNavigationItemSelected(menuItem)
            } else {
                when (menuItem.itemId) {
                    R.id.nav_settings -> openNewPage(SettingsFragment::class.java)
                    else -> XToastUtils.toast("Click:" + menuItem.title)
                }
            }
            true
        }
        binding!!.includeMain.bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    /**
     * 处理侧边栏点击事件
     *
     * @param menuItem
     * @return
     */
    private fun handleNavigationItemSelected(menuItem: MenuItem): Boolean {
        for (index in mTitles.indices) {
            if (mTitles[index] == menuItem.title) {
                binding!!.includeMain.viewPager.setCurrentItem(index, false)
                return true
            }
        }
        return false
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return false
    }

    override fun onClick(p0: View?) {
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        for (index in mTitles.indices) {
            if (mTitles[index] == menuItem.title) {
                binding!!.includeMain.toolbar.title = menuItem.title
                binding!!.includeMain.viewPager.setCurrentItem(index, false)
                updateSideNavStatus(menuItem)
                return true
            }
        }
        return false
    }

    /**
     * 更新侧边栏菜单选中状态
     *
     * @param menuItem
     */
    private fun updateSideNavStatus(menuItem: MenuItem) {
        val side = binding!!.navView.menu.findItem(menuItem.itemId)
        if (side != null) {
            side.isChecked = true
        }
    }

    //按返回键不退出回到桌面
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    override fun onItemClick(itemView: View?, widgetInfo: PageInfo, pos: Int) {
        try {
            @Suppress("UNCHECKED_CAST")
            PageOption.to(Class.forName(widgetInfo.classPath) as Class<XPageFragment>) //跳转的fragment
                .setNewActivity(true)
                .putInt(KEY_SENDER_TYPE, pos) //注意：目前刚好是这个顺序而已
                .open(this)
        } catch (e: Exception) {
            e.printStackTrace()
            XToastUtils.error(e.message.toString())
        }
    }

    override fun initTaskActivity(title: String?, url: String?) {
        binding!!.includeMain.viewPager.currentItem = 1
        taskFragment!!.init(title, url)
    }

    override fun initMainActivity() {
        binding!!.includeMain.viewPager.currentItem = 0
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            when (binding!!.includeMain.viewPager.currentItem) {
                0 -> {
                    // 抢夕夕
                    mainFragment!!.onKeyDown(keyCode, event)
                }
                1 -> {
                    // 做单大厅
                    taskFragment!!.onKeyDown(keyCode, event)
                }
                else -> {
                    binding!!.includeMain.viewPager.currentItem = 0
                }
            }
        }
        return true
    }
}