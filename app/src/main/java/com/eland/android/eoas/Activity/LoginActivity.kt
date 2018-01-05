package com.eland.android.eoas.Activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import com.rey.material.widget.Button

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import cn.jpush.android.api.JPushInterface
import com.eland.android.eoas.DeviceInfoFactory.GetDeviceInfo
import me.drakeet.materialdialog.MaterialDialog

/**
 * Created by liu.wenbin on 15/11/10.
 */
class LoginActivity : BaseActivity(), LoginService.ISignInListener, ProgressUtil.IOnDialogConfirmListener {

    @BindView(R.id.edit_username)
    lateinit var editUsername: EditTextView
    @BindView(R.id.edit_password)
    lateinit var editPassword: EditTextView
    @BindView(R.id.btn_login)
    lateinit var btnLogin: Button

    private val TAG = "EOAS"
    private lateinit var progressDialog: Dialog
    private lateinit var context: Context
    private lateinit var loginService: LoginService
    private lateinit var telephonyManager: TelephonyManager
    private var loginId: String = ""
    private var password = ""
    private var loginFailCount = 0

    private var updateManagerListener: UpdateManagerListener? = null
    private var downUri: String? = null
    private var dialogUtil: ProgressUtil? = null
    private var updateDialog: MaterialDialog? = null
    private var theme = ""
    private var imei: String? = ""

    private lateinit var loading: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
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

        loading = ProgressUtil.loading(this@LoginActivity)

        initActivity()
        initUpdate()
        initImei()
    }

    @OnClick(R.id.btn_login)
    internal fun login() {
        val isGo = invalidateInput()

        if (isGo) {
            //progressDialog = ProgressUtil().showProgress(context)
            showLoading()
            ConsoleUtil.i(TAG, "--------imei value----=========$imei")
            loginService.signIn(loginId, password, "", imei)
        }
    }

    private fun initActivity() {
        //如果进入登录页面说明还未登录，暂不能接收push
        JPushInterface.stopPush(applicationContext)

        loginService = LoginService(context)
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

    private fun initImei() {
        imei = GetDeviceInfo(this).getDeviceId()
    }

    private fun invalidateInput(): Boolean {
        loginId = editUsername.text.toString()
        password = editPassword.text.toString()

        if (loginId!!.isEmpty() && password.isEmpty()) {
            editUsername.setShakeAnimation()
            editPassword.setShakeAnimation()

            ToastUtil.showToast(this, "用户名或密码不能为空", Toast.LENGTH_LONG)
            return false
        }

        if (loginId!!.isEmpty()) {
            editUsername.setShakeAnimation()

            ToastUtil.showToast(this, "用户名不能为空", Toast.LENGTH_LONG)
            return false
        }

        if (password.isEmpty()) {
            editPassword.setShakeAnimation()

            ToastUtil.showToast(this, "密码不能为空", Toast.LENGTH_LONG)
            return false
        }

        return true
    }

    private fun showLoading() {
        if(loading.isShowing) loading.hide() else loading.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        return
    }

    override fun onSignInSuccess(info: LoginInfo) {
        showLoading()
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID, loginId)
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGIN_SUCCESS, "TRUE")
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_CELLNO, info.cellNo!!)
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_EMAIL, info.email!!)
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_USERNAME, info.userName!!)

        //登录系统后开启接收push,当然需要判断下用户是否设置了接收与否
        if (CacheInfoUtil.loadIsReceive(this, loginId)!!) {
            JPushInterface.resumePush(applicationContext)
        }

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSignInFailure(code: Int, msg: String) {
        showLoading()
        val message = if (code == 99999) {
            "服务器异常"
        } else {
            msg
        }
        ToastUtil.showToast(context, message, Toast.LENGTH_LONG)
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID, "")
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGIN_SUCCESS, "")
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_CELLNO, "")
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_EMAIL, "")
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_USERNAME, "")
    }

    fun setLoginInfo(info: LoginInfo) {
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID, "")
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGIN_SUCCESS, "TRUE")
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_CELLNO, info.cellNo!!)
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_EMAIL, info.email!!)
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_USERNAME, info.userName!!)
    }

    override fun OnDialogConfirmListener() {
        updateDialog!!.dismiss()
        UpdateManagerListener.startDownloadTask(this@LoginActivity, downUri)
    }

    override fun onDestroy() {
        super.onDestroy()
        loginService.cancel()
    }

    companion object {
    }
}
