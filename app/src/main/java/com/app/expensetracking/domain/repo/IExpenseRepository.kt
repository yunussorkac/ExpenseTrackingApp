package com.app.expensetracking.domain.repo

import com.app.expensetracking.model.Expense
import com.app.expensetracking.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow

interface IExpenseRepository {

    suspend fun addExpense(expense: Expense): Result<Unit>

    fun getExpenses(): Flow<List<Expense>>

    suspend fun getDailyExpenseTotal(): Double
    suspend fun getWeeklyExpenseTotal(): Double
    suspend fun getMonthlyExpenseTotal(): Double
    suspend fun getMonthlyCategoryTotals(): Map<ExpenseCategory, Double>
    fun getLast5Expenses(): Flow<List<Expense>>

}