package com.eland.android.eoas.Util

import android.content.Context

import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.entity.StringEntity

/**
 * Created by liu.wenbin on 15/11/10.
 */
object HttpRequstUtil {

    //开发测试地址
    //private static final String baseURL = "http://10.202.101.11:30002/";
    //正式运营地址
    private val baseURL = "http://182.92.65.253:30001/"

    private val client = AsyncHttpClient()

    init {
        client.setMaxRetriesAndTimeout(2, 1000)
    }

    operator fun get(url: String, params: RequestParams, response: JsonHttpResponseHandler) {
        client.get(getAbsoluteUri(url), params, response)
    }

    operator fun get(context: Context, url: String, params: RequestParams, response: JsonHttpResponseHandler) {
        client.get(context, getAbsoluteUri(url), params, response)
    }

    fun post(context: Context, url: String, stringEntity: StringEntity, response: JsonHttpResponseHandler) {
        client.post(context, getAbsoluteUri(url), stringEntity, "application/json", response)
    }

    fun post(context: Context, url: String, params: RequestParams, responseHandler: AsyncHttpResponseHandler) {
        client.post(context, getAbsoluteUri(url), params, responseHandler)
    }

    operator fun get(url: String, params: RequestParams, responseHandler: AsyncHttpResponseHandler) {
        client.get(getAbsoluteUri(url), params, responseHandler)
    }

    fun cancelSingleRequest(context: Context, cancel: Boolean) {
        client.cancelRequests(context, cancel)
    }

    private fun getAbsoluteUri(url: String): String {
        return baseURL + url
    }
}
