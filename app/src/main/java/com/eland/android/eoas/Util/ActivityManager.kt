package com.eland.android.eoas.Util

import android.app.Activity
import java.util.*

/**
 * Created by liu.wenbin on 15/11/10.
 */
class ActivityManager private constructor() {

    fun addActivity(activity: Activity) {
        activityList?.add(activity)
    }

    /**
     * 结束所有activity
     */
    fun removeAllActivity() {
        activityList?.forEach {
            if(!it.isFinishing) {
                it.finish()
            }
        }

        activityList?.clear()
    }

    companion object {

        private var activityList: ArrayList<Activity>? = null

        private var activities: Collection<Activity>? = null


        private var activityManager: ActivityManager? = null

        val instance: ActivityManager
            get() {
                if (null == activityManager) {
                    activityManager = ActivityManager()
                }
                return activityManager!!
            }
    }
}
