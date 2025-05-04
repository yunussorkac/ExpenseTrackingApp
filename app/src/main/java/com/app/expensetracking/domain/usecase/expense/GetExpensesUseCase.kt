package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.domain.repo.IExpenseRepository
import com.app.expensetracking.model.Expense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExpensesUseCase @Inject constructor(
    private val repository: IExpenseRepository
) {
    operator fun invoke(): Flow<List<Expense>> {
        return repository.getExpenses()
    }
}