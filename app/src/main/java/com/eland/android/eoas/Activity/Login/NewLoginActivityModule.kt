package com.eland.android.eoas.Activity.Login

import dagger.Module
import com.eland.android.eoas.Service.LoginService
import dagger.Binds
import dagger.Provides



/**
 * Created by liuwenbin on 2018/1/5.
 * 虽然青春不在，但不能自我放逐.
 */

@Module
abstract class NewLoginActivityModule {

    @Binds
    internal abstract fun provideMainView(mainActivity: NewLoginActivity): LoginView

    @Module
    companion object {
        @Provides
        fun provideMainPresenter(mainView: LoginView, apiService: LoginService): NewLoginPresenter =  NewLoginPresenterImpl(mainView, apiService)
    }
}