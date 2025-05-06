package com.app.expensetracking.domain.repo

import com.app.expensetracking.model.Expense
import com.app.expensetracking.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow

interface IExpenseRepository {

    suspend fun addExpense(expense: Expense): Result<Unit>

    fun getExpenses(): Flow<List<Expense>>


    fun getDailyExpenseTotal(): Flow<Double>

    fun getWeeklyExpenseTotal(): Flow<Double>

    fun getMonthlyExpenseTotal(): Flow<Double>

    fun getMonthlyCategoryTotals(): Flow<Map<ExpenseCategory, Double>>

    fun getMonthlyDailyExpenses(): Flow<Map<String, Double>>

    fun getLast5Expenses(): Flow<List<Expense>>

    suspend fun deleteExpense(expense: Expense): Result<Unit>


}