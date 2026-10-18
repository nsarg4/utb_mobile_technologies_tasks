package com.example.watertracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.example.watertracker.ui.theme.WaterTrackerTheme
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class MainActivity : ComponentActivity() {
    private val TAG = "WaterTrackerLifecycle"
    private lateinit var prefs: SharedPreferences
    private var glasses by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        prefs = getSharedPreferences("water_tracker_prefs", Context.MODE_PRIVATE)

        glasses = prefs.getInt("glasses", 0)
        Log.d(TAG, "onCreate() called. Loaded glasses from storage: $glasses")

        setContent {
            WaterTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WaterTrackerScreen(
                        glasses = glasses,
                        onAddGlass = {
                            glasses++
                            saveGlasses()
                            Log.d(TAG, "User added a glass. Total now: $glasses")
                        },
                        onReset = {
                            glasses = 0
                            saveGlasses()
                            Log.d(TAG, "User reset progress to 0")
                        }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        glasses = prefs.getInt("glasses", 0)
        Log.d(TAG, "onStart() called. Reading latest value from storage: $glasses")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called. App is interactive. Current glasses: $glasses")
    }

    override fun onPause() {
        super.onPause()
        saveGlasses()
        Log.d(TAG, "onPause() called. Saved current value before leaving screen: $glasses")
    }

    override fun onStop() {
        super.onStop()
        saveGlasses()
        Log.d(TAG, "onStop() called. Final save before app becomes invisible: $glasses")
    }

    override fun onDestroy() {
        saveGlasses()
        Log.d(TAG, "onDestroy() called. Last value saved: $glasses")
        super.onDestroy()
    }

    private fun saveGlasses() {
        prefs.edit().putInt("glasses", glasses).apply()
    }
}

@Composable
fun WaterTrackerScreen(
    glasses: Int,
    onAddGlass: () -> Unit,
    onReset: () -> Unit
) {
    val goal = 8


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Daily Water Tracker",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "$glasses / $goal glasses",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.headlineSmall
            )


            Button(
                onClick = onAddGlass,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text("Add one glass")
            }

            Button(
                onClick = onReset,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Reset")
            }
        }
    }
}

