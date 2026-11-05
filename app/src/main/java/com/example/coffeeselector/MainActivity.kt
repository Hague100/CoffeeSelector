package com.example.coffeeselector


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coffeeselector.ui.HomeRoute
import com.example.coffeeselector.ui.LoginRoute
import com.example.coffeeselector.ui.home.HomeScreen
import com.example.coffeeselector.ui.login.LoginScreen
import com.example.coffeeselector.ui.login.LoginViewModel


/**
 * This is going to act as my router to each of my pages.
 * I will start to make this following a Kotlin guide:
 * https://kotlinlang.org/docs/multiplatform/compose-navigation-routing.html#sample-project
 * It contains some sample code that I intend to adapt to my project
 */
/**
 * Using this guide my toast messages where made
 * https://developer.android.com/guide/topics/ui/notifiers/toasts
 * As well as:
 * https://www.scaler.com/topics/toast-in-kotlin/
 * but mainly this for context issues I was having
 * https://pinakinvox.com/blog/android-ui-tutorial-how-to-implement-and-customize-toast-on-android
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = HomeRoute,
                    modifier = Modifier
                ) {
                    composable<HomeRoute> {
                        HomeScreen(
                            {navController.navigate(LoginRoute)},
                            {},
                            {},
                            modifier = Modifier.padding(innerPadding))
                    }
                    composable<LoginRoute> {
                        val loginViewModel: LoginViewModel = viewModel()
                        LoginScreen(
                            onLogin = {email, password ->
                                loginViewModel.login(email, password) { isSuccess, message ->

                                    Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
                                    if (isSuccess) {
                                        navController.navigate(HomeRoute) {
                                            popUpTo(HomeRoute) {inclusive = true}
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

        }
    }

}

