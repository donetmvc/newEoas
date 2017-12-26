package com.eland.android.eoas.Activity

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.eland.android.eoas.R
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
        showPhoneStateWithPermissionCheck()
    }


    @NeedsPermission(READ_PHONE_STATE_PERMISSION)
    fun showPhoneState() {

    }

    @OnShowRationale(READ_PHONE_STATE_PERMISSION)
    fun showRationaleForPhoneState(request: PermissionRequest) {
        //showRationaleDialog(R.string.permission_camera_rationale, request)
    }

    @OnPermissionDenied(READ_PHONE_STATE_PERMISSION)
    fun onPhoneStateDenied() {
        Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(READ_PHONE_STATE_PERMISSION)
    fun onPhoneStateNeverAskAgain() {
        Toast.makeText(this, R.string.permission_camera_never_askagain, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val READ_PHONE_STATE_PERMISSION: String = Manifest.permission.READ_PHONE_STATE
        const val CAMERA_PERMISSION: String = Manifest.permission.CAMERA
    }
}
