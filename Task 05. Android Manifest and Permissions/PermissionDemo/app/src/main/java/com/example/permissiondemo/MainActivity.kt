package com.example.permissiondemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.permissiondemo.ui.theme.PermissionDemoTheme

class MainActivity : ComponentActivity() {

    private var pendingPhone = ""
    private var pendingMessage = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PermissionDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PhoneUI()
                }
            }
        }
    }
    private fun sendSms(phoneNumber: String, messageText: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            putExtra("sms_body", messageText)
        }
        startActivity(intent)
    }

    private fun callPhone(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }
    private val requestSmsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        isGranted ->
        if (isGranted) {
            sendSms(pendingPhone, pendingMessage)
        } else {
            Toast.makeText(this, getString(R.string.sms_permission_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestSmsPermission(phoneNumber: String, messageText: String) {
        if (phoneNumber.isBlank()) {
            Toast.makeText(this, getString(R.string.empty_phone_error), Toast.LENGTH_SHORT).show()
            return
        }
        pendingPhone = phoneNumber
        pendingMessage = messageText

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED -> {
                sendSms(phoneNumber, messageText)
            }
            else -> {
                requestSmsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
            }
        }
    }

    private val requestCallPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            callPhone(pendingPhone)
        } else {
            Toast.makeText(this, getString(R.string.call_permission_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestCallPermission(phoneNumber: String) {
        if (phoneNumber.isBlank()) {
            Toast.makeText(this, getString(R.string.empty_phone_error), Toast.LENGTH_SHORT).show()
            return
        }
        pendingPhone = phoneNumber

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED -> {
                callPhone(phoneNumber)
            }
            else -> {
                requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    @Composable
    fun PhoneUI() {
        MaterialTheme {
            var phone by remember { mutableStateOf("") }
            var message by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.title),
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                    },
                    label = { Text(stringResource(R.string.phone_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it
                    },
                    label = { Text(stringResource(R.string.message_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { requestSmsPermission(phone, message) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.send_sms))
                }

                Button(
                    onClick = { requestCallPermission(phone) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.make_call))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PermissionDemoTheme {
        Greeting("Android")
    }
}