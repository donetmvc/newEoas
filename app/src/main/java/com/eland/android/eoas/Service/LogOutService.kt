package com.eland.android.eoas.Service

import android.app.Service
import android.content.Intent
import android.os.IBinder

import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONObject

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 15/12/18.
 */
class LogOutService : Service() {

    private var imei: String? = null
    private var userId = ""

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (null != intent) {
            userId = intent.getStringExtra("USERID")
            imei = intent.getStringExtra("IMEI")

            startLogOut(userId, imei)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startLogOut(userId: String, imei: String?) {
        val uri = "api/NewLogin"
        val params = RequestParams()
        params.add("userId", userId)
        params.add("imei", imei)
        params.add("type", "123")
        params.add("tips", "321")


        HttpRequstUtil.get(uri, params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                super.onSuccess(statusCode, headers, response)
                stopSelf()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                stopSelf()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
