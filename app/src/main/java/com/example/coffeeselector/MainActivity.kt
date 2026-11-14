package com.example.coffeeselector


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coffeeselector.ui.LocationListRoute
import com.example.coffeeselector.ui.LoginRoute
import com.example.coffeeselector.ui.NoUserHomeRoute
import com.example.coffeeselector.ui.ProfileRoute
import com.example.coffeeselector.ui.RegisterRoute
import com.example.coffeeselector.ui.UserHomeRoute
import com.example.coffeeselector.ui.auth.LoginScreen
import com.example.coffeeselector.ui.auth.AuthViewModel
import com.example.coffeeselector.ui.auth.RegisterScreen
import com.example.coffeeselector.ui.home.NoUserHomeScreen
import com.example.coffeeselector.ui.home.UserHomeScreen
import com.example.coffeeselector.ui.profile.ProfileViewModel
import com.example.coffeeselector.ui.profile.UserProfile
import com.google.firebase.auth.FirebaseAuth


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

/**
 * This will be my guide for checking what users are logged in
 * https://firebase.google.com/docs/auth/web/manage-users#web_2
 */
class MainActivity : ComponentActivity() {
    private var user =  FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = if(user != null) UserHomeRoute else NoUserHomeRoute,
                    modifier = Modifier
                ) {
                    composable<NoUserHomeRoute> {
                        NoUserHomeScreen(
                            { navController.navigate(LoginRoute) },
                            { navController.navigate(RegisterRoute) },
                            {TODO()},
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    composable<UserHomeRoute> {
                        val logOutViewModel: AuthViewModel = viewModel()
                        UserHomeScreen(
                            {navController.navigate(ProfileRoute)},
                            {TODO()},
                            {logOutViewModel.logout()},
                            {
                                user = FirebaseAuth.getInstance().currentUser
                                navController.navigate(NoUserHomeRoute) {
                                    popUpTo(0) {inclusive = true}
                                }
                            }
                        )
                    }
                    composable<LoginRoute> {
                        val loginViewModel: AuthViewModel = viewModel()
                        LoginScreen(
                            onLogin = {email, password ->
                                loginViewModel.login(email, password) { isSuccess, message ->
                                    Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
                                    if (isSuccess) {
                                        navController.navigate(UserHomeRoute) {
                                            popUpTo(0) {inclusive = true}
                                        }
                                    }
                                }
                            } , {navController.navigate(RegisterRoute)}
                        )
                    }
                    composable<RegisterRoute> {
                        val registerViewModel: AuthViewModel = viewModel()
                        RegisterScreen(
                            onRegisterUserData = {email, password, firstName, lastName, username, phoneNo ->
                                registerViewModel.registerUserData(email, password, firstName, lastName, username, phoneNo ) { isSuccess, message ->
                                    Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
                                    if(isSuccess) {
                                        navController.navigate(UserHomeRoute) {
                                            popUpTo(0) {inclusive = true}
                                        }
                                    }

                                }
                            },

                        )
                    }

//                    composable<ProfileRoute> {
//                        val profileViewModel: ProfileViewModel = viewModel()
//                        profileViewModel.getUserDataClass() { isSuccess, message,  ->
//                            if (isSuccess){
//                                Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
//                            } else Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
//                        }
//                        UserProfile(
//                            profileViewModel.userData,
//                            {},
//                            {}
//                        )
//                    }
                    /**
                     * guide for state management:
                     * https://developers.arcgis.com/kotlin/programming-patterns/manage-geoview-state/#using-the-viewmodel-in-a-composable
                     *
                     */
                    composable<ProfileRoute> {
                        val profileViewModel: ProfileViewModel = viewModel()
                        val statusMessage by profileViewModel.statusMessage.collectAsStateWithLifecycle()
                        val userData by profileViewModel.userData.collectAsStateWithLifecycle()
                        /**
                         * talks about why to use launch effects
                         * https://composables.com/blog/side-effects
                         */
                        LaunchedEffect(statusMessage) {
                            val currentMessage = statusMessage
                            currentMessage?.isNotBlank()?.let { it1 ->
                                if(it1)
                                    Toast.makeText(application, statusMessage, Toast.LENGTH_SHORT).show()
                            }
                        }

                        userData?.let {
                            UserProfile(
                                it,
                                {},
                                {}
                            )
                        }

                    }

                    composable<LocationListRoute> {

                    }
                }
            }

        }
    }

}

