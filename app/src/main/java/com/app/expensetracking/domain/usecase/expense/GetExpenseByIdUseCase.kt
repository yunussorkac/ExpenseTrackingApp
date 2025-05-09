package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.data.remote.repository.IExpenseRepository
import com.app.expensetracking.domain.model.Expense
import javax.inject.Inject

class GetExpenseByIdUseCase @Inject constructor(
    private val repository: IExpenseRepository
) {
    suspend operator fun invoke(expenseId: String): Result<Expense> {
        return repository.getExpenseById(expenseId)
    }
}