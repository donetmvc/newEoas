package com.eland.android.eoas.Http.dataService

import com.eland.android.eoas.Http.RetrofitManager
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.util.*

/**
 * Created by liuwenbin on 2017/12/28.
 * 虽然青春不在，但不能自我放逐.
 */
object WeatherDataService {
    fun <T> GetWetherList(apiKey: String, location: String, days: Int = 5, listener: IOnGetWetherListener, dataDto: Class<T>? = null) {
        val param: WeakHashMap<String, Any> = WeakHashMap()
        param["key"] = apiKey
        param["q"] = location
        param["days"] = days
        RetrofitManager.builder()
                .url("/v1/forecast.json")
                .param(param)
                .build()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: ResponseBody ->
                    val resultStr = result.string()
                    val obj = Gson().fromJson<T>(resultStr, dataDto)
                    listener.onGetWetherSuccess(obj as Any)
                }, {
                    listener.onGetWetherFailure()
                })
    }
}

interface IOnGetWetherListener {
    fun onGetWetherSuccess(result: Any)
    fun onGetWetherFailure()
}