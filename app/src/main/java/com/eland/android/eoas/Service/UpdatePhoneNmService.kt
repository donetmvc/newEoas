package com.eland.android.eoas.Service

import android.content.Context

import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONObject

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 15/12/8.
 */
class UpdatePhoneNmService(private val context: Context) {

    private val iOnUpdateListener: IOnUpdateListener? = null

    fun updateCellNo(userId: String, cellNo: String, iOnUpdateListener: IOnUpdateListener?) {

        val uri = "api/Contact/"

        val params = RequestParams()
        params.put("userId", userId)
        params.put("cellNo", cellNo)

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnUpdateListener?.onUpdateFailure(99999, "")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                if (null != iOnUpdateListener) {

                    if (responseString == "\"OK\"") {
                        iOnUpdateListener.onUpdateSuccess(cellNo)
                    } else {
                        iOnUpdateListener.onUpdateFailure(1000, responseString)
                    }
                }
            }
        })

    }

    fun cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true)
    }

    interface IOnUpdateListener {
        fun onUpdateSuccess(number: String)
        fun onUpdateFailure(code: Int, msg: String?)
    }

}
