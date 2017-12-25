package com.eland.android.eoas.Service

import android.content.Context

import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 16/1/7.
 */
class ApproveService(private val context: Context) {

    fun searchApplyInfo(userId: String, applyId: String, iOnApproveListener: IOnApproveListener) {
        val uri = "api/Push"
        val params = RequestParams()
        params.add("userId", userId)
        params.add("applyId", applyId)

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)

                var content = ""
                var reason = ""

                try {
                    if (null != response && null != response.getJSONObject(0)) {
                        val obj = response.getJSONObject(0)
                        content = ("【" + obj.getString("applyUserName") + "】"
                                + "申请了" + "【" + obj.getString("startDate")
                                + "】~【" + obj.getString("endDate") + "】的【" + obj.getString("vacationTypeName") + "】")
                        reason = obj.getString("reason")
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                iOnApproveListener.onApproveSucess(content, reason)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnApproveListener.onApproveFailure(99999, "连接服务器超时")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                iOnApproveListener.onApproveFailure(99999, "连接服务器超时")
            }
        })
    }

    fun saveApprove(userId: String, approveCode: String, approveReason: String, applyId: String, iOnApproveListener: IOnApproveListener) {
        val uri = "api/Push"
        val params = RequestParams()
        params.add("userId", userId)
        params.add("applyId", applyId)
        params.add("approveStateCode", approveCode)
        params.add("approveStateReason", approveReason)

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnApproveListener.onApproveFailure(99999, "连接服务器超时")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)

                if (!responseString!!.isEmpty() && responseString == "\"审批执行成功\"") {
                    iOnApproveListener.onApproveSucess("OK", approveCode)
                } else {
                    iOnApproveListener.onApproveFailure(99999, "审批失败，请重试")
                }
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {
                super.onSuccess(statusCode, headers, responseString)
                iOnApproveListener.onApproveFailure(99999, "连接服务器超时")
            }
        })
    }

    fun cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true)
    }

    interface IOnApproveListener {
        fun onApproveSucess(content: String, reason: String)
        fun onApproveFailure(code: Int, msg: String)
    }
}
