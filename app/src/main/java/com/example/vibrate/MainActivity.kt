package com.example.vibrate

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                VibrateButtonScreen()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.Red,
            onPrimary = Color.White,
            background = Color.White,
            surface = Color.LightGray
        )
    ) {
        content()
    }
}

@Composable
fun VibrateButtonScreen() {
    val defaultVibrationInterval = 50L
    val defaultVibrationDuration = 5000L
    var isVibrating by remember { mutableStateOf(false) }
    var isSliderVisible by remember { mutableStateOf(false) }
    var vibrationInterval by remember { mutableLongStateOf(defaultVibrationInterval) }
    var vibrationDuration by remember { mutableLongStateOf(defaultVibrationDuration) }

    val context = LocalContext.current
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        context.getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    val sharedPreferences: SharedPreferences = context.getSharedPreferences("VibrationSettings", Context.MODE_PRIVATE)
    vibrationDuration = sharedPreferences.getLong("vibrationDuration", defaultVibrationDuration)
    vibrationInterval = sharedPreferences.getLong("vibrationInterval", defaultVibrationInterval)

    fun saveVibrationSettings() {
        with(sharedPreferences.edit()) {
            putLong("vibrationDuration", vibrationDuration)
            putLong("vibrationInterval", vibrationInterval)
            apply()
        }
    }

    fun startRealTimeVibration() {
        vibrator.cancel()
        try {
            val vibrationPattern = longArrayOf(0, vibrationDuration, vibrationInterval)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, 0))
            } else {
                vibrator.vibrate(vibrationPattern, 0)
            }
        } catch (e: Exception) {
            Log.e("VibrationError", "Failed to start vibration: ${e.message}", e)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = {
                    isVibrating = !isVibrating
                    if (isVibrating) {
                        startRealTimeVibration()
                    } else {
                        vibrator.cancel()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x00000000),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .size(200.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val imageResource = if (isVibrating) R.drawable.power_button_on else R.drawable.power_button_off
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = "Power Button",
                        modifier = Modifier.size(100.dp)
                    )
                }
            }


            if (isSliderVisible) {
                Spacer(modifier = Modifier.height(32.dp))

                Text("Vibration Duration: ${vibrationDuration}ms", fontSize = 18.sp)
                Slider(
                    value = vibrationDuration.toFloat(),
                    onValueChange = { newValue ->
                        vibrationDuration = (newValue / 50).toInt() * 50L
                        if (isVibrating) startRealTimeVibration()
                        saveVibrationSettings()
                    },
                    valueRange = 50f..5000f,
                    steps = 99,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Vibration Interval: ${vibrationInterval}ms", fontSize = 18.sp)
                Slider(
                    value = vibrationInterval.toFloat(),
                    onValueChange = { newValue ->
                        vibrationInterval = (newValue / 50).toInt() * 50L
                        if (isVibrating) startRealTimeVibration()
                        saveVibrationSettings()
                    },
                    valueRange = 50f..2000f,
                    steps = 39,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                )
            }
        }

        FloatingActionButton(
            onClick = {
                isSliderVisible = !isSliderVisible
            },
            modifier = Modifier
                .align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.White)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
        VibrateButtonScreen()
    }
}
