package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.data.remote.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDailyExpenseTotalUseCase @Inject constructor(
    private val repo: IExpenseRepository,
) {
    operator fun invoke(): Flow<Double> = flow {
        repo.getDailyExpenseTotal().collect {
            emit(it)
        }

    }
}