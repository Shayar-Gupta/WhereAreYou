package com.example.whereareyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whereareyou.ui.screens.OnboardingScreen
import com.example.whereareyou.ui.theme.WhereAreYouTheme
import com.example.whereareyou.viewmodel.GoogleSignInViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            var showOnboarding by remember { mutableStateOf(true) }

            WhereAreYouTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    innerPadding

                    App() // Pass navController to App
                }
            }
        }
    }
}

@Composable
fun App() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val googleSignInViewModel = GoogleSignInViewModel()

    NavHost(navController = navController, startDestination = "home") {

        composable(route = "home") {
            OnboardingScreen(
                viewModel = googleSignInViewModel, // Pass ViewModel
                navController = navController, // Pass NavController
                context = context // Pass Context
            )
        }

        composable(route = "profile") {
            ProfileScreen(googleSignInViewModel)
        }

    }

}


//@Composable
//fun App(navController: NavController, viewModel: GoogleSignInViewModel) {
//    NavHost(navController = navController, startDestination = "home") {
//        composable(route = "profile") {
//            ProfileScreen(viewModel)  // Pass the ViewModel to ProfileScreen
//        }
//        // Define other composables and destinations here
//    }
//}
