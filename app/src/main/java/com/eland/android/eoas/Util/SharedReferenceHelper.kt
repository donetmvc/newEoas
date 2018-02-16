package com.eland.android.eoas.Util

import android.content.Context
import android.content.SharedPreferences

import com.eland.android.eoas.Model.Constant

/**
 * Created by liu.wenbin on 15/11/10.
 */
class SharedReferenceHelper//实例化
(context: Context) {

    init {
        sharedPreferences = context.getSharedPreferences(Constant.GLOBLE_SETTING, Context.MODE_PRIVATE)
    }

    //设置缓存值
    fun setValue(key: String, value: String?) {
        sharedPreferences?.edit()?.putString(key, value)?.apply()
    }

    //获取缓存值
    fun getValue(key: String): String {
        return sharedPreferences!!.getString(key, "")
    }

    //清理缓存的数据
    fun clear() {
        sharedPreferences?.edit()?.clear()?.apply()
    }

    companion object {
        private var sharedPreferencesHelper: SharedReferenceHelper? = null
        private var sharedPreferences: SharedPreferences? = null

        fun getInstance(context: Context): SharedReferenceHelper {
            if (sharedPreferencesHelper == null) {
                synchronized(SharedReferenceHelper::class.java) {
                    sharedPreferencesHelper = SharedReferenceHelper(context)
                }
            }
            return sharedPreferencesHelper!!
        }
    }
}
