package com.eland.android.eoas.Application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent

import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.Service.RegistAutoService
import com.eland.android.eoas.Util.ActivityManager
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.SystemMethodUtil
import com.pgyersdk.crash.PgyCrashManager

import cn.jpush.android.api.JPushInterface
import me.leolin.shortcutbadger.ShortcutBadger


/**
 * Created by liu.wenbin on 15/11/10.
 */
class EOASApplication : Application() {

    var TAG = "EOAS"
    var photoUri = "http://182.92.65.253:30001/Eland.EOAS/Images/"

    //    public String photoUri = "http://10.202.101.11:30002/Eland.EOAS/Images/";
    var apiUri: String? = null

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        //val context: Context = EOASApplication.applicationContext()

        //蒲公英捕捉crash
        //PgyCrashManager.register(this)

        //初始化JPush 正式发布是，需要把注释打开
        //JPushInterface.setDebugMode(false)
        //JPushInterface.init(this)

        JPushInterface.stopPush(this)
    }

    fun registerPgy() {
        PgyCrashManager.register(this)
    }

    fun initJPush() {
        JPushInterface.init(this)
    }

    fun addActivity(ac: Activity) {
        ActivityManager.instance.addActivity(ac)
    }

    fun existApp() {
        ActivityManager.instance.removeAllActivity()
        System.exit(0)
    }

    companion object {
        var instance: EOASApplication? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}
