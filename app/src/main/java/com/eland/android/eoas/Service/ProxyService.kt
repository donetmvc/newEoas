package com.eland.android.eoas.Service

import android.content.Context

import com.eland.android.eoas.Model.ProxyInfo
import com.eland.android.eoas.Model.ProxyUserInfo
import com.eland.android.eoas.Model.SaveProxyInfo
import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.UnsupportedEncodingException
import java.util.ArrayList

import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity

/**
 * Created by liu.wenbin on 16/1/6.
 */
class ProxyService(private val context: Context) {

    fun searchProxyInfo(userId: String, startDate: String, endDate: String, iOnSearchProxyInfoListener: IOnSearchProxyInfoListener) {
        val uri = "api/ActingEmp"

        val params = RequestParams()
        params.add("StartDate", startDate)
        params.add("EndDate", endDate)
        params.add("userId", userId)

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)

                val infoList = ArrayList<ProxyInfo>()
                var proxyInfo: ProxyInfo
                var userInfo: MutableList<ProxyUserInfo>// = new ArrayList<ProxyUserInfo>();
                var proxyUserInfo: ProxyUserInfo

                try {
                    if (null != response) {
                        for (i in 0 until response.length()) {
                            userInfo = ArrayList()
                            val obj = response.getJSONObject(i)
                            proxyInfo = ProxyInfo()
                            proxyInfo.OrgCode = obj.getString("OrganizationID")
                            proxyInfo.OrgName = obj.getString("OrganizationName")

                            val newArray = obj.getJSONArray("AgentInfo")

                            if (null != newArray && newArray.length() > 0) {
                                for (j in 0 until newArray.length()) {
                                    val newObj = newArray.getJSONObject(j)
                                    proxyUserInfo = ProxyUserInfo()
                                    proxyUserInfo.EmpId = newObj.getString("EmpID")
                                    proxyUserInfo.UserName = newObj.getString("UserName")

                                    userInfo.add(proxyUserInfo)
                                }
                            }

                            proxyInfo.proxyInfoList = userInfo
                            infoList.add(proxyInfo)
                        }
                    }

                    iOnSearchProxyInfoListener.onSuccess(infoList)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnSearchProxyInfoListener.onFailure(99999, "连接服务器超时")
            }


            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                iOnSearchProxyInfoListener.onFailure(99999, "连接服务器超时")
            }
        })

    }

    fun saveProxy(list: List<SaveProxyInfo>, iOnSearchProxyInfoListener: IOnSearchProxyInfoListener) {
        val uri = "api/ActingEmp"

        val array = JSONArray()
        var obj: JSONObject
        var stringEntity: StringEntity? = null
        for (i in list.indices) {
            obj = JSONObject()
            try {
                obj.put("empId", list[i].empId)
                obj.put("orgId", list[i].orgId)
                obj.put("startDate", list[i].startDate)
                obj.put("endDate", list[i].endDate)
                obj.put("userId", list[i].userId)
                array.put(obj)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        try {
            stringEntity = StringEntity(array.toString())
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        HttpRequstUtil.post(context, uri, stringEntity!!, object : JsonHttpResponseHandler() {

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnSearchProxyInfoListener.onFailure(99999, "连接服务器超时")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)

                if (responseString == "\"OK\"") {
                    iOnSearchProxyInfoListener.onFailure(1000, "保存成功")
                } else {
                    iOnSearchProxyInfoListener.onFailure(99999, "连接服务器超时")
                }
            }
        })
    }

    fun cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true)
    }

    interface IOnSearchProxyInfoListener {
        fun onSuccess(list: List<ProxyInfo>)
        fun onFailure(code: Int, msg: String)
    }
}
