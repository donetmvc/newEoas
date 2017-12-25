package com.eland.android.eoas.Http.IService

import com.eland.android.eoas.Http.RequestBody.RequestObject
import com.eland.android.eoas.Model.ApplyListInfo

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by liuwenbin on 2017/10/25.
 * 虽然青春不在，但也不能自我放逐
 */

interface ApplyListService {
    @GET("api/Apply")
    fun getAllVacationApply(@Body request: RequestObject): Observable<ApplyListInfo>
}
