package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.domain.repo.IExpenseRepository
import com.app.expensetracking.model.ExpenseCategory
import javax.inject.Inject

class GetMonthlyCategoryTotalsUseCase @Inject constructor(
    private val repo: IExpenseRepository,
) {
    suspend operator fun invoke(): Map<ExpenseCategory, Double> {
        return repo.getMonthlyCategoryTotals()
    }
}