package com.eland.android.eoas.Service

import android.content.Context

import com.eland.android.eoas.Model.ApproveListInfo
import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 16/1/7.
 */
class ApproveListService(private val context: Context) {

    fun searchApproveList(userId: String, onApproveListListener: IOnApproveListListener?) {
        val uri = "api/Push"
        val params = RequestParams("userId", userId)

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)

                val list = ArrayList<ApproveListInfo>()
                var dto: ApproveListInfo
                var obj: JSONObject

                if (response != null) {
                    try {
                        for (i in 0 until response.length()) {
                            dto = ApproveListInfo()
                            obj = response.getJSONObject(i)
                            dto.vacationType = obj.getString("vacationType")
                            dto.applyUserName = obj.getString("applyUserName")
                            dto.applyId = obj.getString("applyID")
                            dto.applyPeriod = obj.getString("applyPeriod")
                            dto.reason = obj.getString("reason")
                            dto.vacationDays = obj.getString("vacationDays")
                            dto.vacationTypeName = obj.getString("vacationTypeName")

                            list.add(dto)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

                onApproveListListener?.onSuccess(list)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                onApproveListListener?.onFailure(99999, "连接服务器超时")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                onApproveListListener?.onFailure(99999, "连接服务器超时")
            }
        })
    }

    fun cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true)
    }

    interface IOnApproveListListener {
        fun onSuccess(list: List<ApproveListInfo>)
        fun onFailure(code: Int, msg: String)
    }
}
