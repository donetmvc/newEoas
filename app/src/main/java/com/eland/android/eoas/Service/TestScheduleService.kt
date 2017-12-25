package com.eland.android.eoas.Service

import android.content.Context

import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONObject

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 16/1/22.
 * test android-async-http cancel
 */
class TestScheduleService(private val context: Context) {

    fun regSchedule(imei: String, isAM: String, iOnTestScheduleListener: IOnTestScheduleListener?) {
        val uri = "api/adm"
        val params = RequestParams()
        params.put("iemi", imei)
        params.put("registTime", "")
        params.put("isAM", isAM)

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)

                iOnTestScheduleListener?.onFailuer(99999, "无法连接服务器.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)

                if (null != iOnTestScheduleListener) {
                    if (responseString == "\"OK\"") {
                        iOnTestScheduleListener.onSuccess()
                    } else {
                        iOnTestScheduleListener.onFailuer(44444, responseString)
                    }
                }

            }
        })
    }

    fun cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true)
    }

    interface IOnTestScheduleListener {
        fun onSuccess()
        fun onFailuer(code: Int, msg: String?)
    }
}
