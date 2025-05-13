package com.app.expensetracking.data.remote.repository

import com.app.expensetracking.domain.model.Expense
import com.app.expensetracking.domain.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow

interface IExpenseRepository {

    suspend fun addExpense(expense: Expense): Result<Unit>

    fun getExpenses(): Flow<List<Expense>>

    fun getLast5Expenses(): Flow<List<Expense>>

    suspend fun deleteExpense(expense: Expense): Result<Unit>

    suspend fun getExpenseById(expenseId: String): Result<Expense>

    suspend fun editExpense(expense: Expense): Result<Unit>



}