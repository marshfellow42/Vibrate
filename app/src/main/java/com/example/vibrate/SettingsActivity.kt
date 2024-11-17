@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vibrate

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.vibrate.preferences.PreferencesManager
import com.example.vibrate.ui.components.SettingsGroup
import com.example.vibrate.ui.components.SwitchSettingsEntry

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreen()
        }
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    // Initialize PreferencesManager
    val preferencesManager = remember { PreferencesManager(context) }

    // Use MainActivity's public variable for DynamicColor toggle
    var isDynamicColorEnabled by MainActivity.isDynamicColorEnabled

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, MainActivity::class.java))
                        if (context is SettingsActivity) {
                            context.finish()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Gray
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Dynamic Color setting entry
                SettingsGroup(
                    title = "Appearance Settings"
                ) {
                    SwitchSettingsEntry(
                        title = "Enable Dynamic Color",
                        state = isDynamicColorEnabled,
                        setState = { newState ->
                            isDynamicColorEnabled = newState
                            // Save the new state to preferences
                            preferencesManager.saveBoolean("isDynamicColorEnabled", newState)
                        },
                        description = "Enable dynamic color for a more personalized UI experience.",
                        enabled = true
                    )
                }
            }
        }
    )
    
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
