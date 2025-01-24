package com.github.turkurt656.encsms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.github.turkurt656.encsms.ui.theme.EncSmsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EncSmsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SmsScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SmsScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    var phoneNumber by remember { mutableStateOf("+905366350905") }
    var message by remember { mutableStateOf("Hello") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.values.all { it }) {
                context.sendSms(phoneNumber, message)
            } else {
                Toast.makeText(context, "SMS permissions required", Toast.LENGTH_LONG).show()
            }
        }
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") }
        )
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") }
        )
        Button(
            onClick = {
                if (context.hasSmsPermission()) {
                    context.sendSms(phoneNumber, message)
                } else {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_SMS
                        )
                    )
                }
            }
        ) {
            Text("Send SMS")
        }
        // Display received SMS messages here
    }
}

fun Context.hasSmsPermission() =
    ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) ==
            PackageManager.PERMISSION_GRANTED

fun Context.sendSms(phoneNumber: String, message: String) {
    val smsManager = SmsManager.getDefault()
    smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show()
}