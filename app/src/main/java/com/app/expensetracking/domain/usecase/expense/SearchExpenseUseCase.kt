package com.app.expensetracking.domain.usecase.expense

import com.app.expensetracking.data.remote.repository.IExpenseRepository
import com.app.expensetracking.domain.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchExpenseUseCase @Inject constructor(
    private val repository: IExpenseRepository
) {
    operator fun invoke(query: String): Flow<List<Expense>> = flow {
        repository.searchExpenses(query).collect {
            emit(it)
        }
    }
}