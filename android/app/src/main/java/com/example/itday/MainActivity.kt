package com.example.itday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.itday.ui.navigation.ItDayNavHost
import com.example.itday.ui.theme.ItDayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ItDayTheme {
                ItDayNavHost()
            }
        }
    }
}
