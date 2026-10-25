package com.venz29.antigravityballon

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.venz29.antigravityballon.ui.BalloonGameApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(90, 190, 240)
        window.navigationBarColor = Color.rgb(90, 190, 240)
        setContent {
            BalloonGameApp()
        }
    }
}
