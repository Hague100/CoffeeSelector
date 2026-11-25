package com.example.coffeeselector.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeselector.data.model.AddressDataClass
import com.example.coffeeselector.data.model.NameDataClass
import com.example.coffeeselector.data.model.UserDataClass
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date

@Composable
fun UserProfile(
    userDC: UserDataClass,
    onNavigateToLogOut: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = userDC.username, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Row() {
            Text(text = "Your Name:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "${userDC.name.first} ${userDC.name.last}")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row() {
            Text(text = "Phone Number:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = userDC.phoneNo)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row() {
            Text(text = "Address:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(10.dp))
            val userAddress = userDC.address
            if(userAddress == null) Text(text = "No Address")
            else {
                Column() {
                    Text(text = userAddress.line1)
                    userAddress.line2?.let { Text(text = it) }
                    Text(text = userAddress.city)
                    userAddress.county?.let { Text(text = it) }
                    Text(text = userAddress.postCode)

                }
            }

        }
        Spacer(modifier = Modifier.height(10.dp))
        Row() {
            Text(text = "Date of Birth:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = userDC.dateOfBirth?.toString() ?: "No Date")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row() {
            Button({
                onNavigateToLogOut()
            }) {Text("Log Out")}

        }


    }
}

private val testUserDataClass = UserDataClass(
    id = "user_1",
    username = "TestUser1",
    name = NameDataClass("First", "Last"),
    phoneNo = "07777 777777",
    address = AddressDataClass("1 Test Street", null, "Test City", null, "TE1 1AA"),
    dateOfBirth = Date.from(LocalDate.of(2018, 12, 31).atStartOfDay(ZoneOffset.UTC).toInstant()),
    recentOrders = null,
    unfinishedOrder = null
)

@Preview (showBackground = true)
@Composable
fun UserProfilePreview() {

    UserProfile(
        testUserDataClass,
        {}
    )
}

