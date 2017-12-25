package com.eland.android.eoas.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.eland.android.eoas.Activity.ApproveActivity
import com.eland.android.eoas.Activity.TestActivity
import com.eland.android.eoas.Service.RegistAutoService
import com.eland.android.eoas.Util.ConsoleUtil

import cn.jpush.android.api.JPushInterface

/**
 * Created by liu.wenbin on 15/12/17.
 */
class PushMessageReceiver : BroadcastReceiver() {

    private val TAG = "EOAS"
    private var context: Context? = null

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        val bundle = intent.extras
        ConsoleUtil.i(TAG, "onReceive - " + intent.action!!)

        if (JPushInterface.ACTION_REGISTRATION_ID == intent.action) {
            val regId = bundle!!.getString(JPushInterface.EXTRA_REGISTRATION_ID)
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId!!)
            //send the Registration Id to your server...
            startRegistPushID(regId)

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED == intent.action) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle!!.getString(JPushInterface.EXTRA_MESSAGE)!!)
            //processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED == intent.action) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知")
            val notifactionId = bundle!!.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId)

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED == intent.action) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知")
            val applyId = bundle!!.getString(JPushInterface.EXTRA_EXTRA)

            //打开自定义的Activity
            val i = Intent(context, ApproveActivity::class.java)
            i.putExtras(bundle)
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(i)

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK == intent.action) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle!!.getString(JPushInterface.EXTRA_EXTRA)!!)
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE == intent.action) {
            val connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false)
            Log.w(TAG, "[MyReceiver]" + intent.action + " connected state change to " + connected)
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.action!!)
        }

    }

    private fun startRegistPushID(pushId: String?) {
        val intents = Intent(context, RegistAutoService::class.java)
        intents.putExtra("PUSHID", pushId)
        context!!.startService(intents)
    }
}
