package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.data.remote.repository.IExpenseRepository
import com.app.expensetracking.domain.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyCategoryTotalsUseCase @Inject constructor(
    private val repository: IExpenseRepository
) {
    operator fun invoke(month: Int? = null, year: Int? = null): Flow<Map<ExpenseCategory, Double>> {
        return repository.getMonthlyCategoryTotals(month, year)
    }
}