package com.app.expensetracking.di

import com.app.expensetracking.data.remote.repository.IAuthRepository
import com.app.expensetracking.data.remote.repository.IExpenseRepository
import com.app.expensetracking.domain.repository.AuthRepositoryImpl
import com.app.expensetracking.domain.repository.ExpenseRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): IAuthRepository = AuthRepositoryImpl(auth, firestore)

    @Provides
    @Singleton
    fun provideExpenseRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): IExpenseRepository = ExpenseRepositoryImpl(firestore, auth)
}