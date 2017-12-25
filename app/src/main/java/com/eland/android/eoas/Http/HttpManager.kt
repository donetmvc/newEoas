package com.eland.android.eoas.Http

import com.eland.android.eoas.Application.EOASApplication
import org.reactivestreams.Subscriber
import java.io.File
import java.util.concurrent.TimeUnit
import io.reactivex.Observable
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * Created by liuwenbin on 2017/10/25.
 * 虽然青春不在，但也不能自我放逐
 */

class HttpManager {

    private val baseUri = "http://182.92.65.253:30001/"
    private var _retrofit: Retrofit? = null
    private val _observable: Observable<*>? = null

    init {
        if (null == _retrofit) {
            init()
        }
    }

    private fun init() {
        val cacheDirectory = File(EOASApplication.Companion.instance.applicationContext //EOASApplication.instance.applicationContext
                .cacheDir.absolutePath, "HttpCache")

        val builder = OkHttpClient.Builder()
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.cache(Cache(cacheDirectory, (10 * 1024 * 1024).toLong()))

        /*创建retrofit对象*/
        _retrofit = Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUri)
                .build()


        //        _observable = getObservable(_retrofit)
        //                /*失败后的retry配置*/
        //                //.retryWhen(new RetryWhenNetworkException())
        //                /*生命周期管理*/
        //                //.compose(basePar.getRxAppCompatActivity().bindToLifecycle())
        //                /*http请求线程*/
        //                .subscribeOn(Schedulers.io())
        //                .unsubscribeOn(Schedulers.io())
        //                /*回调线程*/
        //                .observeOn(AndroidSchedulers.mainThread());
        /*结果判断*/
        //.map(basePar);

        /*数据回调*/
        //observable.subscribe(subscriber)
    }

    fun request(subscriberf: Subscriber<String>) {
        //_observable.subscribe(subscriber);
    }

    interface getObservable// onGetObservable()

    companion object {
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
    }

}
