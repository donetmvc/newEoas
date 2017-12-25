package com.eland.android.eoas.Service

import android.content.Context

import com.eland.android.eoas.Model.ScheduleInfo
import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 15/12/25.
 */
class SearchScheduleService(private val context: Context) {

    fun searchSchedule(userId: String, startDate: String, endDate: String, iOnSearchScheduleListener: IOnSearchScheduleListener) {
        val uri = "api/ADM"

        val params = RequestParams()
        params.add("userId", userId)
        params.add("startDate", startDate)
        params.add("endDate", endDate)
        params.add("app", "")

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)

                val list = ArrayList<ScheduleInfo>()
                var dto: ScheduleInfo? = null

                if (null != response && response.length() > 0) {
                    try {
                        for (i in 0 until response.length()) {
                            val obj = response.getJSONObject(i)
                            dto = ScheduleInfo()
                            dto.work = if (obj.getString("work") == "null") "" else obj.getString("work")
                            dto.workdes = if (obj.getString("workdes") == "null") "" else obj.getString("workdes")
                            dto.offwork = if (obj.getString("offwork") == "null") "" else obj.getString("offwork")
                            dto.offworkdes = if (obj.getString("offworkdes") == "null") "" else obj.getString("offworkdes")
                            dto.date = obj.getString("date")

                            list.add(dto)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

                iOnSearchScheduleListener.onSuccess(list)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnSearchScheduleListener.onFailure(9999, "连接服务器超时")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                iOnSearchScheduleListener.onFailure(9999, "连接服务器超时")
            }
        })

    }

    fun cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true)
    }

    interface IOnSearchScheduleListener {
        fun onSuccess(list: List<ScheduleInfo>)
        fun onFailure(code: Int, msg: String)
    }

}
