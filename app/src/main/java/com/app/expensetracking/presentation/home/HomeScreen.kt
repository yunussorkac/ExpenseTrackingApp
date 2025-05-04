package com.app.expensetracking.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.app.expensetracking.ui.components.expense.ExpenseCard
import com.app.expensetracking.ui.components.home.CategoryItem
import com.app.expensetracking.ui.components.home.SummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navHostController: NavHostController) {

    val viewModel = hiltViewModel<HomeScreenViewModel>()

    val daily by viewModel.dailyTotal.collectAsStateWithLifecycle()
    val weekly by viewModel.weeklyTotal.collectAsStateWithLifecycle()
    val monthly by viewModel.monthlyTotal.collectAsStateWithLifecycle()
    val categoryMap by viewModel.categoryTotals.collectAsStateWithLifecycle()
    val recentExpenses by viewModel.recentExpenses.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard(
                    title = "Today",
                    amount = daily,
                    icon = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surfaceDim
                )

                SummaryCard(
                    title = "Weekly",
                    amount = weekly,
                    icon = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.secondaryContainer
                )

                SummaryCard(
                    title = "Monthly",
                    amount = monthly,
                    icon = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Monthly by Categories",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (categoryMap.isEmpty()) {
                        Text(
                            "No Data",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        categoryMap.forEach { (category, amount) ->
                            CategoryItem(
                                categoryName = category.displayName,
                                amount = amount
                            )
                            if (category != categoryMap.keys.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Recent Expenses",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (recentExpenses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        "No Data.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(recentExpenses) { expense ->
                ExpenseCard(expense)
            }
        }

    }


}
