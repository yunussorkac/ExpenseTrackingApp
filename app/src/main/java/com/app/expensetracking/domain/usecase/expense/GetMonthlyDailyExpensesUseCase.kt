package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.data.remote.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyDailyExpensesUseCase @Inject constructor(
    private val repository: IExpenseRepository
) {
    operator fun invoke(month: Int? = null, year: Int? = null): Flow<Map<String, Double>> {
        return repository.getMonthlyDailyExpenses(month, year)
    }
}