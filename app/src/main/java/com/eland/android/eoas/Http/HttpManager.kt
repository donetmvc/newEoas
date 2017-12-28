package com.eland.android.eoas.Http

import com.eland.android.eoas.Application.EOASApplication
import com.eland.android.eoas.BuildConfig
import okhttp3.Cache
import java.io.File
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by liuwenbin on 2017/10/25.
 * 虽然青春不在，但也不能自我放逐
 */

class HttpManager {

    companion object {
        val baseUri = "http://api.apixu.com"

        @Volatile
        var instance: HttpManager? = null
        get() {
            if(instance == null) {
                synchronized(HttpManager::class.java) {
                    if(instance == null) {
                        instance = HttpManager()
                    }
                }
            }
            return instance!!
        }

        val cacheDirectory = File(EOASApplication.Companion.instance.applicationContext //EOASApplication.instance.applicationContext
                .cacheDir.absolutePath, "HttpCache")

        val client: OkHttpClient = OkHttpClient().newBuilder()
                .cache(Cache(cacheDirectory, (10 * 1024 * 1024).toLong()))
                .addInterceptor{
                    chain ->
                        val original = chain.request()
                    // See [http://developer.dribbble.com/v1/#authentication] for more information.
                    val requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
//                            .header("Authorization", "Bearer" + " " + mLastToken)  //add token
                            .method(original.method(), original.body())
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                }).build()

        val retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUri)
                .build()
    }

}
