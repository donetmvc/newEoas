package com.eland.android.eoas.Service

import com.eland.android.eoas.Util.ConsoleUtil
import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONObject

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 15/12/9.
 */
class CheckRegScheduleService {

    private val TAG = "EOAS"
    private val iOnCheckListener: IOnCheckListener? = null

    fun check(imei: String, iOnCheckListener: IOnCheckListener) {
        val uri = "api/adm"

        val params = RequestParams()
        params.put("userName", imei)
        params.put("type", "01")
        params.put("app", "1")

        HttpRequstUtil.get(uri, params, object : JsonHttpResponseHandler() {

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                ConsoleUtil.i(TAG, "--------ACTION:------------" + "server error.")
                iOnCheckListener.onCheckFailure()
            }


            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                ConsoleUtil.i(TAG, "--------ACTION:------------" + "request success.")

                if (responseString == "\"EMPTY\"") {
                    iOnCheckListener.onCheckSuccess("EMPTY")
                } else {
                    iOnCheckListener.onCheckSuccess("NOTEMPTY")
                }
            }

        })
    }

    interface IOnCheckListener {
        fun onCheckSuccess(msg: String)
        fun onCheckFailure()
    }
}
