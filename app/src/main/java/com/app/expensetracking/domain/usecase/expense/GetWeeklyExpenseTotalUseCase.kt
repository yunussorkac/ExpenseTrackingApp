package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.domain.repo.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetWeeklyExpenseTotalUseCase @Inject constructor(
    private val repo: IExpenseRepository,
) {
    operator fun invoke(): Flow<Double> = flow {
        repo.getWeeklyExpenseTotal().collect {
            emit(it)
        }

    }
}