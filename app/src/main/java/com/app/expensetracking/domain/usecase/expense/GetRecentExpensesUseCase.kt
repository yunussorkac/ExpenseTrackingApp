package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.data.remote.repository.IExpenseRepository
import com.app.expensetracking.domain.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRecentExpensesUseCase @Inject constructor(
    private val repo: IExpenseRepository,
) {
     operator fun invoke(): Flow<List<Expense>> = flow{
         repo.getLast5Expenses().collect {
             emit(it)
         }
    }
}