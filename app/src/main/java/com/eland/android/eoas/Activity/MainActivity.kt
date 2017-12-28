package com.eland.android.eoas.Activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.View
import android.widget.Toast

import com.eland.android.eoas.Application.EOASApplication
import com.eland.android.eoas.Fragment.DrawerFragment
import com.eland.android.eoas.Fragment.MainFragment
import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.R
import com.eland.android.eoas.Util.ProgressUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.ToastUtil
import com.pgyersdk.javabean.AppBean
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import java.util.Timer
import java.util.TimerTask

import me.drakeet.materialdialog.MaterialDialog


/**
 * Created by liu.wenbin on 15/11/10.
 */
class MainActivity : AppCompatActivity(), ProgressUtil.IOnMainUpdateListener {

    private val TAG = "EOAS"
    private var isExit: Boolean? = false

    private var fragmentManager: FragmentManager? = null
    private var mainFragment: MainFragment? = null
    private var mActionBarDrawerToggle: ActionBarDrawerToggle? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var drawerStatus: Boolean = false

    private var updateManagerListener: UpdateManagerListener? = null
    private var downUri: String? = null
    private var dialogUtil: ProgressUtil? = null
    private var updateMainDialog: MaterialDialog? = null
    private var context: Context? = null
    private var theme = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        theme = SharedReferenceHelper.getInstance(this).getValue(Constant.EOAS_THEME)
        if (!theme.isEmpty()) {
            if (theme == "RED") {
                setTheme(R.style.MainThenmeRed)
            } else {
                setTheme(R.style.MainThenmeBlue)
            }
        } else {
            setTheme(R.style.MainThenmeRed)
        }
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        fragmentManager = supportFragmentManager
        EOASApplication.instance.addActivity(this)

        context = this

        initDrawer()
        initActivity()
        initUpdate()
        setMainPage(MAIN_FRAGMENT)
    }

    private fun initDrawer() {
        mDrawerLayout = findViewById(R.id.content_drawer) as DrawerLayout?

        mDrawerLayout!!.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.left_drawer,
                        DrawerFragment(this@MainActivity)).commit()

        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        toolbar!!.setTitleTextColor(Color.parseColor("#ffffff")) //设置标题颜色
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true) //设置返回键可用
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mActionBarDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open,
                R.string.drawer_close) {
            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                drawerStatus = false
            }

            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                drawerStatus = true
            }
        }

        mActionBarDrawerToggle!!.syncState()
        mDrawerLayout!!.setDrawerListener(mActionBarDrawerToggle)
        setmDrawerLayout(mDrawerLayout!!)
    }

    private fun initActivity() {
        dialogUtil = ProgressUtil()
        dialogUtil!!.setOnMainUpdateListener(this)
    }

    private fun initUpdate() {

        if (null == updateManagerListener) {
            updateManagerListener = object : UpdateManagerListener() {
                override fun onNoUpdateAvailable() {

                }

                override fun onUpdateAvailable(result: String) {
                    val appBean = UpdateManagerListener.getAppBeanFromString(result)
                    val message = appBean.releaseNote
                    downUri = appBean.downloadURL

                    updateMainDialog = dialogUtil!!.showDialogUpdateForMain(message, resources.getString(R.string.hasupdate), context!!)
                    updateMainDialog!!.show()
                }
            }

            //检测更新
            PgyUpdateManager.register(this, updateManagerListener)
        }
    }

    /*
    *页面跳转
    * */
    fun setMainPage(menuType: Int) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out)

        val ft = fragmentManager!!.findFragmentByTag("MainFragment")
        val args = Bundle()
        mainFragment = if (ft == null) MainFragment.newInstance(args) else ft as MainFragment

        when (menuType) {
            MAIN_FRAGMENT -> if (null != mainFragment && mainFragment!!.isAdded) {
                //transaction.show(mainFragment);
                hideFragments(transaction)
            } else {
                transaction.add(R.id.main_content, mainFragment, "MainFragment")
            }
        }
        transaction.commit()
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        if (mainFragment != null && mainFragment!!.isAdded) {
            val isChanging = SharedReferenceHelper.getInstance(this).getValue(Constant.EOAS_ISCHANGINGTHEME)
            if (isChanging == "TURE") {
                val i = fragmentManager!!.backStackEntryCount
                if (i > 0) {
                    transaction.hide(mainFragment)
                } else {
                    SharedReferenceHelper.getInstance(this).setValue(Constant.EOAS_ISCHANGINGTHEME, "FALSE")
                    transaction.show(mainFragment)
                }
            } else {
                SharedReferenceHelper.getInstance(this).setValue(Constant.EOAS_ISCHANGINGTHEME, "FALSE")
                transaction.show(mainFragment)
            }
        }
    }

    fun setmDrawerLayout(mDrawerLayout: DrawerLayout) {
        this.mDrawerLayout = mDrawerLayout
    }

    /**
     * 菜单、返回键响应
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //if(mDrawerLayout.openDrawer(Gravity.NO_GRAVITY))
            val fragmentList = fragmentManager!!.fragments

            val i = fragmentManager!!.backStackEntryCount

            if (i > 0) {
                fragmentManager!!.popBackStack()
            } else {
                existAppByDoubleClick()
            }
        }

        return false
    }

    private fun existAppByDoubleClick() {
        var tExit: Timer? = null
        if (isExit == false) {
            isExit = true // 准备退出
            ToastUtil.showToast(this, "再次点击将退出应用程序", Toast.LENGTH_LONG)
            tExit = Timer()
            tExit.schedule(object : TimerTask() {
                override fun run() {
                    isExit = false // 取消退出
                }
            }, 2000) // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            EOASApplication.instance.existApp()
            finish()
        }
    }

    override fun onUpdateSuccess() {
        updateMainDialog!!.dismiss()
        UpdateManagerListener.startDownloadTask(this@MainActivity, downUri)
//        updateManagerListener!!.(this@MainActivity.downUri)
    }

    companion object {
        private val PROFILE_SETTING = 1

        private val MAIN_FRAGMENT = 10
    }
}
