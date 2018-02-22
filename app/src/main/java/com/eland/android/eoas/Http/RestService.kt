package com.eland.android.eoas.Http

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*
import java.util.*

/**
 * Created by liuwenbin on 18/2/15.
 */
interface RestService {

    @Headers("Content-type: application/json;")
    @GET
    fun get(@Url url: String, @QueryMap params: WeakHashMap<String, @JvmSuppressWildcards Any>): Observable<ResponseBody>
}