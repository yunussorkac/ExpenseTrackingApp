package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.domain.repo.IExpenseRepository
import javax.inject.Inject

class GetWeeklyExpenseTotalUseCase @Inject constructor(
    private val repo: IExpenseRepository,
) {
    suspend operator fun invoke(): Double {
        return repo.getWeeklyExpenseTotal()
    }
}