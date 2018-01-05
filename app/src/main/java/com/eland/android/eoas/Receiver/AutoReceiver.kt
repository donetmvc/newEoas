package com.eland.android.eoas.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.eland.android.eoas.DeviceInfoFactory.GetDeviceInfo

import com.eland.android.eoas.Service.CheckRegScheduleService
import com.eland.android.eoas.Service.RegAutoService
import com.eland.android.eoas.Util.ConsoleUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.SystemMethodUtil

import java.text.SimpleDateFormat
import java.util.Date
import java.util.GregorianCalendar

/**
 * Created by liu.wenbin on 15/12/9.
 */
class AutoReceiver : BroadcastReceiver(), CheckRegScheduleService.IOnCheckListener {

    private var action: String? = ""
    private var context: Context? = null
    private val TAG = "EOAS"
    private var intents: Intent? = null
    private var telephonyManager: TelephonyManager? = null
    private var imei: String? = null
    private var isAm: String? = null
    private var recievedBroadcast = "false"

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        action = intent.action

        init()

        ConsoleUtil.i(TAG, "--------ACTION:------------" + action!!)

        if (!action!!.isEmpty() && action == "REG_AUTO") {
            //check is it right time to start service
            val format = SimpleDateFormat("HH:mm:ss")
            val currentDate = Date(System.currentTimeMillis())
            val formatDate = format.format(currentDate)
            val hour = Integer.valueOf(formatDate.substring(0, 5).replace(":", ""))!!

            recievedBroadcast = SharedReferenceHelper.getInstance(context).getValue("REG_AUTO")
            ConsoleUtil.i(TAG, "--------Received:------------" + recievedBroadcast)
            if (recievedBroadcast != "true") {
                if (hour >= 650 && hour <= 830 || hour > 1705 && hour < 1800) {
                    //check are you reg schedule
                    startCheck()
                }
            }
        }

    }

    private fun init() {
        intents = Intent(context, RegAutoService::class.java)
        telephonyManager = context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        imei = GetDeviceInfo(context!!).getDeviceId() //telephonyManager!!.deviceId
        val gregorianCalendar = GregorianCalendar()
        val amPm = gregorianCalendar.get(GregorianCalendar.AM_PM)
        isAm = if (amPm == 0) {
            "AM"
        } else {
            "PM"
        }
    }

    private fun startCheck() {
        SharedReferenceHelper.getInstance(context!!).setValue("REG_AUTO", "true")
        if (!SystemMethodUtil.isWorked(context!!, "RegAutoService")) {
            val checkService = CheckRegScheduleService()
            checkService.check(imei!!, this)
        }
    }

    private fun startRegService() {
        ConsoleUtil.i(TAG, "--------Service start------------")
        intents!!.putExtra("IMEI", imei)
        intents!!.putExtra("ISAM", isAm)
        intents!!.putExtra("TYPE", "AUTO")
        context!!.startService(intents)
    }

    private fun stopRegService() {
        ConsoleUtil.i(TAG, "--------Service stop------------")
        context!!.stopService(intents)
    }

    override fun onCheckSuccess(msg: String) {
        //        startRegService();
        ConsoleUtil.i(TAG, "--------Message:------------" + msg)
        SharedReferenceHelper.getInstance(context!!).setValue("REG_AUTO", "false")
        if (msg == "EMPTY") {
            startRegService()
        } else {
            stopRegService()
        }
    }

    override fun onCheckFailure() {
        //如果失败，继续check
        startCheck()
    }
}
