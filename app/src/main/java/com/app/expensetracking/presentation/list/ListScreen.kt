package com.app.expensetracking.presentation.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.app.expensetracking.ui.components.expense.ExpenseCard

@Composable
fun ListScreen(navHostController: NavHostController) {


    val viewModel = hiltViewModel<ListScreenViewModel>()

    val expenses by viewModel.expenses.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getExpenses()
    }


    LazyColumn(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {
        items(expenses) { expense ->
            ExpenseCard(
                expense = expense
            ){
                viewModel.deleteExpense(expense) { result ->

                }
            }
        }
    }

}