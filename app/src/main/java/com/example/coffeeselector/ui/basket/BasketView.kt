package com.example.coffeeselector.ui.basket

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeselector.data.model.ItemDataClass
import com.example.coffeeselector.data.model.OrderDataClass

@Composable
fun BasketItem(
    quantity: Int,
    itemDC: ItemDataClass,
    availability: Boolean,
    onAddOneToOrder: () -> Unit,
    onRemoveOneToOrder:() -> Unit
){
    val description = itemDC.itemDescription.let { desc -> desc
    }
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
                Text(text = if (availability)"(${itemDC.formattedPrice} * $quantity) " else "" , textAlign = TextAlign.End,
                    color = Color.Gray, fontSize = 10.sp)
                Text(text = if (availability)itemDC.formattedPriceMulti(quantity) else "Not Available" , textAlign = TextAlign.End)
            }
            Row() {
                Text(text = description, fontSize = 10.sp, color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(.5f))
                Button(onClick = onAddOneToOrder, enabled = availability,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .padding(horizontal = 4.dp)) {
                    Text(text = "+", fontWeight = FontWeight.Bold,fontSize = 25.sp)
                }
                Button(onClick = onRemoveOneToOrder,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)) {
                    Text(text = "-", fontWeight = FontWeight.Bold,fontSize = 25.sp)
                }
            }
        }
    }

}

val testItem = ItemDataClass(
    "Test Name",
    100.0,
    "This is a test description I do not know how long it should be"
)

@Preview(showBackground = true)
@Composable
fun BasketItemPreview(){
    Column() {
        BasketItem(
            2,
            testItem,
            true,
            {},
            {}
        )
        BasketItem(
            1,
            testItem,
            false,
            {},
            {}
        )
    }

}

@Composable
fun BasketList(
    orderDC: OrderDataClass,
    collectItemDC: (String) -> ItemDataClass,
    collectMenuAvailability: (String, String) -> Boolean,
    onAddOneToOrder: (String) -> Unit = {},
    onRemoveOneToOrder: (String) -> Unit = {},
    onClearBasket: () -> Unit = {},
    onNavigateToLocation: () -> Unit = {},
    onOrder:() -> Unit = {}
) {
    val locationId = orderDC.locationId
    var canComplete = false
        Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                items = orderDC.itemIdQuantityObject
            ) { (itemId, quantity) ->
                val itemDC = collectItemDC(itemId)
                val isAvailable = collectMenuAvailability(locationId,itemId)
                canComplete = canComplete && isAvailable
                BasketItem(
                    quantity = quantity,
                    itemDC = itemDC,
                    availability = isAvailable,
                    onAddOneToOrder = { onAddOneToOrder(itemId) },
                    onRemoveOneToOrder = { onRemoveOneToOrder(itemId) }
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = orderDC.formattedCost, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(onClick = onClearBasket, modifier = Modifier.weight(1f)) {
                    Text(text = "Clear")
                }
                Button(onClick = onNavigateToLocation, modifier = Modifier.weight(1f)) {
                    Text(text = "Add More")
                }
                Button(onClick = onOrder, enabled = orderDC.itemIdQuantityObject.isNotEmpty() && canComplete,
                    modifier = Modifier.weight(1f)) {
                    Text(text = "Order")
                }
            }
        }
    }
}


//preview data  for BasketListPreview was generated quickly with Gemini
@Preview(showBackground = true)
@Composable
fun BasketListPreview() {
    val sampleMenuItems = mapOf(
        "coffee_1" to ItemDataClass(
            itemName = "Flat White",
            itemPrice = 3.50,
            itemDescription = "Double shot espresso with velvety steamed milk."
        ),
        "coffee_2" to ItemDataClass(
            itemName = "Iced Caramel Latte",
            itemPrice = 4.20,
            itemDescription = "Chilled espresso with caramel syrup and oat milk."
        ),
        "pastry_1" to ItemDataClass(
            itemName = "Almond Croissant",
            itemPrice = 3.00,
            itemDescription = "Flaky butter croissant filled with sweet almond frangipane."
        )
    )

    val sampleOrder = OrderDataClass(
        orderId = "order_123",
        userId = "user_456",
        locationId = "leicester_city_centre",
        customerEmail = "customer@example.com",
        itemIdQuantityObject = listOf(
            "coffee_1" to 2,
            "coffee_2" to 1,
            "pastry_1" to 1
        ),
        totalCost = 14.20
    )

    BasketList(
        orderDC = sampleOrder,
        collectItemDC = { itemId ->
            sampleMenuItems[itemId] ?: ItemDataClass("Unknown Item", 0.0, "")
        },
        collectMenuAvailability = { _, itemId ->
            itemId != "pastry_1"
        }
    )
}