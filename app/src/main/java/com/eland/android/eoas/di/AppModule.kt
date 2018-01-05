package com.eland.android.eoas.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module


/**
 * Created by liuwenbin on 2018/1/5.
 * 虽然青春不在，但不能自我放逐.
 */
@Module
abstract class AppModule {
    @Binds
    internal abstract fun provideContext(application: Application): Context

}