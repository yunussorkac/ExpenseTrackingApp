package com.app.expensetracking.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.expensetracking.presentation.login.LoginScreen
import com.app.expensetracking.presentation.register.RegisterScreen
import com.app.expensetracking.presentation.settings.SettingsScreenViewModel
import com.app.expensetracking.ui.theme.ExpenseTrackingAppTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }
        setContent {
            val viewModel = hiltViewModel<SettingsScreenViewModel>()
            val isDarkMode = viewModel.isDarkMode.collectAsStateWithLifecycle()
            ExpenseTrackingAppTheme(
                darkTheme = isDarkMode.value
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MainContent(modifier = Modifier.padding(it))
                }
            }
        }
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = Color.White,
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                MainNavigation(navController = navController)
            }
        }
    )
}

@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screens.Login) {

        composable<Screens.Login> {
            LoginScreen(navController)
        }

        composable<Screens.Register> {
            RegisterScreen(navController)
        }



    }
}

