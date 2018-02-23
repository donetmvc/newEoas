package com.eland.android.eoas.Http

import com.eland.android.eoas.Application.EOASApplication
import com.eland.android.eoas.BuildConfig
import io.reactivex.Observable
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.*

/**
 * Created by liuwenbin on 18/2/15.
 */

class RetrofitManager {

    companion object {

        private lateinit var mUrl: String
        private lateinit var mParams: WeakHashMap<String, Any>

        /**登记式/静态内部类
         *这种方式能达到双检锁方式一样的功效，但实现更简单。对静态域使用延迟初始化，应使用这种方式而不是双检锁方式。
         * 这种方式只适用于静态域的情况，双检锁方式可在实例域需要延迟初始化时使用
         * */
        fun builder() = RetrofitManagerBuilder

        object RetrofitManagerBuilder {
            fun url(url: String): RetrofitManagerBuilder {
                mUrl = url
                return this
            }

            fun param(params:  WeakHashMap<String, Any>): RetrofitManagerBuilder {
                mParams = params
                return this
            }

            fun build(): Observable<ResponseBody> {
                return   RetrofitHolder.service
            }

        }

        object RetrofitHolder {
            private val RETROFITINSTANCE = Retrofit.Builder()
                    .baseUrl("http://api.apixu.com")
                    .client(OkhttpHolder.OKHTTPINSTANCE)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            var service = RETROFITINSTANCE.create(RestService::class.java).get(mUrl, mParams)
        }

        object OkhttpHolder {
            val OKHTTPINSTANCE = OkHttpClient.Builder()
                    .cache(Cache(File(EOASApplication.applicationContext().cacheDir.absoluteFile, "HttpCache"), (10 * 1024 * 1024).toLong()))
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
                    }).build()!!

            //        val client = OkHttpClient()
//        val request = Request.Builder()
//                .url("")
//                .build()
//        val call = client.newCall(request)
//        call.execute()
//        call.enqueue(object : Callback {
//            override fun onResponse(call: Call?, response: Response?) {
//
//            }
//
//            override fun onFailure(call: Call?, e: IOException?) {
//
//            }
//        })
        }

    }

}