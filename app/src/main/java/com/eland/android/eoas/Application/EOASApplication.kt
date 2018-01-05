package com.eland.android.eoas.Application

import android.app.Activity
import android.app.Application
import android.content.Context

import com.eland.android.eoas.Util.ActivityManager
import com.pgyersdk.crash.PgyCrashManager

import cn.jpush.android.api.JPushInterface
import com.eland.android.eoas.BuildConfig
import com.eland.android.eoas.Jobs.DemoJobCreator
import com.eland.android.eoas.Jobs.MyLogger
import com.eland.android.eoas.di.AppComponent
import com.evernote.android.job.JobConfig
import com.evernote.android.job.JobManager
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.DiskLogAdapter
import com.orhanobut.logger.Logger
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber


/**
 * Created by liu.wenbin on 15/11/10.
 */
class EOASApplication : DaggerApplication() {
    var TAG = "EOAS"
    var photoUri = "http://182.92.65.253:30001/Eland.EOAS/Images/"

    //    public String photoUri = "http://10.202.101.11:30002/Eland.EOAS/Images/";
    var apiUri: String? = null

    init {
        instance = this
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        var appComponent: AppComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
        return  appComponent
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
        JobConfig.addLogger(MyLogger())
        JobManager.create(this).addJobCreator(DemoJobCreator())

        //debug
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })

        //release
        Logger.addLogAdapter(DiskLogAdapter())

        // Set methodOffset to 5 in order to hide internal method calls
        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                Logger.log(priority, tag, message, t)
            }
        })
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
