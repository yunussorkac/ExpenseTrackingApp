package com.app.expensetracking.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.expensetracking.presentation.chart.ChartScreen
import com.app.expensetracking.presentation.add.AddExpenseScreen
import com.app.expensetracking.presentation.home.HomeScreen
import com.app.expensetracking.presentation.list.ListScreen
import com.app.expensetracking.presentation.settings.SettingsScreen
import com.app.expensetracking.ui.components.bottom.BottomNavigation
import com.app.expensetracking.ui.theme.ExpenseTrackingAppTheme
import com.app.expensetracking.ui.components.top.TopAppBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackingAppTheme {
                HomeContent()
            }
        }
    }
}

@Composable
fun HomeContent() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route


    val items = remember {
        listOf(
            NavigationItem.Home,
            NavigationItem.Chart,
            NavigationItem.List,
            NavigationItem.Settings
        )
    }

    val topBarTitle = when (currentRoute) {
        NavigationItem.Home.route -> NavigationItem.Home.title
        NavigationItem.Chart.route -> NavigationItem.Chart.title
        NavigationItem.List.route -> NavigationItem.List.title
        NavigationItem.Settings.route -> NavigationItem.Settings.title
        Screens.Add::class.qualifiedName -> "Add Expense"
        else -> ""
    }

    val isBottomNavRoute = remember(currentRoute) {
        currentRoute in items.map { it.route }
    }

    Scaffold(
        bottomBar = {
            if (isBottomNavRoute) {
                BottomNavigation(
                    items = items,
                    selectedItemIndex = items.indexOfFirst { it.route == currentRoute },
                    onItemSelected = { index ->
                        val selectedRoute = items[index].route
                        if (selectedRoute != currentRoute) {
                            navController.navigate(selectedRoute) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        }
                    },
                )
            }
        },
        topBar = {
            TopAppBar(
                title = topBarTitle,
                showNavigationIcon = currentRoute == Screens.Add::class.qualifiedName,
                onNavigationClick = {
                    navController.popBackStack()
                }
            )
        },
        floatingActionButton = {
            if (currentRoute == NavigationItem.Home.route){

                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screens.Add)
                    },
                    modifier = Modifier
                        .shadow(12.dp, CircleShape)
                    ,
                    containerColor = Color.Black,

                    ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Location",
                        tint = Color.White
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            HomeNavigation(navController = navController)
        }
    }
}

@Composable
fun HomeNavigation(navController: NavHostController) {
    val startDestination = NavigationItem.Home.route

    NavHost(navController, startDestination = startDestination) {

        composable(NavigationItem.Home.route) {
            HomeScreen(navController)
        }
        composable(NavigationItem.Chart.route) {
            ChartScreen()
        }
        composable(NavigationItem.List.route) {
            ListScreen(navController)
        }
        composable(NavigationItem.Settings.route) {
            SettingsScreen(navController)
        }

        composable<Screens.Add> {
            AddExpenseScreen(navController)
        }



    }
}

