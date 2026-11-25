package com.example.coffeeselector


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.coffeeselector.data.model.ItemDataClass
import com.example.coffeeselector.data.repository.ItemRepository
import com.example.coffeeselector.data.repository.NotificationRepository
import com.example.coffeeselector.data.repository.OrderRepository
import com.example.coffeeselector.ui.LocationListRoute
import com.example.coffeeselector.ui.LoginRoute
import com.example.coffeeselector.ui.MenuRoute
import com.example.coffeeselector.ui.NotificationsRoute
import com.example.coffeeselector.ui.OrderListRoute
import com.example.coffeeselector.ui.OrderOverviewRoute
import com.example.coffeeselector.ui.ProfileRoute
import com.example.coffeeselector.ui.RecentOrdersRoute
import com.example.coffeeselector.ui.RegisterRoute
import com.example.coffeeselector.ui.auth.AuthViewModel
import com.example.coffeeselector.ui.auth.LoginScreen
import com.example.coffeeselector.ui.auth.RegisterScreen
import com.example.coffeeselector.ui.items.MenuScreen
import com.example.coffeeselector.ui.locations.LocationList
import com.example.coffeeselector.ui.locations.LocationViewModel
import com.example.coffeeselector.ui.navigation.NavTopBar
import com.example.coffeeselector.ui.notification.NotificationList
import com.example.coffeeselector.ui.notification.NotificationViewModel
import com.example.coffeeselector.ui.orders.AllOrderList
import com.example.coffeeselector.ui.orders.OrderOverview
import com.example.coffeeselector.ui.orders.OrderViewModel
import com.example.coffeeselector.ui.orders.RecentOrderList
import com.example.coffeeselector.ui.profile.ProfileViewModel
import com.example.coffeeselector.ui.profile.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


/**
 * This is going to act as my router to each of my pages.
 * I will start to make this following a Kotlin guide:
 * https://kotlinlang.org/docs/multiplatform/compose-navigation-routing.html#sample-project
 * It contains some sample code that I intend to adapt to my project
 * Using this guide my toast messages where made
 * https://developer.android.com/guide/topics/ui/notifiers/toasts
 * As well as:
 * https://www.scaler.com/topics/toast-in-kotlin/
 * but mainly this for context issues I was having
 * https://pinakinvox.com/blog/android-ui-tutorial-how-to-implement-and-customize-toast-on-android
 * This will be my guide for checking what users are logged in
 * https://firebase.google.com/docs/auth/web/manage-users#web_2
 * guide for state management:
 * https://developers.arcgis.com/kotlin/programming-patterns/manage-geoview-state/#using-the-viewmodel-in-a-composable
 * talks about why to use launch effects
 * https://composables.com/blog/side-effects
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
            val notificationRepository = remember { NotificationRepository(db, auth) }
            var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
            val orderViewModel: OrderViewModel = viewModel(
                factory = OrderViewModel.provideFactory(orderRepository, itemRepository, notificationRepository)
            )

            DisposableEffect(auth) {
                val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                    isLoggedIn = firebaseAuth.currentUser != null
                }
                auth.addAuthStateListener(listener)
                onDispose { auth.removeAuthStateListener(listener) }
            }

            val notificationViewModel: NotificationViewModel = viewModel(
                factory = NotificationViewModel.provideFactory(db, auth, notificationRepository)
            )
            val unreadCount by notificationViewModel.unreadNotificationCount.collectAsStateWithLifecycle()

            LaunchedEffect(notificationViewModel) {
                notificationViewModel.newNotificationEvent.collect { message ->
                    Toast.makeText(this@MainActivity, "New notification: $message",
                        Toast.LENGTH_SHORT).show()
                }
            }

            Scaffold(modifier = Modifier.fillMaxSize(),
                topBar = {
                    NavTopBar(
                        unreadCount = unreadCount,
                        onUserClick = {
                            val destination = if(isLoggedIn) ProfileRoute else LoginRoute
                            navController.navigate(destination){
                                popUpTo(destination) {inclusive = true}
                                launchSingleTop = true
                            }
                        },
                        onLocationsClick = {
                            navController.navigate(LocationListRoute) {
                                popUpTo(LocationListRoute) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onNotificationsClick = {
                            val destination = if (isLoggedIn) NotificationsRoute else LoginRoute
                            navController.navigate(destination) {
                                popUpTo(destination) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onOrdersClick = {
                            val destination = if (isLoggedIn) RecentOrdersRoute else LoginRoute
                            navController.navigate(destination) {
                                popUpTo(destination) { inclusive = true }
                                launchSingleTop = true
                            }
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
                            distanceCalculation = { _ -> "" },
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
                        val profileViewModel: ProfileViewModel = viewModel()
                        val userData by profileViewModel.userData.collectAsStateWithLifecycle()
                        if (isLoggedIn && userData == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            MenuScreen(
                                locationId = route.locationId,
                                menuItemsIdAndAvailability = itemsAndAvailability,
                                unfinishedOrderId = userData?.unfinishedOrder,
                                itemRepository = itemRepository,
                                orderRepository = orderRepository,
                                onOrderSubmitted = { submittedOrderId ->
                                    orderViewModel.simulateOrderLifecycle(submittedOrderId)
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                    composable<ProfileRoute> {
                        val profileViewModel: ProfileViewModel = viewModel()
                        val authViewModel: AuthViewModel = viewModel()
                        val statusMessage by profileViewModel.statusMessage.collectAsStateWithLifecycle()
                        val userData by profileViewModel.userData.collectAsStateWithLifecycle()
                        LaunchedEffect(statusMessage) {
                            if (!statusMessage.isNullOrBlank()) {
                                Toast.makeText(application, statusMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                        userData?.let {
                            UserProfile(
                                it
                            ) {
                                authViewModel.logout()
                                navController.navigate(LocationListRoute) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                    composable<LoginRoute> {
                        val loginViewModel: AuthViewModel = viewModel()
                        LoginScreen(
                            onLogin = {email, password ->
                                loginViewModel.login(email, password) { isSuccess, message ->
                                    Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
                                    if (isSuccess) {
                                        navController.navigate(LocationListRoute) {
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
                                        navController.navigate(LocationListRoute) {
                                            popUpTo(0) {inclusive = true}
                                        }
                                    }

                                }
                            }
                        )
                    }

                    composable<RecentOrdersRoute> {
                        val profileViewModel: ProfileViewModel = viewModel()
                        val userData by profileViewModel.userData.collectAsStateWithLifecycle()
                        val ordersMap by orderViewModel.ordersMap.collectAsStateWithLifecycle()
                        val isLoading by orderViewModel.isLoading.collectAsStateWithLifecycle()
                        val rawRecentIds = userData?.recentOrders
                        var hasLoadedOnce by remember { mutableStateOf(false) }
                        LaunchedEffect(rawRecentIds) {
                            if (rawRecentIds != null) {
                                if (rawRecentIds.isNotEmpty()) {
                                    orderViewModel.loadOrdersByIds(rawRecentIds)
                                }
                                hasLoadedOnce = true
                            }
                        }
                        val visibleRecentIds = remember(rawRecentIds, ordersMap) {
                            rawRecentIds?.filter { ordersMap.containsKey(it) }?.reversed()
                        }
                        if (userData == null || !hasLoadedOnce || isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            RecentOrderList(
                                recentOrderIdList = visibleRecentIds,
                                collectOrderDC = { orderId -> ordersMap.getValue(orderId) },
                                onNavigateToOverview = { orderId ->
                                    navController.navigate(OrderOverviewRoute(orderId = orderId))
                                },
                                onNavigateToAllOrders = {
                                    navController.navigate(OrderListRoute)
                                }
                            )
                        }
                    }

                    composable<OrderListRoute> {
                        val orderListViewModel: OrderViewModel = viewModel(
                            factory = OrderViewModel.provideFactory(orderRepository, itemRepository, notificationRepository)
                        )
                        val pastOrdersMap by orderListViewModel.pastOrdersMap.collectAsStateWithLifecycle()
                        val isLoading by orderListViewModel.isLoading.collectAsStateWithLifecycle()
                        val statusMessage by orderListViewModel.statusMessage.collectAsStateWithLifecycle()

                        LaunchedEffect(Unit) {
                            orderListViewModel.loadPastOrders()
                        }

                        LaunchedEffect(statusMessage) {
                            if (!statusMessage.isNullOrBlank()) {
                                Toast.makeText(application, statusMessage, Toast.LENGTH_SHORT).show()
                            }
                        }

                        val allOrderIds = remember(pastOrdersMap) { pastOrdersMap.keys.toList() }

                        if (isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            AllOrderList(
                                allOrderIdList = allOrderIds,
                                collectOrderDC = { orderId -> pastOrdersMap.getValue(orderId) },
                                onNavigateToOverview = { orderId ->
                                    navController.navigate(OrderOverviewRoute(orderId = orderId))
                                }
                            )
                        }
                    }

                    composable<OrderOverviewRoute> { backStackEntry ->
                        val route: OrderOverviewRoute = backStackEntry.toRoute()
                        val orderViewModel: OrderViewModel = viewModel(
                            factory = OrderViewModel.provideFactory(orderRepository, itemRepository, notificationRepository)
                        )

                        val orderData by orderViewModel.orderData.collectAsStateWithLifecycle()
                        val itemsMap by orderViewModel.itemsMap.collectAsStateWithLifecycle()
                        val statusMessage by orderViewModel.statusMessage.collectAsStateWithLifecycle()

                        LaunchedEffect(route.orderId) {
                            orderViewModel.loadOrder(route.orderId)
                        }

                        val collectItemDC = remember(itemsMap) {
                            { itemId: String -> itemsMap[itemId] ?: ItemDataClass(itemName = "Loading...") }
                        }

                        orderData?.let { order ->
                            OrderOverview(
                                orderId = route.orderId,
                                orderDC = order,
                                collectItemDC = collectItemDC,
                                validationMessage = statusMessage,
                                onUpdateRating = { newRating ->
                                    orderViewModel.updateRating(route.orderId, newRating)
                                }
                            )
                        }
                    }

                    composable<NotificationsRoute> {
                        val notifications by notificationViewModel.notifications.collectAsStateWithLifecycle()
                        val isLoading by notificationViewModel.isLoading.collectAsStateWithLifecycle()
                        val statusMessage by notificationViewModel.statusMessage.collectAsStateWithLifecycle()

                        LaunchedEffect(statusMessage) {
                            if (!statusMessage.isNullOrBlank()) {
                                Toast.makeText(application, statusMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                        DisposableEffect(Unit) {
                            onDispose {
                                notificationViewModel.markAllAsRead()
                            }
                        }
                        if (isLoading && notifications.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            NotificationList(
                                notificationDCList = notifications,
                                onClearNotifications = {
                                    notificationViewModel.clearAllNotifications()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


