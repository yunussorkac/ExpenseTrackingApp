package com.app.expensetracking.presentation.settings

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.app.expensetracking.ui.MainActivity
import com.app.expensetracking.ui.components.settings.SettingsItem
import com.app.expensetracking.ui.components.settings.SettingsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<SettingsScreenViewModel>()
    val user by viewModel.user.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        user?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it.email.first().toString().uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = it.email,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {

                        },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Edit Profile")
                    }
                }
            }
        }

        SettingsSection(title = "General Settings") {
            SettingsItem(
                title = "Theme",
                description = if (darkModeEnabled) "Dark Theme" else "Light Theme",
                icon = Icons.Outlined.DarkMode,
                onClick = { showThemeDialog = true }
            ) {
                Switch(
                    checked = darkModeEnabled,
                    onCheckedChange = {
                        darkModeEnabled = it
                    }
                )
            }


            SettingsItem(
                title = "Currency",
                description = "TRY",
                icon = Icons.Outlined.CurrencyExchange,
                onClick = {  }
            )
        }

        SettingsSection(title = "About App") {
            SettingsItem(
                title = "Privacy Policy",
                description = "Learn how to protect your data",
                icon = Icons.Outlined.PrivacyTip,
                onClick = {  }
            )


            SettingsItem(
                title = "About Us",
                description = "v1.0",
                icon = Icons.Outlined.Info,
                onClick = { }
            )

            SettingsItem(
                title = "Rate Us",
                description = "Rate us on the Play Store",
                icon = Icons.Outlined.Star,
                onClick = {  }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.logout()
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log Out",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
