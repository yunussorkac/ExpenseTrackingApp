package com.app.expensetracking.di

import com.app.expensetracking.domain.repo.AuthRepositoryImpl
import com.app.expensetracking.domain.repo.ExpenseRepositoryImpl
import com.app.expensetracking.domain.repo.IAuthRepository
import com.app.expensetracking.domain.repo.IExpenseRepository
import com.app.expensetracking.domain.usecase.expense.AddExpenseUseCase
import com.app.expensetracking.domain.usecase.expense.GetDailyExpenseTotalUseCase
import com.app.expensetracking.domain.usecase.expense.GetMonthlyCategoryTotalsUseCase
import com.app.expensetracking.domain.usecase.expense.GetMonthlyExpenseTotalUseCase
import com.app.expensetracking.domain.usecase.expense.GetRecentExpensesUseCase
import com.app.expensetracking.domain.usecase.expense.GetWeeklyExpenseTotalUseCase
import com.app.expensetracking.domain.usecase.auth.LoginUseCase
import com.app.expensetracking.domain.usecase.auth.RegisterUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun provideRegisterUseCase(authRepository: IAuthRepository): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: IAuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideAddExpenseUseCase(expenseRepository: IExpenseRepository): AddExpenseUseCase {
        return AddExpenseUseCase(expenseRepository)
    }

    @Provides
    @Singleton
    fun provideExpenseRepository(firestore: FirebaseFirestore, auth: FirebaseAuth): IExpenseRepository {
        return ExpenseRepositoryImpl(firestore, auth)
    }

    @Provides
    @Singleton
    fun provideGetDailyExpenseTotalUseCase(expenseRepository: IExpenseRepository): GetDailyExpenseTotalUseCase {
        return GetDailyExpenseTotalUseCase(expenseRepository)
    }
    @Provides
    @Singleton
    fun provideGetWeeklyExpenseTotalUseCase(expenseRepository: IExpenseRepository): GetWeeklyExpenseTotalUseCase {
        return GetWeeklyExpenseTotalUseCase(expenseRepository)
    }
    @Provides
    @Singleton
    fun provideGetMonthlyExpenseTotalUseCase(expenseRepository: IExpenseRepository): GetMonthlyExpenseTotalUseCase {
        return GetMonthlyExpenseTotalUseCase(expenseRepository)
    }
    @Provides
    @Singleton
    fun provideGetMonthlyCategoryTotalsUseCase(expenseRepository: IExpenseRepository): GetMonthlyCategoryTotalsUseCase {
        return GetMonthlyCategoryTotalsUseCase(expenseRepository)
    }
    @Provides
    @Singleton
    fun provideGetRecentExpensesUseCase(expenseRepository: IExpenseRepository): GetRecentExpensesUseCase {
        return GetRecentExpensesUseCase(expenseRepository)
    }



}