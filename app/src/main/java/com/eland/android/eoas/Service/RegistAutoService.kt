package com.eland.android.eoas.Service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.TelephonyManager

import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.Util.SharedReferenceHelper

/**
 * Created by liu.wenbin on 15/12/18.
 */
class RegistAutoService : Service(), RegistPushIDService.IOnRegistListener {

    private var pushId: String? = null
    private var imei = ""
    private var telephonyManager: TelephonyManager? = null

    override fun onCreate() {
        super.onCreate()
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (null != intent) {
            pushId = intent.getStringExtra("PUSHID")
        }
        imei = telephonyManager!!.deviceId

        if (!pushId!!.isEmpty() && !imei.isEmpty()) {
            startRegistService()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startRegistService() {
        val registPushIDService = RegistPushIDService()
        registPushIDService.registPush(pushId!!, imei, this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onRegistSuccess() {
        SharedReferenceHelper.getInstance(this).setValue(Constant.EOAS_PUSHID, "SUCCESS")
        stopSelf()
    }

    override fun onRegistFailure() {
        //save push id to cache
        SharedReferenceHelper.getInstance(this).setValue(Constant.EOAS_PUSHID, "FAILURE")
        stopSelf()
    }
}
