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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeselector.data.model.AddressDataClass
import com.example.coffeeselector.data.model.LocationDataClass
import com.google.firebase.firestore.GeoPoint

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
    distance: Int,
    onNavigateToMenu: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.Cyan),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Column(modifier = Modifier.padding(5.dp)){
            Row(modifier = Modifier
                .fillMaxWidth()) {
                Text(text = locationDC.cafeName, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, modifier = Modifier.weight(1f).padding(end = 8.dp))
                Text(text = if(distance >= 0) {
                    if (distance < 1000) "$distance m"
                    else String.format(java.util.Locale.UK, "%.1f km", distance / 1000.0)
                } else ""
                    , textAlign = TextAlign.End)
            }
            Row {
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxWidth(0.5f)
                ) {
                    Text(text = locationDC.cafeDescription.orEmpty(), fontSize = 10.sp,
                        color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Button(onNavigateToMenu, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp) ) { Text(text = "Menu")}
                }
            }
        }
    }
}

private val testLocationClass = LocationDataClass(
    id = "loc_1",
    cafeName = "Test Cafe",
    cafeCoordinates = null,
    cafeDescription = "Test description, hopefully not too long",
    streetAddress = AddressDataClass(
        "Unit 1",
        "1 Test Street",
        "Test City",
        "Test County",
        "TE11 1TE"
    )
)

private val testLocationClass2 = LocationDataClass(
    id = "loc_2",
    cafeName = "Test Cafe 2",
    cafeCoordinates = null,
    cafeDescription = "Test description, definitely should be too long",
    streetAddress = AddressDataClass(
        "Unit 1",
        "1 Test Street",
        "Test City",
        "Test County",
        "TE11 1TE"
    )
)

private val testLocationClass3 = LocationDataClass(
    id = "loc_3",
    cafeName = "Test Cafe 3",
    cafeCoordinates = null,
    cafeDescription = "Test description, hopefully not too long",
    streetAddress = AddressDataClass(
        "3 Test Street",
        "",
        "Test City",
        "Test County",
        "TE11 1TE"
    )
)


@Preview(showBackground = true)
@Composable
fun LocationListPreview() {
    LocationList(
        locations = listOf(testLocationClass, testLocationClass2, testLocationClass3),
        userLocation = null,
        distanceCalculation = { _, _ -> 1000 }
    )
}


@Composable
fun LocationList(
    locations: List<LocationDataClass>,
    userLocation: GeoPoint?,
    distanceCalculation: (GeoPoint?, GeoPoint?) -> Int,
    onNavigateToMenu: (LocationDataClass) -> Unit = {}
) {
    val sortedLocations = remember(locations, userLocation) {
        locations.map { location -> location to distanceCalculation(userLocation, location.cafeCoordinates) }
            .sortedWith(compareBy { (_, distance) ->
                if (distance < 0) Int.MAX_VALUE else distance })
    }
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        items(items = sortedLocations, key = { (location, _) -> location.id } ) { (location, distance) ->
            LocationItem(
                locationDC = location,
                distance = distance,
                onNavigateToMenu = { onNavigateToMenu(location) }
            )
        }
    }
}
