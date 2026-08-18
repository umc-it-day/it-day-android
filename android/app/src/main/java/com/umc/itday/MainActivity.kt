package com.umc.itday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.umc.itday.ui.navigation.ItDayNavHost
import com.umc.itday.ui.theme.ItDayTheme

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
