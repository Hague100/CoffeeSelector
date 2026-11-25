package com.example.coffeeselector.ui.notification

import androidx.compose.animation.AnimatedVisibility
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
import com.example.coffeeselector.data.model.NotificationDataClass
import com.google.firebase.Timestamp
import java.util.Date

@Composable
fun NotificationListItem(
    notificationDC: NotificationDataClass
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Green
            ),
        verticalAlignment = Alignment.CenterVertically) {
        Column (Modifier.padding(5.dp)){
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = notificationDC.formattedTimeSentAt, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, modifier = Modifier.weight(1f).padding(end = 8.dp))
                AnimatedVisibility(!notificationDC.isRead){
                    Text(text = "New", textAlign = TextAlign.End, color = Color.Magenta,
                        fontWeight = FontWeight.Bold)
                }
            }
            Text(text = notificationDC.message, modifier = Modifier.padding(5.dp))
        }
    }
}

val sampleNotifications = listOf(
    NotificationDataClass(
        id = "1",
        sentAt = Timestamp.now(),
        message = "Test message big message that is a bit long",
        isRead = false
    ),
    NotificationDataClass(
        id = "2",
        sentAt = Timestamp(Date(System.currentTimeMillis() - 3_600_000)),
        message = "Test message big message that is a bit long",
        isRead = false
    ),
    NotificationDataClass(
        id = "3",
        sentAt = Timestamp(Date(System.currentTimeMillis() - 86_400_000)),
        message = "Test message big message that is a bit long",
        isRead = true
    )
)


@Preview(showBackground = true)
@Composable
fun NotificationListItemPreview() {
    Column{
        NotificationListItem(sampleNotifications[1])
        NotificationListItem(sampleNotifications[2])
    }
}

@Composable
fun NotificationList(
    notificationDCList: List<NotificationDataClass>?,
    onClearNotifications: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp)
        ) {
            Text(text = "Notifications", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        val isNullOrEmpty = notificationDCList.isNullOrEmpty()

        if (!isNullOrEmpty) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(items = notificationDCList, key = { it.id }) { notificationDC ->
                    NotificationListItem(notificationDC)
                }
            }
        } else{
            Text(text = "No Notifications Found", fontWeight = FontWeight.Bold, fontSize = 40.sp,
                 textAlign = TextAlign.Center, modifier = Modifier.weight(1f).fillMaxWidth()
                     .padding(all = 10.dp)
            )
        }
        Row(modifier = Modifier.fillMaxWidth().background(Color.LightGray).padding(8.dp)) {
            Button(onClick = {onClearNotifications()}, modifier = Modifier.weight(1f),
                enabled = !isNullOrEmpty){ Text(text = "Clear Notifications") }
        }
    }
}

@Preview(showBackground = true, name = "Populated List")
@Composable
fun NotificationListPreview() {
    NotificationList(
        notificationDCList = sampleNotifications,
        onClearNotifications = {}
    )
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun NotificationListEmptyPreview() {
    NotificationList(
        notificationDCList = emptyList(),
        onClearNotifications = {}
    )
}