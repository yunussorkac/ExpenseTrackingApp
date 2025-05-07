package com.app.expensetracking.presentation.chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.ehsannarmani.compose_charts.PieChart
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
fun ChartScreen() {

    val viewModel = hiltViewModel<ChartScreenViewModel>()
    val categoryTotals by viewModel.categoryTotals.collectAsState()
    val dailyExpenses by viewModel.dailyExpenses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val scrollState = rememberScrollState()
    val currentMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Calendar.getInstance().time)

    val colors = remember {
        listOf(
            Color(0xFF4CAF50),
            Color(0xFF2196F3),
            Color(0xFFFFC107),
            Color(0xFFE91E63),
            Color(0xFF9C27B0),
            Color(0xFFFF5722),
            Color(0xFF607D8B),
            Color(0xFF795548)
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadChartData()
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {




                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = "Monthly Expenses by Category",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(onClick = { viewModel.loadChartData() }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh"
                                )
                            }
                        }




                        Text(
                            text = currentMonth,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )



                        Spacer(modifier = Modifier.height(16.dp))

                        if (categoryTotals.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No expenses recorded this month",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            val pieData by remember(categoryTotals) {
                                mutableStateOf(
                                    categoryTotals.entries.mapIndexed { index, entry ->
                                        Pie(
                                            label = entry.key.displayName,
                                            data = entry.value,
                                            color = colors[index % colors.size],
                                            selectedColor = colors[index % colors.size].copy(alpha = 0.7f),
                                            selected = false
                                        )
                                    }
                                )
                            }

                            var chartPieData by remember { mutableStateOf(pieData) }

                            PieChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                data = chartPieData,
                                onPieClick = { clickedPie ->
                                    val pieIndex = chartPieData.indexOf(clickedPie)
                                    chartPieData = chartPieData.mapIndexed { mapIndex, pie ->
                                        pie.copy(selected = pieIndex == mapIndex)
                                    }
                                },
                                selectedScale = 1.1f,
                                scaleAnimEnterSpec = spring<Float>(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                colorAnimEnterSpec = tween(300),
                                colorAnimExitSpec = tween(300),
                                scaleAnimExitSpec = tween(300),
                                spaceDegreeAnimExitSpec = tween(300),
                                style = Pie.Style.Fill
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                categoryTotals.entries.forEachIndexed { index, entry ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(colors[index % colors.size])
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = entry.key.displayName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Text(
                                            text = "₺${String.format("%.2f", entry.value)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Daily Expenses",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = currentMonth,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (dailyExpenses.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No daily expenses recorded this month",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            val columnData = remember(dailyExpenses) {
                                dailyExpenses.map { (date, value) ->
                                    Bars(
                                        label = date,
                                        values = listOf(
                                            Bars.Data(value = value, color = Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF4CAF50),
                                                    Color(0xFF2196F3)
                                                )
                                            ))
                                        )
                                    )
                                }.sortedBy { it.label }
                            }

                            ColumnChart(
                                data = columnData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                barProperties = BarProperties(
                                    spacing = 4.dp,
                                ),
                                onBarClick = {
                                    println("Bar clicked")
                                },

                                )
                        }
                    }

                }
                Spacer(modifier = Modifier.height(24.dp))

            }

        }
    }



}