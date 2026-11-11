package com.example.coffeeselector.ui.locations

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
import com.example.coffeeselector.data.model.AddressDataClass
import com.example.coffeeselector.data.model.LocationDataClass

/**
 * https://developer.android.com/develop/ui/compose/components/button
 * rounded corner button:
 * https://stackoverflow.com/questions/75083981/how-to-define-shape-for-button-in-jetpack-compose-when-using-material-design-3
 * Lazy column guide:
 * https://developer.android.com/develop/ui/compose/lists
 */

@Composable
fun LocationItem(
    locationDC: LocationDataClass,
    distance: String?,
    onNavigateToMenu: () -> Unit
){
    val description = locationDC.cafeDescription?.let { desc ->
        if (desc.length < 41) " $desc" else {desc.take(38)+"..."}
    } ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Cyan
            ),
        verticalAlignment = Alignment.CenterVertically,

    ){
        Column(modifier = Modifier.padding(5.dp)){
            Row(modifier = Modifier
                .fillMaxWidth()) {
                Text(text = locationDC.cafeName, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                Text(text = distance ?: "", textAlign = TextAlign.End)
            }
            Row() {
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxWidth(0.5f)
                ) {
                    Text(text = description, fontSize = 10.sp, color = Color.Gray)
                    if(locationDC.streetAddress == null) {
                        Text(text = "No Listed Address")
                    } else {
                        val line1And2 = locationDC.streetAddress.line1 +
                                if(locationDC.streetAddress.line2 !=  null && locationDC.streetAddress.line2 != "") {
                                    ", " + locationDC.streetAddress.line2
                                } else {""} + ", "
                        Text(text = line1And2, fontSize = 12.sp, softWrap = true)
                        Text(text = locationDC.streetAddress.postCode,  fontWeight = FontWeight.Bold)

                    }
                }
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End,) {
                    Button(onNavigateToMenu, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(4.dp) ) { Text(text = "Menu")}
                }
            }

        }
    }


}

val testLocationClass = LocationDataClass(
    "Test Cafe",
    null,
    "Test description, hopefully not too long",
    AddressDataClass(
        "Unit 1",
        "1 Test Street",
        "Test City",
        "Test County",
        "TE11 1TE"
    )
)
val testLocationClass3 = LocationDataClass(
    "Test Cafe 3",
    null,
    "Test description, hopefully not too long",
    AddressDataClass(
        "3 Test Street",
        "",
        "Test City",
        "Test County",
        "TE11 1TE"
    )
)
val testLocationClass2 = LocationDataClass(
    "Test Cafe 2",
    null,
    "Test description, definitely should be too long",
    AddressDataClass(
        "Unit 1",
        "1 Test Street",
        "Test City",
        "Test County",
        "TE11 1TE"
    )
)



@Preview(showBackground = true)
@Composable
fun LocationItemPreview() {
    LocationItem(
        testLocationClass,
        "1000m"
    ) {}
}

@Composable
fun LocationList(
    locations: List<LocationDataClass>,
    distanceCalculation: (LocationDataClass) -> String,
    onNavigateToMenu: (LocationDataClass) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(locations) { location ->
            LocationItem(
                locationDC = location,
                distance = distanceCalculation(location),
                onNavigateToMenu = { onNavigateToMenu(location) }
            )
        }
    }
}



val testList1 = listOf(testLocationClass)
val testList2 = listOf(testLocationClass, testLocationClass2, testLocationClass3)
val testList3 = testList2 + testList2
val testList4 =  testList3 + testList3 + testList3
@Preview(showBackground = true, name = "1 Item")
@Composable
fun LocationListPreview1(){
    LocationList(
        testList1,
        { "1000" },
        {}
    )
}
@Preview(showBackground = true, name = "3 Item")
@Composable
fun LocationListPreview2(){
    LocationList(
        testList2,
        { "1000" },
        {}
    )
}

@Preview(showBackground = true, name = "6 Item")
@Composable
fun LocationListPreview3(){
    LocationList(
        testList3,
        { "1000" },
        {}
    )
}

@Preview(showBackground = true, name = "18 Item")
@Composable
fun LocationListPreview4(){
    LocationList(
        testList4,
        { "1000" },
        {}
    )
}
