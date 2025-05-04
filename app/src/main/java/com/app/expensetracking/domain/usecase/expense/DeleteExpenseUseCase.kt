package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.domain.repo.IExpenseRepository
import com.app.expensetracking.model.Expense
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val repository: IExpenseRepository
) {
    suspend operator fun invoke(expense: Expense): Result<Unit> {
        return repository.deleteExpense(expense)
    }
}