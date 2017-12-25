package com.eland.android.eoas.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.R
import com.eland.android.eoas.Util.SharedReferenceHelper

import java.util.Timer
import java.util.TimerTask

import cn.jpush.android.api.JPushInterface

/**
 * Created by liu.wenbin on 15/11/10.
 */
class LauncherActivity : Activity() {

    private val TAG = "EOAS"
    private var timer: Timer? = null
    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        context = this
        initTimer()
    }

    override fun onResume() {
        super.onResume()
        JPushInterface.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        JPushInterface.onPause(this)
    }

    private fun initTimer() {
        timer = Timer()

        timer!!.schedule(object : TimerTask() {
            override fun run() {
                goLogin()
            }
        }, 3000)
    }

    private fun goLogin() {

        val isLogin = SharedReferenceHelper.getInstance(context!!).getValue(Constant.LOGIN_SUCCESS)
        val intent: Intent

        if (isLogin == "TRUE") {
            intent = Intent(this@LauncherActivity, MainActivity::class.java)
        } else {
            intent = Intent(this@LauncherActivity, LoginActivity::class.java)
        }

        startActivity(intent)

        finish()
    }
}
