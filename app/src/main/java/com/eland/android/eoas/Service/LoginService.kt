package com.eland.android.eoas.Service

import android.content.Context

import com.eland.android.eoas.Model.LoginInfo
import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import org.json.JSONException
import org.json.JSONObject

import cz.msebera.android.httpclient.Header


/**
 * Created by liu.wenbin on 15/11/10.
 */
class LoginService(private val context: Context) {
    var isignInListener: ISignInListener? = null

    fun signIn(loginId: String, password: String, username: String, imei: String) {
        val uri = "api/NewLogin"

        val params = RequestParams()
        params.put("userId", loginId)
        params.put("password", password)
        params.put("imei", imei)

        HttpRequstUtil.get(context, uri, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                super.onSuccess(statusCode, headers, response)

                if (null != isignInListener) {
                    try {
                        val code = Integer.valueOf(response!!.getString("code"))!!
                        val cellNo = response.getString("cellNo")
                        val message = response.getString("message")
                        val userName = response.getString("userName")
                        val email = response.getString("email")
                        if (code == 1000) {
                            val info = LoginInfo()
                            info.cellNo = cellNo
                            info.userName = userName
                            info.email = email
                            isignInListener!!.onSignInSuccess(info)
                        } else {
                            isignInListener!!.onSignInFailure(code, message)
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                if (null != isignInListener) {
                    isignInListener!!.onSignInFailure(99999, "")
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                if (null != isignInListener) {
                    isignInListener!!.onSignInFailure(99999, "")
                }

            }
        })
    }

    fun setOnSignInListener(isignInListener: ISignInListener) {
        this.isignInListener = isignInListener
    }

    fun cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true)
    }

    interface ISignInListener {
        fun onSignInSuccess(info: LoginInfo)
        fun onSignInFailure(code: Int, msg: String)
    }
}
