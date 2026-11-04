package com.example.coffeeselector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.coffeeselector.ui.HomeRoute


/**
 * This is going to act as my router to each of my pages.
 * I will start to make this following a Kotlin guide:
 * https://kotlinlang.org/docs/multiplatform/compose-navigation-routing.html#sample-project
 * It contains some sample code that I intend to adapt to my project
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//            val navController = rememberNavController()
//            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                NavHost(
//                    navController = navController,
//                    startDestination = HomeRoute
//                    modifier = Modifier.padding(innerPadding)
//                ) {
//                    composable<HomeRoute> {
//
//                    }
//                }
//            }

        }
    }

}

