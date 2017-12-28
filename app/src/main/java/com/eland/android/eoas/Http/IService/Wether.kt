package com.eland.android.eoas.Http.IService

import com.eland.android.eoas.Http.HttpManager
import com.eland.android.eoas.Model.WetherData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.eland.android.eoas.Service.WetherService.*

/**
 * Created by liuwenbin on 2017/12/28.
 * 虽然青春不在，但不能自我放逐.
 */
class Wether {

    fun GetWetherList(apiKey: String, location: String, days: String, listener: IOnGetWetherListener) {
        val observable = wetherService.GetWether(apiKey, location, days = 5)

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: WetherData ->
                    listener.onGetSucess(result)
                }, {
                    listener.onGetFailure(9999, "")
                })
    }

    companion object {
        val wetherService: WetherService = HttpManager.retrofit.create(WetherService::class.java)
    }
}