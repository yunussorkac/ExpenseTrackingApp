package com.app.expensetracking.presentation.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.app.expensetracking.presentation.settings.SettingsScreenViewModel
import com.app.expensetracking.ui.Screens
import com.app.expensetracking.ui.components.expense.ExpenseCard

@Composable
fun ListScreen(navHostController: NavHostController) {


    val viewModel = hiltViewModel<ListScreenViewModel>()
    val settingsViewModel = hiltViewModel<SettingsScreenViewModel>()
    val currency by settingsViewModel.selectedCurrency.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.getExpenses()
        settingsViewModel.loadCurrency()
    }


    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                viewModel.onSearchQueryChanged(it)

            },
            shape = CircleShape,
            label = { Text("Search by title") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
            items(expenses) { expense ->
                ExpenseCard(
                    expense = expense,
                    currency = currency,
                    onDelete = {
                        viewModel.deleteExpense(expense) {

                        }
                    },
                    onEdit = {
                        navHostController.navigate(Screens.Edit(expense.expenseId))
                    }
                )
            }
        }
    }
}

