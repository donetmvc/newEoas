package com.eland.android.eoas.Activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.widget.Toast

import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.Model.LoginInfo
import com.eland.android.eoas.R
import com.eland.android.eoas.Service.LoginService
import com.eland.android.eoas.Util.CacheInfoUtil
import com.eland.android.eoas.Util.ConsoleUtil
import com.eland.android.eoas.Util.ProgressUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.ToastUtil
import com.eland.android.eoas.Views.EditTextView
import com.pgyersdk.javabean.AppBean
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import com.rey.material.widget.Button
import com.zhy.m.permission.MPermissions
import com.zhy.m.permission.PermissionDenied
import com.zhy.m.permission.PermissionGrant

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import cn.jpush.android.api.JPushInterface
import me.drakeet.materialdialog.MaterialDialog

/**
 * Created by liu.wenbin on 15/11/10.
 */
class LoginActivity : AppCompatActivity(), LoginService.ISignInListener, ProgressUtil.IOnDialogConfirmListener {

    @BindView(R.id.edit_username)
     var editUsername: EditTextView? = null
    @BindView(R.id.edit_password)
     var editPassword: EditTextView? = null
    @BindView(R.id.btn_login)
    var btnLogin: Button? = null
    private val TAG = "EOAS"
    private var progressDialog: Dialog? = null
    private var context: Context? = null
    private var loginService: LoginService? = null
    private var telephonyManager: TelephonyManager? = null
    private var loginId: String? = null
    private var password = ""
    private var loginFailCount = 0

    private var updateManagerListener: UpdateManagerListener? = null
    private var downUri: String? = null
    private var dialogUtil: ProgressUtil? = null
    private var updateDialog: MaterialDialog? = null
    private var theme = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        //read phone state
        if (!MPermissions.shouldShowRequestPermissionRationale(this@LoginActivity,
                Manifest.permission.READ_PHONE_STATE, REQUECT_CODE_READPHONESTATE)) {
            MPermissions.requestPermissions(this@LoginActivity, REQUECT_CODE_READPHONESTATE, Manifest.permission.READ_PHONE_STATE)
        }

        theme = SharedReferenceHelper.getInstance(this).getValue(Constant.EOAS_THEME)
        if (!theme.isEmpty()) {
            if (theme == "RED") {
                setTheme(R.style.LoginThemeRed)
            } else {
                setTheme(R.style.LoginThemeBlue)
            }
        } else {
            setTheme(R.style.LoginThemeRed)
        }
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        context = this
        ButterKnife.bind(this)

        initActivity()
        initUpdate()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    @PermissionGrant(REQUEST_CODE_WRITEEXTERNAL_STORAGE)
    fun requestExternalSuccess() {
        //access gps
        if (!MPermissions.shouldShowRequestPermissionRationale(this@LoginActivity, Manifest.permission.ACCESS_FINE_LOCATION, REQEST_CODE_ACCESS_FINE_LOCATION)) {
            MPermissions.requestPermissions(this@LoginActivity, REQEST_CODE_ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @PermissionDenied(REQUEST_CODE_WRITEEXTERNAL_STORAGE)
    fun requestExternalFailed() {
        //Toast.makeText(this, "DENY ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @PermissionGrant(REQUECT_CODE_READPHONESTATE)
    fun requestPhoneStateSuccess() {
        //access EXTERNAL
        if (!MPermissions.shouldShowRequestPermissionRationale(this@LoginActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_CODE_WRITEEXTERNAL_STORAGE)) {
            MPermissions.requestPermissions(this@LoginActivity, REQUEST_CODE_WRITEEXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    @PermissionDenied(REQUECT_CODE_READPHONESTATE)
    fun requestPhoneStateFailed() {
        //Toast.makeText(this, "DENY ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @PermissionGrant(REQEST_CODE_ACCESS_FINE_LOCATION)
    fun requestLocationSuccess() {
        //Toast.makeText(this, "GRANT ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
        //access Mic
        if (!MPermissions.shouldShowRequestPermissionRationale(this@LoginActivity, Manifest.permission.RECORD_AUDIO, REQEST_CODE_RECORD_AUDIO)) {
            MPermissions.requestPermissions(this@LoginActivity, REQEST_CODE_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO)
        }
    }

    @PermissionDenied(REQUECT_CODE_READPHONESTATE)
    fun requestMicFailed() {
        //Toast.makeText(this, "DENY ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @PermissionGrant(REQEST_CODE_RECORD_AUDIO)
    fun requestMicSuccess() {
        //Toast.makeText(this, "GRANT ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(REQEST_CODE_RECORD_AUDIO)
    fun requestLocationFailed() {
        //Toast.makeText(this, "DENY ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_login)
    internal fun login() {
        val isGo = invalidateInput()

        if (isGo) {
            progressDialog = ProgressUtil().showProgress(context!!)

            loginService!!.signIn(loginId!!, password, "", telephonyManager!!.deviceId)
        }
    }

    private fun initActivity() {
        //如果进入登录页面说明还未登录，暂不能接收push
        JPushInterface.stopPush(applicationContext)

        loginService = LoginService(context!!)
        loginService!!.setOnSignInListener(this)
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        dialogUtil = ProgressUtil()
        dialogUtil!!.setOnDialogConfirmListener(this)
    }

    private fun initUpdate() {
        updateManagerListener = object : UpdateManagerListener() {
            override fun onNoUpdateAvailable() {

            }

            override fun onUpdateAvailable(result: String) {
                val appBean = UpdateManagerListener.getAppBeanFromString(result)
                val message = appBean.releaseNote
                downUri = appBean.downloadURL

                updateDialog = dialogUtil!!.showDialogUpdate(message, resources.getString(R.string.hasupdate), context!!)
                updateDialog!!.show()
            }
        }

        //检测更新
        PgyUpdateManager.register(this, updateManagerListener)
    }

    private fun invalidateInput(): Boolean {
        loginId = editUsername!!.text.toString()
        password = editPassword!!.text.toString()

        if (loginId!!.isEmpty() && password.isEmpty()) {
            editUsername!!.setShakeAnimation()
            editPassword!!.setShakeAnimation()

            ToastUtil.showToast(this, "用户名或密码不能为空", Toast.LENGTH_LONG)
            return false
        }

        if (loginId!!.isEmpty()) {
            editUsername!!.setShakeAnimation()

            ToastUtil.showToast(this, "用户名不能为空", Toast.LENGTH_LONG)
            return false
        }

        if (password.isEmpty()) {
            editPassword!!.setShakeAnimation()

            ToastUtil.showToast(this, "密码不能为空", Toast.LENGTH_LONG)
            return false
        }

        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        return
    }

    override fun onSignInSuccess(info: LoginInfo) {
        if (progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
        SharedReferenceHelper.getInstance(context!!).setValue(Constant.LOGINID, loginId!!)
        SharedReferenceHelper.getInstance(context!!).setValue(Constant.LOGIN_SUCCESS, "TRUE")
        SharedReferenceHelper.getInstance(context!!).setValue(Constant.LOGINID_CELLNO, info.cellNo!!)
        SharedReferenceHelper.getInstance(context!!).setValue(Constant.LOGINID_EMAIL, info.email!!)
        SharedReferenceHelper.getInstance(context!!).setValue(Constant.LOGINID_USERNAME, info.userName!!)

        //登录系统后开启接收push,当然需要判断下用户是否设置了接收与否
        if (CacheInfoUtil.loadIsReceive(this, loginId!!)!!) {
            JPushInterface.resumePush(applicationContext)
        }

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSignInFailure(code: Int, msg: String) {
        var message = ""

        if (code == 99999) {
            message = "服务器异常"
        } else {
            message = msg
        }

        if (loginFailCount == 1) {
            ToastUtil.showToast(context!!, message, Toast.LENGTH_LONG)
            if (progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
            SharedReferenceHelper.getInstance(context!!).setValue(Constant.LOGINID, "")
            SharedReferenceHelper.getInstance(context!!).setValue(Constant.LOGIN_SUCCESS, "")
            SharedReferenceHelper.getInstance(context!!).setValue(Constant.LOGINID_CELLNO, "")
            SharedReferenceHelper.getInstance(context!!).setValue(Constant.LOGINID_EMAIL, "")
            SharedReferenceHelper.getInstance(context!!).setValue(Constant.LOGINID_USERNAME, "")
        } else {
            loginFailCount++
            ConsoleUtil.i(TAG, "--------try login again------------")
            loginService!!.signIn(loginId!!, password, "", telephonyManager!!.deviceId)
        }
    }

    override fun OnDialogConfirmListener() {
        updateDialog!!.dismiss()

//        UpdateManagerListener.startDownloadTask()

        UpdateManagerListener.startDownloadTask(this@LoginActivity, downUri)
    }

    override fun onDestroy() {
        super.onDestroy()
        loginService!!.cancel()
    }

    companion object {

        const val REQUEST_CODE_WRITEEXTERNAL_STORAGE = 1
        const val REQUECT_CODE_READPHONESTATE = 2
        const val REQEST_CODE_ACCESS_FINE_LOCATION = 3
        const val REQEST_CODE_RECORD_AUDIO = 4
    }
}
