package com.eland.android.eoas.Service


import android.content.Context

import com.eland.android.eoas.Application.EOASApplication
import com.eland.android.eoas.Model.LoginInfo
import com.eland.android.eoas.Util.HttpRequstUtil
import com.eland.android.eoas.Util.SystemMethodUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 15/12/16.
 */
class ContactService(private val context: Context) {

    fun searchContact(userId: String, iOnSearchContactListener: IOnSearchContactListener) {
        val uri = "api/contact"

        val params = RequestParams()
        params.put("userId", userId)
        params.put("type", "123")
        params.put("tips", "321")

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)

                val list = ArrayList<LoginInfo>()
                var dto: LoginInfo? = null

                if (null != response && response.length() > 0) {
                    try {
                        for (i in 0 until response.length()) {
                            val `object` = response.getJSONObject(i)
                            val userIds = `object`.getString("userIDField")
                            val cellNo = `object`.getString("cellNoField")
                            val backCellNo = `object`.getString("backCellNoField")
                            val backName = `object`.getString("backNameField")

                            //没名没姓没电话号码的一律不显示作为惩戒，任性。
                            if (userIds.isEmpty() || userIds == "null"
                                    || userIds == userId
                                    || cellNo == "null") {
                                continue
                            }

                            dto = LoginInfo()
                            dto.userId = userIds
                            dto.cellNo = cellNo
                            dto.userName = `object`.getString("userNameField")
                            dto.email = userIds + "@eland.co.kr"
                            dto.backCellNo = backCellNo
                            dto.backName = backName
                            dto.url = EOASApplication.instance!!.photoUri + userIds.replace(".", "") + ".jpg"


                            //list中已存在的人员不再添加
                            if (SystemMethodUtil.isContains(list, userIds)!!) {
                                continue
                            }

                            list.add(dto)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    iOnSearchContactListener.onSearchSuccess(list)
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnSearchContactListener.onSearchFailure(99999, "连接服务器失败")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnSearchContactListener.onSearchFailure(99999, "连接服务器失败")
            }
        })
    }

    fun cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true)
    }

    interface IOnSearchContactListener {
        fun onSearchSuccess(list: ArrayList<LoginInfo>)
        fun onSearchFailure(code: Int, msg: String)
    }

}
