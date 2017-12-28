package com.eland.android.eoas.Service

import android.content.Context
import com.eland.android.eoas.Model.WetherData

import com.eland.android.eoas.Model.WetherInfo
import com.eland.android.eoas.Util.HttpRequstUtil
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import cz.msebera.android.httpclient.Header

/**
 * Created by liu.wenbin on 2016/2/24.
 */
class WetherService(private val context: Context?) {

    fun getWether(lng: String, lat: String, iOnGetWetherListener: IOnGetWetherListener) {

        val uri = "api/Wether"

        val params = RequestParams()
        params.add("lng", lng)
        params.add("lat", lat)

        HttpRequstUtil.get(context!!, uri, params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                super.onSuccess(statusCode, headers, response)

                val dto = WetherInfo()

                if (null != response) {
                    try {
                        dto.sd = response.getString("sd")
                        dto.hightTemp = response.getString("hightTemp")
                        dto.lowTemp = response.getString("lowTemp")
                        dto.temperature = response.getString("temperature")
                        //                        dto.temperature_time = response.getString("temperature_time");
                        dto.weather = response.getString("weather")
                        dto.wind_direction = response.getString("wind_direction")
                        dto.wind_power = response.getString("wind_power")
                        dto.weather_pic = response.getString("weather_pic")

                        iOnGetWetherListener.onGetSucess(dto)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        iOnGetWetherListener.onGetFailure(99999, "")
                    }

                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                iOnGetWetherListener.onGetFailure(99999, "")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                super.onFailure(statusCode, headers, responseString, throwable)
                iOnGetWetherListener.onGetFailure(99999, "")
            }
        })

    }

    fun cancelRequest() {
        if (null != context) {
            HttpRequstUtil.cancelSingleRequest(context, true)
        }
    }

    interface IOnGetWetherListener {
        fun onGetSucess(weather: WetherInfo)
        fun onGetSucess(weather: WetherData)
        fun onGetFailure(code: Int, message: String)
    }
}
