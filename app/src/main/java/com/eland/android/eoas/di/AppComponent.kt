package com.eland.android.eoas.di

import android.app.Application
import com.eland.android.eoas.Application.EOASApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


/**
 * Created by liuwenbin on 2018/1/5.
 * 虽然青春不在，但不能自我放逐.
 */
@Singleton
@Component(modules = [(AndroidSupportInjectionModule::class), (AppModule::class), (ActivityBuilder::class)])
interface AppComponent : AndroidInjector<DaggerApplication> {

    fun inject(app: EOASApplication)

    override fun inject(instance: DaggerApplication?)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
