package com.eland.android.eoas.di

import com.eland.android.eoas.Activity.Login.NewLoginActivity
import com.eland.android.eoas.Activity.Login.NewLoginActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector



/**
 * Created by liuwenbin on 2018/1/5.
 * 虽然青春不在，但不能自我放逐.
 */

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [(NewLoginActivityModule::class)])
    abstract fun bindNewLoginActivity(): NewLoginActivity
}