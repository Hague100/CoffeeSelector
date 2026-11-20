package com.example.coffeeselector.ui.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeselector.data.model.ItemDataClass
import com.example.coffeeselector.data.model.OrderDataClass

@Composable
fun OrderListItem(
    orderId: String,
    orderDC: OrderDataClass,
    onNavigateToOverview: () -> Unit
) {
    val itemQuantity = orderDC.itemIdQuantityObject.toList().sumOf { (_, amount) -> amount }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Yellow
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = orderId, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Text(
                    text = orderDC.statusMessage,
                    textAlign = TextAlign.End
                )

            }
            Row(modifier = Modifier.padding(all = 5.dp)){
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Quantity of Items: $itemQuantity", fontSize = 15.sp, color = Color.Gray, fontStyle = FontStyle.Italic)
                    Spacer(modifier = Modifier.padding(vertical = 3.dp))
                    Text(text = "Total Cost: ${orderDC.formattedCost}", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
                Button(onClick = { onNavigateToOverview() },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)) {
                    Text(text = "View Order", fontWeight = FontWeight.Bold,fontSize = 25.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderListItemPreview() {
    val testOrder = OrderDataClass(
        itemIdQuantityObject = mapOf(
            "item_01" to 2,
            "item_02" to 1
        ),
        totalCost = 24.50
    )
    OrderListItem(
        orderId = "D8PvloqiczrzXik3SWjZ",
        orderDC = testOrder,
        onNavigateToOverview = {}
    )
}

@Composable
fun RecentOrderList(
    recentOrderIdList: List<String>?,
    collectOrderDC: (String) -> OrderDataClass,
    onNavigateToOverview: (String) -> Unit,
    onNavigateToAllOrders: () -> Unit
){

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(8.dp)) {
            Text(text = "Recent Orders", fontWeight = FontWeight.Bold,
                fontSize = 20.sp)
        }
        if (!recentOrderIdList.isNullOrEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(items = recentOrderIdList) { orderId ->
                    val orderDC = collectOrderDC(orderId)
                    OrderListItem(
                        orderId = orderId,
                        orderDC = orderDC,
                        onNavigateToOverview = { onNavigateToOverview(orderId) }
                    )
                }
            }
        } else{
            Text(text = "No Recent Orders", fontWeight = FontWeight.Bold,
                fontSize = 40.sp, modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(all = 10.dp)
                , textAlign = TextAlign.Center)
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(8.dp)) {
            Button(onClick = {onNavigateToAllOrders()}, modifier = Modifier.weight(1f)){
                Text(text = "All Orders")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun RecentOrderListPreview() {
    val testOrders = mapOf(
        "D8PvloqiczrzXik3SWjZ" to OrderDataClass(
            itemIdQuantityObject = mapOf("item_01" to 2, "item_02" to 1),
            totalCost = 24.50
        ),
        "K9Lmnpqrstuvwxyz1234" to OrderDataClass(
            itemIdQuantityObject = mapOf("item_03" to 4),
            totalCost = 48.00
        ),
        "X7Abcdefghijklmn5678" to OrderDataClass(
            itemIdQuantityObject = mapOf("item_01" to 1, "item_04" to 1, "item_05" to 2),
            totalCost = 19.99
        )
    )

    RecentOrderList(
        recentOrderIdList = testOrders.keys.toList(),
        collectOrderDC = { orderId ->
            testOrders[orderId] ?: OrderDataClass()
        },
        onNavigateToOverview = {},
        onNavigateToAllOrders = {}
    )
}

@Preview(showBackground = true)
@Composable
fun RecentOrderListEmptyPreview() {
    RecentOrderList(
        recentOrderIdList = emptyList(),
        collectOrderDC = { OrderDataClass() },
        onNavigateToOverview = {},
        onNavigateToAllOrders = {}
    )
}

@Composable
fun AllOrderList(
    allOrderIdList: List<String>?,
    collectOrderDC: (String) -> OrderDataClass,
    onNavigateToOverview: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp)
        ){
            Text(text = "All Orders", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        if (!allOrderIdList.isNullOrEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(items = allOrderIdList) { orderId ->
                    val orderDC = collectOrderDC(orderId)
                    OrderListItem(
                        orderId = orderId,
                        orderDC = orderDC,
                        onNavigateToOverview = { onNavigateToOverview(orderId) }
                    )
                }
            }
        } else{
            Text(text = "No Orders Found", fontWeight = FontWeight.Bold,
                fontSize = 40.sp, modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(all = 10.dp)
                , textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AllOrderListPreview() {
    val mockOrders = mapOf(
        "D8PvloqiczrzXik3SWjZ" to OrderDataClass(
            itemIdQuantityObject = mapOf("item_01" to 2, "item_02" to 1),
            totalCost = 24.50
        ),
        "K9Lmnpqrstuvwxyz1234" to OrderDataClass(
            itemIdQuantityObject = mapOf("item_03" to 4),
            totalCost = 48.00
        ),
        "X7Abcdefghijklmn5678" to OrderDataClass(
            itemIdQuantityObject = mapOf("item_01" to 1, "item_04" to 1, "item_05" to 2),
            totalCost = 19.99
        ),
        "P2Qwertyuiopasdf9012" to OrderDataClass(
            itemIdQuantityObject = mapOf("item_02" to 3),
            totalCost = 36.00
        )
    )

    AllOrderList(
        allOrderIdList = mockOrders.keys.toList(),
        collectOrderDC = { orderId ->
            mockOrders[orderId] ?: OrderDataClass()
        },
        onNavigateToOverview = {}
    )
}

@Preview(showBackground = true)
@Composable
fun AllOrderListEmptyPreview() {
    AllOrderList(
        allOrderIdList = emptyList(),
        collectOrderDC = { OrderDataClass() },
        onNavigateToOverview = {}
    )
}

@Composable
fun OrderItem(
    quantity: Int,
    itemDC: ItemDataClass
){
    val description = itemDC.itemDescription
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Blue
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${itemDC.itemName} ($quantity)", fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
            }
            Row {
                Text(text = description, fontSize = 10.sp, color = Color.Gray,
                    modifier = Modifier.weight(2f))
                Text(text = "(${itemDC.formattedPrice} * $quantity) = ${itemDC.formattedPriceMulti(quantity)}", textAlign = TextAlign.End,
                    fontSize = 14.sp, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderItemPreview() {
    val sampleItem = ItemDataClass(
        itemName = "Flat White",
        itemPrice = 3.40,
        itemDescription = "Double shot of espresso with velvety steamed microfoam milk."
    )

    OrderItem(
        quantity = 2,
        itemDC = sampleItem
    )
}

@Composable
fun OrderOverview(
    orderId: String,
    orderDC: OrderDataClass,
    collectItemDC: (String) -> ItemDataClass,
    validationMessage: String?,
    onUpdateRating: (Int) -> Unit
){
    var rating by rememberSaveable { mutableStateOf(orderDC.rating?.toString() ?: "") }
    var message: String? by rememberSaveable { mutableStateOf(validationMessage) }
    LaunchedEffect(validationMessage) {
        message = validationMessage
    }
    val itemList = orderDC.itemIdQuantityObject.map{ (id, quantity) ->
        collectItemDC(id) to quantity
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp)
        ) {
            Text(
                text = orderId, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Text(
                text = orderDC.statusMessage,
                textAlign = TextAlign.End
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp)
        ) {
            Text(
                text = orderDC.formattedCost,
                fontSize = 15.sp, modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            AnimatedVisibility(visible = orderDC.formattedCollectedTime != null) {
                Text(
                    text = orderDC.formattedCollectedTime.orEmpty(),
                    textAlign = TextAlign.End,
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items = itemList) { (itemDC, quantity) ->
                OrderItem(
                    quantity = quantity,
                    itemDC = itemDC
                )
            }

        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(8.dp)
        ){
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = rating,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || (newValue.length == 1 && newValue[0] in '1'..'5')) {
                                rating = newValue
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(56.dp)
                    )
                    Text(
                        text = " / 5",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
                AnimatedVisibility(visible = !message.isNullOrBlank()) {
                    message?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                }
            }
            Button({
                val parsedRating = rating.trim().toIntOrNull()
                if (parsedRating != null && parsedRating in 1..5) {
                    message = null
                    onUpdateRating(parsedRating)
                } else {
                    message = "Please enter a rating between 1 and 5"
                }
            }, enabled = orderDC.dateTimeOrderCollected != null) {
                Text(text = "Submit")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderOverviewCompletedPreview() {
    val testItems = mapOf(
        "coffee_1" to ItemDataClass(
            itemName = "Flat White",
            itemPrice = 3.40,
            itemDescription = "Double shot of espresso with velvety steamed microfoam milk."
        ),
        "coffee_2" to ItemDataClass(
            itemName = "Iced Caramel Latte",
            itemPrice = 4.10,
            itemDescription = "Espresso poured over cold milk, ice, and sweet caramel syrup."
        ),
        "pastry_1" to ItemDataClass(
            itemName = "Almond Croissant",
            itemPrice = 3.25,
            itemDescription = "Flaky butter croissant filled with sweet almond frangipane."
        )
    )

    val completedOrder = OrderDataClass(
        userId = "user_01",
        locationId = "loc_leicester_01",
        itemIdQuantityObject = mapOf(
            "coffee_1" to 2,
            "coffee_2" to 1,
            "pastry_1" to 1
        ),
        dateTimeOrderInitiated = java.util.Date(),
        dateTimeOrderFinished = java.util.Date(),
        dateTimeOrderCollected = java.util.Date(),
        totalCost = 14.15,
        rating = 5
    )

    OrderOverview(
        orderId = "D8PvloqiczrzXik3SWjZ",
        orderDC = completedOrder,
        collectItemDC = { itemId ->
            testItems[itemId] ?: ItemDataClass(itemName = "Unknown Item")
        },
        validationMessage = null,
        onUpdateRating = {}
    )
}

@Preview(showBackground = true)
@Composable
fun OrderOverviewReadyPreview() {
    val mockItems = mapOf(
        "coffee_1" to ItemDataClass(
            itemName = "Flat White",
            itemPrice = 3.40,
            itemDescription = "Double shot of espresso with velvety steamed microfoam milk."
        )
    )

    val readyOrder = OrderDataClass(
        userId = "user_01",
        locationId = "loc_leicester_01",
        itemIdQuantityObject = mapOf("coffee_1" to 1),
        dateTimeOrderInitiated = java.util.Date(),
        dateTimeOrderFinished = java.util.Date(),
        dateTimeOrderCollected = null,
        totalCost = 3.40
    )

    OrderOverview(
        orderId = "K9Lmnpqrstuvwxyz1234",
        orderDC = readyOrder,
        collectItemDC = { itemId ->
            mockItems[itemId] ?: ItemDataClass(itemName = "Unknown Item")
        },
        validationMessage = null,
        onUpdateRating = {}
    )
}





