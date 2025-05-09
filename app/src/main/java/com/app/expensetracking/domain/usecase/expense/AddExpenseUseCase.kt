package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.data.remote.repository.IExpenseRepository
import com.app.expensetracking.domain.model.Expense
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val repository: IExpenseRepository
) {
    suspend operator fun invoke(expense: Expense): Result<Unit> {
        return repository.addExpense(expense)
    }
}