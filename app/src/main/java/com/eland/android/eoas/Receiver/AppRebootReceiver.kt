package com.eland.android.eoas.Receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.eland.android.eoas.DeviceInfoFactory.GetDeviceInfo

import com.eland.android.eoas.Util.CacheInfoUtil
import com.eland.android.eoas.Util.ConsoleUtil

/**
 * Created by liu.wenbin on 15/12/14.
 */
class AppRebootReceiver : BroadcastReceiver() {

    private var action: String? = ""
    private val context: Context? = null
    private val TAG = "EOAS"
    private var telephonyManager: TelephonyManager? = null
    private var imei: String? = null

    override fun onReceive(context: Context, intent: Intent) {

        action = intent.action
        telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        imei = GetDeviceInfo(context).getDeviceId()

        ConsoleUtil.i(TAG, "----------自启动:-------------" + imei!!)

        if (!action!!.isEmpty() && action == Intent.ACTION_BOOT_COMPLETED) {

            ConsoleUtil.i(TAG, "----------自启动:-------------" + CacheInfoUtil.loadIsRegAuto(context, imei!!).toString())

            if (CacheInfoUtil.loadIsRegAuto(context, imei!!)!!) {
                ConsoleUtil.i(TAG, "----------自启动:-------------" + "start")
                val clockIntent = Intent(context, AutoReceiver::class.java)
                clockIntent.action = "REG_AUTO"
                val alar = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val pi = PendingIntent.getBroadcast(context, 0, clockIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                alar.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (10 * 1000).toLong(), pi)
                ConsoleUtil.i(TAG, "----------自启动:-------------" + "finished")
            }
        }
    }
}
