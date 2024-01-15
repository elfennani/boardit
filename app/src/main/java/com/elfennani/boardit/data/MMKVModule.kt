package com.elfennani.boardit.data

import com.tencent.mmkv.MMKV
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MMKVModule {

    @Singleton
    @Provides
    fun provideMMKV(): MMKV = MMKV.defaultMMKV()

}