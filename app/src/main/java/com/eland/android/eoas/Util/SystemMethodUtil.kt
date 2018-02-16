package com.eland.android.eoas.Util

import android.content.Context

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList
import android.app.ActivityManager
import android.app.ActivityManager.RunningServiceInfo


import com.eland.android.eoas.Model.LoginInfo

/**
 * Created by liu.wenbin on 15/12/9.
 */
object SystemMethodUtil {

    //ping -c 3 -w 100  中  ，-c 是指ping的次数 3是指ping 3次 ，-w 100  以秒为单位指定超时间隔，是指超时时间为100秒
    val isInnerNetwork: Boolean
        get() {
            val p: Process
            var status: Int = -1
            try {
                p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + "10.202.101.23")

                p.inputStream.use { input -> {
                    status = p.waitFor()
                    val line = BufferedReader(InputStreamReader(input)).readLine()
                    while (line != null) {
                        StringBuffer().append(line)
                    }
                } }

                return status == 0
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return true
        }

    fun isWorked(context: Context, serviceName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServiceInfos = activityManager.getRunningServices(30) as ArrayList<RunningServiceInfo>
        for (i in runningServiceInfos.indices) {
            if (runningServiceInfos[i].service.className.toString() == serviceName) {
                return true
            }
        }

        return false
    }

    fun isContains(list: ArrayList<LoginInfo>, userId: String): Boolean? {
        return list.any { it.userId == userId }
    }
}
