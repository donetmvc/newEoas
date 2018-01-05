package com.eland.android.eoas.Activity.Login

/**
 * Created by liuwenbin on 2018/1/5.
 * 虽然青春不在，但不能自我放逐.
 */
interface NewLoginPresenter {
    fun loadLogin()

    fun login(id: String, password: String, userName: String? = "", imei: String? = "")
}