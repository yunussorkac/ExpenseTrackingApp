package com.app.expensetracking.di

import android.content.Context
import com.app.expensetracking.data.local.BooleanDataStore
import com.app.expensetracking.data.local.StringDataStore
import com.app.expensetracking.data.remote.api.CurrencyApi
import com.app.expensetracking.domain.repository.AuthRepositoryImpl
import com.app.expensetracking.domain.repository.ExpenseRepositoryImpl
import com.app.expensetracking.data.remote.repository.IAuthRepository
import com.app.expensetracking.data.remote.repository.IExpenseRepository
import com.app.expensetracking.domain.usecase.auth.GetUserUseCase
import com.app.expensetracking.domain.usecase.auth.LoginUseCase
import com.app.expensetracking.domain.usecase.auth.RegisterUseCase
import com.app.expensetracking.domain.usecase.expense.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()







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






    @Provides
    @Singleton
    fun provideRegisterUseCase(authRepository: IAuthRepository): RegisterUseCase =
        RegisterUseCase(authRepository)

    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: IAuthRepository): LoginUseCase =
        LoginUseCase(authRepository)

    @Provides
    @Singleton
    fun provideGetUserUseCase(authRepository: IAuthRepository): GetUserUseCase =
        GetUserUseCase(authRepository)






    @Provides
    @Singleton
    fun provideAddExpenseUseCase(expenseRepository: IExpenseRepository): AddExpenseUseCase =
        AddExpenseUseCase(expenseRepository)

    @Provides
    @Singleton
    fun provideDeleteExpenseUseCase(expenseRepository: IExpenseRepository): DeleteExpenseUseCase =
        DeleteExpenseUseCase(expenseRepository)

    @Provides
    @Singleton
    fun provideGetDailyExpenseTotalUseCase(expenseRepository: IExpenseRepository): GetDailyExpenseTotalUseCase =
        GetDailyExpenseTotalUseCase(expenseRepository)

    @Provides
    @Singleton
    fun provideGetWeeklyExpenseTotalUseCase(expenseRepository: IExpenseRepository): GetWeeklyExpenseTotalUseCase =
        GetWeeklyExpenseTotalUseCase(expenseRepository)

    @Provides
    @Singleton
    fun provideGetMonthlyExpenseTotalUseCase(expenseRepository: IExpenseRepository): GetMonthlyExpenseTotalUseCase =
        GetMonthlyExpenseTotalUseCase(expenseRepository)

    @Provides
    @Singleton
    fun provideGetMonthlyCategoryTotalsUseCase(expenseRepository: IExpenseRepository): GetMonthlyCategoryTotalsUseCase =
        GetMonthlyCategoryTotalsUseCase(expenseRepository)

    @Provides
    @Singleton
    fun provideGetRecentExpensesUseCase(expenseRepository: IExpenseRepository): GetRecentExpensesUseCase =
        GetRecentExpensesUseCase(expenseRepository)

    @Provides
    @Singleton
    fun provideEditExpenseUseCase(expenseRepository: IExpenseRepository): EditExpenseUseCase =
        EditExpenseUseCase(expenseRepository)

    @Provides
    @Singleton
    fun provideSearchExpensesUseCase(expenseRepository: IExpenseRepository): SearchExpenseUseCase =
        SearchExpenseUseCase(expenseRepository)

    @Provides
    @Singleton
    fun provideGetExpenseByIdUseCase(expenseRepository: IExpenseRepository): GetExpenseByIdUseCase =
        GetExpenseByIdUseCase(expenseRepository)




    @Provides
    @Singleton
    fun provideAppDataStore(@ApplicationContext context: Context): BooleanDataStore =
        BooleanDataStore(context)

    @Provides
    @Singleton
    fun provideStringDataStore(@ApplicationContext context: Context): StringDataStore =
        StringDataStore(context)



    @Provides
    fun provideRetrofit(): CurrencyApi {
        return Retrofit.Builder()
            .baseUrl("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }

}
