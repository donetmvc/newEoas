package com.eland.android.eoas.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.eland.android.eoas.Application.EOASApplication
import permissions.dispatcher.*

/**
 * Created by liuwenbin on 2017/2/8.
 */
@RuntimePermissions
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAppPermission()
    }

    fun checkAppPermission() {
        if(!PermissionUtils.hasSelfPermissions(this,
                READ_EXTERNAL_STORAGE_PERMISSION,
                WRITE_EXTERNAL_STORAGE_PERMISSION,
                READ_PHONE_STATE_PERMISSION,
                CAMERA_PERMISSION,
                ACCESS_FINE_LOCATION_PEMISSION,
                ACCESS_CROSE_LOCATION_PERMISSION)) {
            showPermissionWithPermissionCheck()
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated method
        onRequestPermissionsResult(requestCode, grantResults)

        //if(grantResults.any { it -> it == -1 }) EOASApplication.instance.existApp()
        if(grantResults.all { it === 0 }) {
            EOASApplication.instance?.registerPgy()
            EOASApplication.instance?.initJPush()
        }

//        if(permissions.isNotEmpty()) {
//            when(permissions.first()) {
//                READ_PHONE_STATE_PERMISSION -> if(grantResults[requestCode] == 0) getIMEI() else imei = ""
//            }
//        }
    }

    @NeedsPermission(
            READ_EXTERNAL_STORAGE_PERMISSION,
            WRITE_EXTERNAL_STORAGE_PERMISSION,
            READ_PHONE_STATE_PERMISSION,
            CAMERA_PERMISSION,
            ACCESS_FINE_LOCATION_PEMISSION,
            ACCESS_CROSE_LOCATION_PERMISSION)
    fun showPermission() {

    }

    companion object {
        const val READ_PHONE_STATE_PERMISSION: String = Manifest.permission.READ_PHONE_STATE
        const val CAMERA_PERMISSION: String = Manifest.permission.CAMERA
        const val WRITE_EXTERNAL_STORAGE_PERMISSION: String = Manifest.permission.WRITE_EXTERNAL_STORAGE
        const val READ_EXTERNAL_STORAGE_PERMISSION: String = Manifest.permission.READ_EXTERNAL_STORAGE
        const val ACCESS_FINE_LOCATION_PEMISSION: String = Manifest.permission.ACCESS_FINE_LOCATION
        const val ACCESS_CROSE_LOCATION_PERMISSION: String = Manifest.permission.ACCESS_COARSE_LOCATION


        lateinit  var imei: String
    }
}
