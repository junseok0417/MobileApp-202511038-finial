package com.example.appwondol


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CurrencyConverterScreen()
                }
            }
        }
    }
}

@Composable
fun CurrencyConverterScreen() {
    // 상태 변수: TextField 안의 문자열
    var wonText by remember { mutableStateOf("") }   // 원화 입력값
    var dollarText by remember { mutableStateOf("") } // 달러 입력값

    val rate = 1400.0 // 1 USD = 1400 KRW (예시 환율)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "간단 화폐 변환기",
            style = MaterialTheme.typography.headlineSmall
        )

        // 원화 입력
        OutlinedTextField(
            value = wonText,
            onValueChange = { wonText = it },
            label = { Text("원화 (KRW)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        // 달러 입력
        OutlinedTextField(
            value = dollarText,
            onValueChange = { dollarText = it },
            label = { Text("달러 (USD)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        // 버튼들
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            // 원 → 달러
            Button(
                onClick = {
                    val wonValue = wonText.toDoubleOrNull()
                    if (wonValue != null) {
                        val usd = wonValue / rate
                        // 소수 둘째 자리까지
                        dollarText = String.format("%.2f", usd)
                    } else {
                        // 숫자 아니면 그냥 비워
                        dollarText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("원 → 달러")
            }

            // 달러 → 원
            Button(
                onClick = {
                    val usdValue = dollarText.toDoubleOrNull()
                    if (usdValue != null) {
                        val krw = usdValue * rate
                        // 원화는 정수로 보통 보기 때문에 소수점 없이
                        wonText = String.format("%.0f", krw)
                    } else {
                        wonText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("달러 → 원")
            }

            // 초기화
            Button(
                onClick = {
                    wonText = ""
                    dollarText = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("초기화")
            }
        }
    }
}
