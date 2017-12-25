package com.eland.android.eoas.Activity

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

import com.zhy.m.permission.MPermissions
import com.zhy.m.permission.PermissionDenied
import com.zhy.m.permission.PermissionGrant

/**
 * Created by liuwenbin on 2017/2/8.
 */

class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MPermissions.shouldShowRequestPermissionRationale(this@BaseActivity, Manifest.permission.READ_PHONE_STATE, REQUECT_CODE_SDCARD)) {
            MPermissions.requestPermissions(this@BaseActivity, REQUECT_CODE_SDCARD, Manifest.permission.READ_PHONE_STATE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


//    @PermissionGrant(REQUECT_CODE_SDCARD)
    fun requestPhoneStateSuccess() {
        Toast.makeText(this, "GRANT ACCESS SDCARD!", Toast.LENGTH_SHORT).show()
    }

//    @PermissionDenied(REQUECT_CODE_SDCARD)
    fun requestPhoneStateFailed() {
        Toast.makeText(this, "DENY ACCESS SDCARD!", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val REQUECT_CODE_SDCARD = 2
    }
}
