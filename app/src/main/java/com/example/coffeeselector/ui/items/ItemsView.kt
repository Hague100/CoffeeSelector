package com.example.coffeeselector.ui.items

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeselector.data.model.ItemDataClass
import com.example.coffeeselector.data.repository.ItemRepository
import com.example.coffeeselector.data.repository.OrderRepository

@Composable
fun ItemInMenuList(
    itemDC: ItemDataClass,
    availability: Boolean,
    inOrder: Boolean,
    onNavigateToItem: () -> Unit,
    onAddOneToOrder: () -> Unit,
    onRemoveOneToOrder:() -> Unit
) {
    val description = itemDC.itemDescription
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Red
            ),
        verticalAlignment = Alignment.CenterVertically
        ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = itemDC.itemName, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Text(text = if (availability)itemDC.formattedPrice else "Not Available" , textAlign = TextAlign.End)
            }
            Row() {
                Text(text = description, fontSize = 10.sp, color = Color.Gray,
                    modifier = Modifier.weight(2f))
                Button(onClick = onAddOneToOrder, enabled = availability,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)) {
                    Text(text = "+", fontWeight = FontWeight.Bold,fontSize = 25.sp)
                }
                Button(onClick = onRemoveOneToOrder, enabled = inOrder,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)) {
                    Text(text = "-", fontWeight = FontWeight.Bold,fontSize = 25.sp)
                }
            }
        }
    }
}

private val testItem = ItemDataClass(
    itemName = "Test Name",
    itemPrice = 100.0,
    itemDescription = "This is a test description I do not know how long it should be"
)

@Preview(showBackground = true)
@Composable
fun ItemInMenuListPreview() {
    Column() {
        ItemInMenuList(
            testItem,
            availability = true,
            inOrder = true,
            onNavigateToItem = {},
            onAddOneToOrder = {},
            onRemoveOneToOrder = {})
        ItemInMenuList(
            testItem,
            availability = false,
            inOrder = true,
            onNavigateToItem = {},
            onAddOneToOrder = {},
            onRemoveOneToOrder = {})
        ItemInMenuList(
            testItem,
            availability = false,
            inOrder = false,
            onNavigateToItem = {},
            onAddOneToOrder = {},
            onRemoveOneToOrder = {})
        ItemInMenuList(
            testItem,
            availability = true,
            inOrder = false,
            onNavigateToItem = {},
            onAddOneToOrder = {},
            onRemoveOneToOrder = {})
    }

}


@Composable
fun MenuList(
    menuItemsIdAndAvailability: List<Pair<String, Boolean>>,
    collectedItemDataClass: (String) -> ItemDataClass,
    itemInOrderCheck: (String) -> Boolean,
    onAddItemToOrder: (String) -> Unit,
    onRemoveItemToOrder: (String) -> Unit = {},
    onNavigateToItem: (String) -> Unit = {}
) {
    var showUnavailable by remember { mutableStateOf(true) }

    val displayedItems = remember(menuItemsIdAndAvailability, showUnavailable) {
        if (showUnavailable) {
            menuItemsIdAndAvailability
        } else {
            menuItemsIdAndAvailability.filter { (_, availability) -> availability }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Show unavailable items",
                fontWeight = FontWeight.Medium
            )
            Switch(
                checked = showUnavailable,
                onCheckedChange = { showUnavailable = it }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                items = displayedItems,
                key = { it.first }
            ) { (itemId, availability) ->
                ItemInMenuList(
                    collectedItemDataClass(itemId),
                    availability = availability,
                    inOrder = itemInOrderCheck(itemId),
                    onNavigateToItem = { onNavigateToItem(itemId) },
                    onAddOneToOrder = { onAddItemToOrder(itemId) },
                    onRemoveOneToOrder = { onRemoveItemToOrder(itemId) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuListPreview() {
    val sampleMenuItems = remember {
        mapOf(
            "item_1" to ItemDataClass(
                id = "item_1",
                itemName = "Classic Burger",
                itemPrice = 12.50,
                itemDescription = "Juicy beef patty with lettuce, tomato, and house sauce."
            ),
            "item_2" to ItemDataClass(
                id = "item_2",
                itemName = "Truffle Fries",
                itemPrice = 5.00,
                itemDescription = "Crispy fries tossed in truffle oil and parmesan."
            ),
            "item_3" to ItemDataClass(
                id = "item_3",
                itemName = "Seasonal Tart",
                itemPrice = 7.25,
                itemDescription = "Freshly baked pastry with seasonal fruit."
            ),
            "item_4" to ItemDataClass(
                id = "item_4",
                itemName = "Iced Matcha Latte",
                itemPrice = 4.50,
                itemDescription = "Ceremonial grade matcha whisked with oat milk."
            )
        )
    }

    val menuAvailability = remember {
        listOf(
            "item_1" to true,  // Available & in order
            "item_2" to true,  // Available & not in order
            "item_3" to false, // Unavailable & in order
            "item_4" to false  // Unavailable & not in order
        )
    }

    var itemsInOrder by remember {
        mutableStateOf(setOf("item_1", "item_3"))
    }

    MenuList(
        menuItemsIdAndAvailability = menuAvailability,
        collectedItemDataClass = { id ->
            sampleMenuItems[id] ?: ItemDataClass(
                id = id,
                itemName = "Unknown Item",
                itemPrice = 0.0,
                itemDescription = ""
            )
        },
        itemInOrderCheck = { id ->
            id in itemsInOrder
        },
        onAddItemToOrder = { id ->
            itemsInOrder = itemsInOrder + id
        },
        onRemoveItemToOrder = { id ->
            itemsInOrder = itemsInOrder - id
        },
        onNavigateToItem = {}
    )
}

@Composable
fun MenuScreen(
    locationId: String,
    menuItemsIdAndAvailability: List<Pair<String, Boolean>>,
    unfinishedOrderId: String? = null,
    itemRepository: ItemRepository,
    orderRepository: OrderRepository,
    onNavigateToItem: (String) -> Unit,
    onOrderSubmitted: () -> Unit
) {
    val viewModel: ItemViewModel = viewModel(
        factory = ItemViewModel.provideFactory(
            itemRepository = itemRepository,
            orderRepository = orderRepository
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(locationId, menuItemsIdAndAvailability, unfinishedOrderId) {
        viewModel.initializeMenu(
            locationId = locationId,
            itemsIdAndAvailability = menuItemsIdAndAvailability,
            unfinishedOrderId = unfinishedOrderId
        )
    }
    LaunchedEffect(uiState.orderSubmitted) {
        if (uiState.orderSubmitted) {
            onOrderSubmitted()
        }
    }

    Scaffold(
        bottomBar = {
            // Show checkout bar whenever there is at least 1 item in the order
            if (uiState.totalItemCount > 0) {
                Surface(shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${uiState.totalItemCount} item(s)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = uiState.formattedTotalCost,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Button(
                            onClick = { viewModel.submitCurrentOrder() },
                            enabled = !uiState.isSavingOrder
                        ) {
                            Text(text = if (uiState.isSavingOrder) "Saving..." else "Submit Order")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        uiState.errorMessage?.let { error ->
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(onClick = { viewModel.clearError() }) {
                                        Text("Dismiss")
                                    }
                                }
                            }
                        }

                        // 5. Pass ViewModel state & callbacks into your MenuList
                        MenuList(
                            menuItemsIdAndAvailability = uiState.menuItemsIdAndAvailability,
                            collectedItemDataClass = { id ->
                                uiState.loadedItems[id] ?: ItemDataClass(
                                    id = id,
                                    itemName = "Unavailable Item",
                                    itemPrice = 0.0,
                                    itemDescription = ""
                                )
                            },
                            itemInOrderCheck = { id ->
                                (uiState.itemQuantities[id] ?: 0) > 0
                            },
                            onAddItemToOrder = { id ->
                                viewModel.addItemToOrder(id)
                            },
                            onRemoveItemToOrder = { id ->
                                viewModel.removeItemFromOrder(id)
                            },
                            onNavigateToItem = onNavigateToItem
                        )
                    }
                }
            }
        }
    }
}