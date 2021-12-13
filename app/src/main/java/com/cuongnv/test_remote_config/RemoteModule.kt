package com.cuongnv.test_remote_config

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

class RemoteModule {

    @Qualifier
    annotation class GsonAnnotation

    @Module
    @InstallIn(SingletonComponent::class)
    object RemoteConfigModule{
        @GsonAnnotation
        @Provides
        fun initGson() = Gson()
    }
}