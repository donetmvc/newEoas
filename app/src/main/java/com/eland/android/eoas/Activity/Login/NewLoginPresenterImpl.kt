package com.eland.android.eoas.Activity.Login

import com.eland.android.eoas.Service.LoginService
import javax.inject.Inject



/**
 * Created by liuwenbin on 2018/1/5.
 * 虽然青春不在，但不能自我放逐.
 */
class NewLoginPresenterImpl: NewLoginPresenter {
    var loginView: LoginView
    var apiService: LoginService

    @Inject
    constructor(loginView: LoginView, apiService: LoginService) {
        this.loginView = loginView
        this.apiService = apiService
    }

    override fun login(id: String, password: String, userName: String?, imei: String?) {
        loginView.showLoading()
        apiService.signIn(id, password, userName, imei)
    }

    override fun loadLogin() {

        //loginView.showLoading()
        //get data
    }
}