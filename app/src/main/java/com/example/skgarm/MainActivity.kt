package com.example.skgarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skgarm.ui.theme.BgPrimary
import com.example.skgarm.ui.theme.SkgArmTheme
import com.example.skgarm.Viewmodel.AppViewModel
import com.example.skgarm.data.Local.Entity.User
import com.example.skgarm.ui.Screens.LoginScreen
import com.example.skgarm.ui.Screens.ScheduleScreen

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkgArmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgPrimary
                ) {
                    SkgArmApp()
                }
            }
        }
    }
}

@Composable
fun SkgArmApp(viewModel: AppViewModel = hiltViewModel()) {
    val currentUser by viewModel.currentUser.collectAsState()

    when (val user: User? = currentUser) {
        null -> LoginScreen(viewModel = viewModel)
        else -> ScheduleScreen(user = user, viewModel = viewModel)
    }
}