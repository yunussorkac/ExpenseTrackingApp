package com.app.expensetracking.di

import android.content.Context
import com.app.expensetracking.data.local.BooleanDataStore
import com.app.expensetracking.data.local.StringDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideBooleanDataStore(@ApplicationContext context: Context): BooleanDataStore =
        BooleanDataStore(context)

    @Provides
    @Singleton
    fun provideStringDataStore(@ApplicationContext context: Context): StringDataStore =
        StringDataStore(context)
}