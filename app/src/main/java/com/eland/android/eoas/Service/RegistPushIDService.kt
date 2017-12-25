package com.eland.android.eoas.Service

import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONException
import org.json.JSONObject

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 15/12/18.
 */
class RegistPushIDService {

    fun registPush(pushId: String, imei: String, iOnRegistListener: IOnRegistListener) {
        val uri = "api/NewLogin"
        val params = RequestParams()
        params.add("pushId", pushId)
        params.add("imei", imei)

        HttpRequstUtil.get(uri, params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                super.onSuccess(statusCode, headers, response)

                try {
                    val message = response!!.getString("message")

                    if (message == "OK") {
                        iOnRegistListener.onRegistSuccess()
                    } else {
                        iOnRegistListener.onRegistFailure()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }


            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnRegistListener.onRegistFailure()
            }

        })
    }

    interface IOnRegistListener {
        fun onRegistSuccess()
        fun onRegistFailure()
    }

}
