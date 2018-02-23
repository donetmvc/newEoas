package com.eland.android.eoas.Service

import android.content.Context

import com.eland.android.eoas.Util.ConsoleUtil
import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONArray
import org.json.JSONObject

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 15/12/30.
 */
class ApplyService(private val context: Context) {

    var action = { type: Int, code: Int, result: JSONArray? -> Unit }


    fun searchVacationType(userId: String, iOnApplyListener: IOnApplyListener) {
        val uri = "api/Apply"

        val params = RequestParams()
        params.add("userId", userId)
        params.add("type", "02")

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
//                iOnApplyListener.onSuccess(response)
                action(2, 200, response)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                action(1, 99999, null)
//                iOnApplyListener.onFailure(1, 99999, "连接服务器超时")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
//                iOnApplyListener.onFailure(1, 99999, "连接服务器超时")
                action(1, 99999, null)
            }

        })
    }

    fun searchVacationDays(userId: String, iOnApplyListener: IOnApplyListener) {
        val uri = "api/Apply"

        val params = RequestParams()
        params.add("userId", userId)

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                super.onSuccess(statusCode, headers, response)
                iOnApplyListener.onSuccess(response)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnApplyListener.onFailure(2, 99999, "连接服务器超时")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                iOnApplyListener.onFailure(2, 99999, "连接服务器超时")
            }
        })
    }

    fun searchDiffDays(userId: String, startDate: String, endDate: String,
                       startTime: String, endTime: String, vacationType: String,
                       iOnApplyListener: IOnApplyListener) {
        val uri = "api/Time"
        val params = RequestParams()
        params.add("userId", userId)
        params.add("startDate", startDate)
        params.add("endDate", endDate)
        params.add("startTime", startTime)
        params.add("endTime", endTime)
        params.add("vacationType", vacationType)

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                super.onSuccess(statusCode, headers, response)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnApplyListener.onFailure(3, 99999, "连接服务器超时")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                iOnApplyListener.onSuccess(responseString)
            }
        })
    }

    fun saveApply(userId: String, startDate: String, endDate: String, startTime: String, endTime: String,
                  vacationType: String, vacationDate: String, remark: String,
                  iOnApplyListener: IOnApplyListener) {
        val uri = "api/Apply"
        val params = RequestParams()
        params.add("s1", startDate)
        params.add("s2", endDate)
        params.add("s3", startTime)
        params.add("s4", endTime)
        params.add("s5", vacationType)
        params.add("s6", vacationDate)
        params.add("s7", userId)
        params.add("s8", remark)

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnApplyListener.onFailure(4, 99999, "连接服务器超时")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                iOnApplyListener.onSuccess(4, responseString)
            }

        })

    }

    fun searchApprogressList(userId: String, applyId: String, iOnApplyListener: IOnApplyListener) {
        val uri = "api/ADM"
        val params = RequestParams()
        params.add("userId", userId)
        params.add("applyId", applyId)

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
                iOnApplyListener.onSuccess(response)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnApplyListener.onFailure(1, 99999, "连接服务器超时")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                iOnApplyListener.onFailure(1, 99999, "连接服务器超时")
            }
        })
    }

    fun cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true)
    }

    interface IOnApplyListener {
        fun onSuccess(obj: JSONObject?)
        fun onSuccess(array: JSONArray?)
        fun onSuccess(diffDays: String?)
        fun onSuccess(type: Int, msg: String?)
        fun onFailure(type: Int, code: Int, msg: String)
    }

    companion object {

        private val TAG = "EOAS"
    }

}
