package com.example.coffeeselector.ui.items

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

@Composable
fun ItemInMenuList(
    itemDC: ItemDataClass,
    availability: Boolean,
    inOrder: Boolean,
    onNavigateToItem: () -> Unit,
    onAddOneToOrder: () -> Unit,
    onRemoveOneToOrder:() -> Unit
) {
    val description = itemDC.itemDescription.let { desc -> desc
    }
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
                    modifier = Modifier.fillMaxWidth(.5f))
                Button(onClick = onAddOneToOrder, enabled = availability,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .padding(horizontal = 4.dp)) {
                    Text(text = "+", fontWeight = FontWeight.Bold,fontSize = 25.sp)
                }
                Button(onClick = onRemoveOneToOrder, enabled = inOrder,
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
    collectedItemDataClass:  (String) -> ItemDataClass,
    itemInOrderCheck: (String) -> Boolean,
    onAddItemToOrder: (String) -> Unit,
    onRemoveItemToOrder: (String) -> Unit = {},
    onNavigateToItem: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(menuItemsIdAndAvailability) { (itemId, availability) ->
            ItemInMenuList(
                collectedItemDataClass(itemId),
                availability = availability,
                inOrder = itemInOrderCheck(itemId),
                onNavigateToItem = {onNavigateToItem(itemId)},
                onAddOneToOrder = {onAddItemToOrder(itemId)},
                onRemoveOneToOrder = {onRemoveItemToOrder(itemId)})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuListPreview() {
    val sampleMenuItems = mapOf(
        "item_1" to ItemDataClass(
            itemName = "Classic Burger",
            itemPrice = 12.50,
            itemDescription = "Juicy beef patty with lettuce, tomato, and house sauce."
        ),
        "item_2" to ItemDataClass(
            itemName = "Truffle Fries",
            itemPrice = 5.00,
            itemDescription = "Crispy fries tossed in truffle oil and parmesan."
        ),
        "item_3" to ItemDataClass(
            itemName = "Seasonal Tart",
            itemPrice = 7.25,
            itemDescription = "Freshly baked pastry with seasonal fruit."
        ),
        "item_4" to ItemDataClass(
            itemName = "Iced Matcha Latte",
            itemPrice = 4.50,
            itemDescription = "Ceremonial grade matcha whisked with oat milk."
        )
    )

    val menuAvailability = listOf(
        "item_1" to true,  // Available & in order
        "item_2" to true,  // Available & not in order
        "item_3" to false, // Unavailable & in order
        "item_4" to false  // Unavailable & not in order
    )

    val itemsInOrder = setOf("item_1", "item_3")

    MenuList(
        menuItemsIdAndAvailability = menuAvailability,
        collectedItemDataClass = { id ->
            sampleMenuItems[id] ?: ItemDataClass("Unknown Item", 0.0, "")
        },
        itemInOrderCheck = { id ->
            id in itemsInOrder
        },
        onAddItemToOrder = {},
        onRemoveItemToOrder = {},
        onNavigateToItem = {}
    )
}