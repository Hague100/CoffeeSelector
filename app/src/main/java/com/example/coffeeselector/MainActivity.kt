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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.coffeeselector.data.repository.ItemRepository
import com.example.coffeeselector.data.repository.OrderRepository
import com.example.coffeeselector.ui.LocationListRoute
import com.example.coffeeselector.ui.MenuRoute
import com.example.coffeeselector.ui.items.MenuScreen
import com.example.coffeeselector.ui.locations.LocationList
import com.example.coffeeselector.ui.locations.LocationViewModel
import com.example.coffeeselector.ui.navigation.NavTopBar
import com.example.coffeeselector.ui.navigation.NavigationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


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
/**
 * guide for state management:
 * https://developers.arcgis.com/kotlin/programming-patterns/manage-geoview-state/#using-the-viewmodel-in-a-composable
 *
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val db = remember { FirebaseFirestore.getInstance() }
            val auth = remember { FirebaseAuth.getInstance() }
            val itemRepository = remember { ItemRepository(db) }
            val orderRepository = remember { OrderRepository(db, auth) }

            val navigationViewModel: NavigationViewModel = viewModel(
                factory = NavigationViewModel.provideFactory(db, auth)
            )
            val unreadCount by navigationViewModel.unreadNotificationCount.collectAsStateWithLifecycle()
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    NavTopBar(
                        unreadCount = unreadCount,
                        onUserClick = { // navController.navigate(UserRoute) { launchSingleTop = true }
                        },
                        onLocationsClick = {
                            navController.navigate(LocationListRoute) {
                                popUpTo(LocationListRoute) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onNotificationsClick = { // navController.navigate(NotificationsRoute) { launchSingleTop = true }
                        },
                        onOrdersClick = { // navController.navigate(OrdersRoute) { launchSingleTop = true }
                        }
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = LocationListRoute,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable<LocationListRoute> {
                        val viewModel: LocationViewModel = viewModel()
                        val locations by viewModel.locationsListData.collectAsStateWithLifecycle()
                        val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

                        LaunchedEffect(statusMessage) {
                            if (!statusMessage.isNullOrBlank()) {
                                Toast.makeText(application, statusMessage, Toast.LENGTH_SHORT).show()
                            }
                        }

                        LocationList(
                            locations = locations,
                            distanceCalculation = { cafe -> "" },
                            onNavigateToMenu = { selectedLocation ->
                                viewModel.selectLocation(selectedLocation.id)
                                val stockEntries = selectedLocation.menuStockQuantity.toList()
                                navController.navigate(
                                    MenuRoute(
                                        locationId = selectedLocation.id,
                                        itemIds = stockEntries.map { it.first },
                                        itemAvailabilities = stockEntries.map { it.second }
                                    )
                                )
                            }
                        )
                    }

                    composable<MenuRoute> { backStackEntry ->
                        val route: MenuRoute = backStackEntry.toRoute()
                        val itemsAndAvailability = route.itemIds.zip(route.itemAvailabilities)

                        MenuScreen(
                            locationId = route.locationId,
                            menuItemsIdAndAvailability = itemsAndAvailability,
                            itemRepository = itemRepository,
                            orderRepository = orderRepository,
                            onNavigateToItem = { itemId ->
                                // navController.navigate(ItemDetailRoute(itemId))
                            },
                            onOrderSubmitted = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}


