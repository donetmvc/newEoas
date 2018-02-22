package com.eland.android.eoas.Service

import android.content.Context
import android.os.Message

import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams

import java.io.File
import java.io.FileNotFoundException

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 15/12/8.
 */
class UploadFileService(private val context: Context) {
    private var iOnUploadListener: IOnUploadListener? = null

    fun uploadFile(path: String, fileName: String, message: Message) {

        val uri = "api/UploadImage"

        val myFile = File(path)
        if(!myFile.exists()) throw NullPointerException("File didn't exists!")
        val params = RequestParams()
        try {
            params.put(fileName, myFile)

            HttpRequstUtil.post(context, uri, params, object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                    if (null != iOnUploadListener) {
                        message.what = 0
                        iOnUploadListener!!.onUploadSuccess(message)
                    }
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                    if (null != iOnUploadListener) {
                        message.what = 1
                        iOnUploadListener!!.onUploadFailure(message)
                    }
                }
            })
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    fun setUploadListener(iOnUploadListener: IOnUploadListener) {
        this.iOnUploadListener = iOnUploadListener
    }

    fun cancel() {
        HttpRequstUtil.cancelSingleRequest(context, true)
    }

    interface IOnUploadListener {
        fun onUploadSuccess(message: Message)
        fun onUploadFailure(message: Message)
    }
}
