package com.eland.android.eoas.Http.Api

import com.eland.android.eoas.Http.HttpManager


/**
 * Created by liuwenbin on 2017/10/27.
 * 虽然青春不在，但也不能自我放逐
 */

class BaseApi {
    init {
    }

    inner class ChildApi(val child:String, childs: String) {
        private var towChild:String? = null

        init {
            print(childs) //or print(child)
            this.towChild = childs
        }

        fun print() {
            print(child) //can not access childs
            print(this.towChild)
            print(_baseUri)
        }
    }

    companion object {
        private val _baseUri: String = "http://api.apixu.com"
        private val _timeOut: Long = 10
        private var httpManange: HttpManager? = null

        val newInstanc:HttpManager
            @Synchronized get() {
                if(httpManange == null) {
                    httpManange = HttpManager()
                }

                return httpManange!!
            }
    }

}
