package com.eland.android.eoas.Service

import android.content.Context

import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONObject

import cz.msebera.android.httpclient.Header

/**
 * Created by liuw.wenbin on 15/12/1.
 */
class ScheduleService(private val context: Context?) {
    private var iScheduleListener: IScheduleListener? = null

    fun regSchedulePM(imei: String, isAM: String) {
        val uri = "api/adm"
        val params = RequestParams()
        params.put("iemi", imei)
        params.put("registTime", "")
        params.put("isAM", isAM)

        HttpRequstUtil.get(uri, params, object : JsonHttpResponseHandler() {

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)

                if (null != iScheduleListener) {
                    iScheduleListener!!.onScheduleFailure(99999, "无法连接服务器.")
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)

                if (null != iScheduleListener) {
                    if (responseString == "\"OK\"") {
                        iScheduleListener!!.onScheduleSuccess()
                    } else {
                        iScheduleListener!!.onScheduleFailure(44444, responseString)
                    }
                }

            }
        })
    }

    fun regScheduleAM(imei: String, isAM: String) {
        val uri = "api/adm"
        val params = RequestParams()
        params.put("iemi", imei)
        params.put("registTime", "")
        params.put("isAM", isAM)

        HttpRequstUtil.get(uri, params, object : JsonHttpResponseHandler() {

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)

                if (null != iScheduleListener) {
                    iScheduleListener!!.onScheduleFailure(99999, "无法连接服务器.")
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)

                if (null != iScheduleListener) {
                    if (responseString == "\"OK\"") {
                        iScheduleListener!!.onScheduleSuccess()
                    } else {
                        iScheduleListener!!.onScheduleFailure(44444, responseString)
                    }
                }

            }
        })
    }

    fun cancel() {
        if (null != context) {
            HttpRequstUtil.cancelSingleRequest(context, true)
        }
    }

    fun setOnScheduleListener(iScheduleListener: IScheduleListener) {
        this.iScheduleListener = iScheduleListener
    }

    interface IScheduleListener {
        fun onScheduleSuccess()
        fun onScheduleFailure(code: Int, msg: String?)
    }
}
