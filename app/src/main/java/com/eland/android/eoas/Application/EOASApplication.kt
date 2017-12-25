package com.eland.android.eoas.Application

import android.app.Activity
import android.app.Application
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

    override fun onCreate() {
        super.onCreate()

        //蒲公英捕捉crash
        PgyCrashManager.register(this)

        //初始化JPush 正式发布是，需要把注释打开
        JPushInterface.setDebugMode(false)
        JPushInterface.init(this)

        initApplicantion()
    }

    private fun initApplicantion() {
        //check is push id regist success or failure?
        //the first time launcher app it does't work
        val isOk = SharedReferenceHelper.getInstance(this).getValue(Constant.EOAS_PUSHID)//"FAILURE");
        if (isOk == "FAILURE") {
            val pushId = JPushInterface.getRegistrationID(this)
            //retry to regist push id
            startRegistPushId(pushId)
        }
    }

    private fun startRegistPushId(pushId: String) {
        val intents = Intent(this, RegistAutoService::class.java)
        intents.putExtra("PUSHID", pushId)
        startService(intents)
    }

    fun addActivity(ac: Activity) {
        ActivityManager.instance.addActivity(ac)
    }

    fun existApp() {
        ActivityManager.instance.removeAllActivity()
        System.exit(0)
    }

    companion object {
        var mInstance: EOASApplication? = null

        val instance: EOASApplication
            @Synchronized get() {
                if (mInstance == null) {
                    mInstance = EOASApplication()
                }
                return mInstance!!
            }
    }
}
