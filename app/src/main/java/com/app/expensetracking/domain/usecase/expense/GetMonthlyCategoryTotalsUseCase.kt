package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.domain.repo.IExpenseRepository
import com.app.expensetracking.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMonthlyCategoryTotalsUseCase @Inject constructor(
    private val repo: IExpenseRepository,
) {
    operator fun invoke(): Flow<Map<ExpenseCategory, Double>> = flow {
        repo.getMonthlyCategoryTotals().collect {
            emit(it)
        }

    }
}