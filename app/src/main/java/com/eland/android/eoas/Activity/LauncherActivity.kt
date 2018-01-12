package com.eland.android.eoas.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.R
import com.eland.android.eoas.Util.SharedReferenceHelper

import java.util.Timer
import java.util.TimerTask

/**
 * Created by liu.wenbin on 15/11/10.
 */
class LauncherActivity : AppCompatActivity() {

    private val TAG = "EOAS"
    private lateinit var timer: Timer
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        context = this
        initTimer()
    }

    override fun onResume() {
        super.onResume()
        //JPushInterface.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        //JPushInterface.onPause(this)
    }

    private fun initTimer() {
        timer = Timer()

        timer.schedule(object : TimerTask() {
            override fun run() {
                goLogin()
            }
        }, 3000)
    }

    private fun goLogin() {
        val isLogin = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGIN_SUCCESS)
        var intent: Intent = if(isLogin == "TRUE") {
            Intent(this@LauncherActivity, MainActivity::class.java)
        } else {
            Intent(this@LauncherActivity, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
