package com.example.w04

import android.os.Bundle
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.w04.ui.theme.JaruTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JaruTheme {
                HomeScreen()
            }
        }
    }
}

data class Message(val name: String, val msg: String)
data class Profile(val name: String, val intro: String)

/** TaewonyTheme 래퍼 */
@Composable
fun TaewonyTheme(content: @Composable () -> Unit) {
    JaruTheme(content = content)
}

@Composable
fun HomeScreen() {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ProfileCard(Profile("권준서", "Junseo Kwon"))
            Spacer(modifier = Modifier.height(16.dp))
            MessageCard(Message("Messge Card", "Jetpack Compose"))
        }
    }
}

/** Card 디자인 적용된 MessageCard */
@Composable
fun MessageCard(me: Message) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = me.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = me.msg,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

/** ProfileCard – 이미지 + 텍스트 */
@Composable
fun ProfileCard(data: Profile) {
    Row(
        modifier = Modifier.padding(all = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.mamti), // drawable 리소스 필요
            contentDescription = "연락처 프로필 사진",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = data.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.intro,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(
    name = "Profile Card Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun PreviewProfileCard() {
    TaewonyTheme {
        ProfileCard(Profile("권준서", "Junseo Kwon"))
    }
}

@Preview(
    name = "Message Card Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun PreviewMessageCard() {
    TaewonyTheme {
        MessageCard(Message("Messge Card", "Jetpack Compose"))
    }
}
