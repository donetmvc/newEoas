package com.eland.android.eoas.Http.IService

import com.eland.android.eoas.Model.WetherData
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by liuwenbin on 2017/12/28.
 * 虽然青春不在，但不能自我放逐.
 */

interface WetherService {
    @GET("/v1/forecast.json")
    fun GetWether(@Query("key") apiKey: String, @Query("q") q: String, @Query("days") days : Int) : Observable<WetherData>
}
