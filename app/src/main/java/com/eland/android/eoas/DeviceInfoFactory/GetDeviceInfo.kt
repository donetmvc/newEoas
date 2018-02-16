package com.eland.android.eoas.DeviceInfoFactory

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import com.eland.android.eoas.Activity.BaseActivity
import com.eland.android.eoas.Activity.showPermissionWithPermissionCheck
import permissions.dispatcher.PermissionUtils

/**
 * Created by liuwenbin on 2017/12/28.
 * 虽然青春不在，但不能自我放逐.
 */
class GetDeviceInfo {
    private var contexts: Context

    constructor(context: Context) {
        contexts = context
    }

    @SuppressLint("MissingPermission")
    fun getDeviceId(): String? {
        if(!PermissionUtils.hasSelfPermissions(contexts,
                BaseActivity.READ_EXTERNAL_STORAGE_PERMISSION)) {
            return ""
        }

        val telephonyManager: TelephonyManager? = contexts.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return if(Build.VERSION.SDK_INT < 26) {
            telephonyManager?.deviceId
        }
        else {
            telephonyManager?.imei
        }
    }

}