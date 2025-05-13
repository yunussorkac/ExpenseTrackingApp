package com.app.expensetracking.di

import com.app.expensetracking.data.remote.repository.IAuthRepository
import com.app.expensetracking.data.remote.repository.IExpenseRepository
import com.app.expensetracking.domain.usecase.auth.GetUserUseCase
import com.app.expensetracking.domain.usecase.auth.LoginUseCase
import com.app.expensetracking.domain.usecase.auth.RegisterUseCase
import com.app.expensetracking.domain.usecase.expense.AddExpenseUseCase
import com.app.expensetracking.domain.usecase.expense.DeleteExpenseUseCase
import com.app.expensetracking.domain.usecase.expense.EditExpenseUseCase
import com.app.expensetracking.domain.usecase.expense.GetExpenseByIdUseCase
import com.app.expensetracking.domain.usecase.expense.GetRecentExpensesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides fun provideRegisterUseCase(repo: IAuthRepository) = RegisterUseCase(repo)
    @Provides fun provideLoginUseCase(repo: IAuthRepository) = LoginUseCase(repo)
    @Provides fun provideGetUserUseCase(repo: IAuthRepository) = GetUserUseCase(repo)
    @Provides fun provideAddExpenseUseCase(repo: IExpenseRepository) = AddExpenseUseCase(repo)
    @Provides fun provideDeleteExpenseUseCase(repo: IExpenseRepository) = DeleteExpenseUseCase(repo)
    @Provides fun provideGetRecentExpensesUseCase(repo: IExpenseRepository) = GetRecentExpensesUseCase(repo)
    @Provides fun provideEditExpenseUseCase(repo: IExpenseRepository) = EditExpenseUseCase(repo)
    @Provides fun provideGetExpenseByIdUseCase(repo: IExpenseRepository) = GetExpenseByIdUseCase(repo)
}