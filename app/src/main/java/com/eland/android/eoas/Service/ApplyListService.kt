package com.eland.android.eoas.Service

import android.content.Context

import com.eland.android.eoas.Model.ApplyListInfo
import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 15/12/29.
 */
class ApplyListService(private val context: Context) {

    fun searchApplyList(userId: String, startDate: String, endDate: String, iOnSearchApplyListListener: IOnSearchApplyListListener) {
        val uri = "api/Apply"

        val params = RequestParams()
        params.add("userId", userId)
        params.add("startDate", startDate)
        params.add("endDate", endDate)
        params.add("searchType", "01") //休假类型

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)

                val list = ArrayList<ApplyListInfo>()
                var dto: ApplyListInfo? = null

                if (null != response && response.length() > 0) {
                    try {
                        for (i in response.length() - 1 downTo 0) {
                            dto = ApplyListInfo()
                            val `object` = response.getJSONObject(i)

                            dto.processStateCode = `object`.getString("ProcessStateCode")
                            dto.vacationTypeName = `object`.getString("VacationTypeName")
                            dto.vacationDays = `object`.getString("VacationDays")
                            dto.vacationPeriod = `object`.getString("VacationPeriod")
                            dto.remarks = `object`.getString("Remarks")
                            dto.vacationNo = `object`.getString("VacationNo")
                            list.add(dto)
                        }
                    } catch (e: JSONException) {
                        iOnSearchApplyListListener.onSearchFailure(99999, "数据异常")
                        e.printStackTrace()
                    }

                }

                iOnSearchApplyListListener.onSearchSuccess(list)
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnSearchApplyListListener.onSearchFailure(99999, "连接服务器超时")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnSearchApplyListListener.onSearchFailure(99999, "连接服务器超时")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                iOnSearchApplyListListener.onSearchFailure(99999, "连接服务器超时")
            }

        })
    }

    fun cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true)
    }

    interface IOnSearchApplyListListener {
        fun onSearchSuccess(list: List<ApplyListInfo>)
        fun onSearchFailure(code: Int, msg: String)
    }

}
