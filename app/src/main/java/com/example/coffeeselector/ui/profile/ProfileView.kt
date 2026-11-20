package com.example.coffeeselector.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    onNavigateToRecentOrders: () -> Unit,
    onNavigateToPastOrders: () -> Unit,
    modifier: Modifier = Modifier
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
                onNavigateToRecentOrders()
            }) {Text("Recent Orders")}
            Spacer(modifier = Modifier.width(20.dp))
            Button({
                onNavigateToPastOrders()
            }) {Text("Past Orders")}
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
        {},
        {}
    )
}

@Composable
fun UpdateUserProfile(
    userDC: UserDataClass,
    newUserData: (String, String, String, String, String) -> Unit
){
    var firstName by rememberSaveable { mutableStateOf(userDC.name.first) }
    var lastName by rememberSaveable { mutableStateOf(userDC.name.last) }
    var phoneNo by rememberSaveable { mutableStateOf(userDC.phoneNo) }
    var addressLine1 by rememberSaveable { mutableStateOf(userDC.address?.line1 ?: "") }
    var addressLine2 by rememberSaveable { mutableStateOf(userDC.address?.line2 ?: "") }
    var addressCity by rememberSaveable { mutableStateOf(userDC.address?.city ?: "") }
    var addressCounty by rememberSaveable { mutableStateOf(userDC.address?.county ?: "") }
    var addressPostCode by rememberSaveable { mutableStateOf(userDC.address?.postCode ?: "") }

    var dOB by rememberSaveable { mutableStateOf(userDC.dateOfBirth) }
    var error by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = userDC.username, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(firstName, { firstName = it }, label = { Text("First Name") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
        OutlinedTextField(lastName, { lastName = it }, label = { Text("Last Name") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
        OutlinedTextField(phoneNo, { phoneNo = it }, label = { Text("Phone Name") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Address:", fontSize = 15.sp)
        OutlinedTextField(addressLine1, onValueChange = { addressLine1 = it }, label = { Text("Line 1")},
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateUserProfilePreview(){
    UpdateUserProfile(testUserDataClass, {_,_,_,_,_ ->})
}