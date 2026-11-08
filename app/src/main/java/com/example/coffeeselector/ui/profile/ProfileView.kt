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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeselector.data.model.AddressDataClass
import com.example.coffeeselector.data.model.NameDataClass
import com.example.coffeeselector.data.model.UserDataClass
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun UserProfile(
    user: UserDataClass,
    onNavigateToRecentOrders: () -> Unit,
    onNavigateToPastOrders: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = user.username, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Row() {
            Text(text = "Your Name:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "${user.name.first} ${user.name.last}")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row() {
            Text(text = "Phone Number:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = user.phoneNo)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row() {
            Text(text = "Address:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(10.dp))
            val userAddress = user.address
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
            //https://www.baeldung.com/kotlin/dates
            //https://www.baeldung.com/java-datetimeformatter
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            Text(text = user.dateOfBirth?.format(formatter) ?: "No Date")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row() {
            Button({
                onNavigateToRecentOrders()
            }) {Text("Recent Orders")}
            Spacer(modifier = Modifier.width(20.dp))
            Button({
                onNavigateToPastOrders()
            }) {Text("Past Orders")}
        }


    }
}

@Preview (showBackground = true)
@Composable
fun UserProfilePreview() {
    val testUserDataClass = UserDataClass(
        "1",
        "TestUser1",
        NameDataClass("First","Last"),
        "07777 777777",
        AddressDataClass("1 Test Street",null,"Testville",null,"TE11AA"),
        LocalDate.of(2018, 12, 31),
        null,
        null)
    UserProfile(
        testUserDataClass,
        {},
        {})
}