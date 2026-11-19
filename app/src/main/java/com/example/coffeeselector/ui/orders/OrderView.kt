package com.example.coffeeselector.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeselector.data.model.OrderDataClass

@Composable
fun OrderListItem(
    orderId: String,
    orderDC: OrderDataClass,
    onNavigateToOverview: () -> Unit
) {
    val itemQuantity = orderDC.itemIdQuantityObject.sumOf { (_, amount) -> amount }
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
        itemIdQuantityObject = listOf(
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
            itemIdQuantityObject = listOf("item_01" to 2, "item_02" to 1),
            totalCost = 24.50
        ),
        "K9Lmnpqrstuvwxyz1234" to OrderDataClass(
            itemIdQuantityObject = listOf("item_03" to 4),
            totalCost = 48.00
        ),
        "X7Abcdefghijklmn5678" to OrderDataClass(
            itemIdQuantityObject = listOf("item_01" to 1, "item_04" to 1, "item_05" to 2),
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
            itemIdQuantityObject = listOf("item_01" to 2, "item_02" to 1),
            totalCost = 24.50
        ),
        "K9Lmnpqrstuvwxyz1234" to OrderDataClass(
            itemIdQuantityObject = listOf("item_03" to 4),
            totalCost = 48.00
        ),
        "X7Abcdefghijklmn5678" to OrderDataClass(
            itemIdQuantityObject = listOf("item_01" to 1, "item_04" to 1, "item_05" to 2),
            totalCost = 19.99
        ),
        "P2Qwertyuiopasdf9012" to OrderDataClass(
            itemIdQuantityObject = listOf("item_02" to 3),
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





