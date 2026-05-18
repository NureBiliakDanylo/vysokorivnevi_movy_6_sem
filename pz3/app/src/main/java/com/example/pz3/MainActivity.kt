package com.example.pz3

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pz3.ui.theme.Pz3Theme
import java.io.File
import java.io.OutputStream
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pz3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var num1 by remember { mutableStateOf("") }
    var num2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var isRoman by remember { mutableStateOf(false) }
    var history by remember { mutableStateOf("") }

    val historyFile = "calc_history.txt"

    val createFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri: Uri? ->
        uri?.let {
            saveToUri(context, it, history)
            result = "History Saved to ${it.path?.substringAfterLast("/")}!"
        }
    }

    LaunchedEffect(Unit) {
        history = readHistory(context, historyFile)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Calculator", style = MaterialTheme.typography.headlineMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Arabic")
            Switch(checked = isRoman, onCheckedChange = { isRoman = it })
            Text("Roman")
        }

        OutlinedTextField(
            value = num1,
            onValueChange = { num1 = it },
            label = { Text(if (isRoman) "Roman Numeral 1" else "Number 1") },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isRoman) KeyboardType.Text else KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = num2,
            onValueChange = { num2 = it },
            label = { Text(if (isRoman) "Roman Numeral 2" else "Number 2") },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isRoman) KeyboardType.Text else KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        val operations = if (isRoman) {
            listOf("+", "-", "*", "/")
        } else {
            listOf("+", "-", "*", "/", "%", "^")
        }

        operations.chunked(3).forEach { rowOps ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                rowOps.forEach { op ->
                    Button(
                        onClick = {
                            val res = calculate(num1, num2, op, isRoman)
                            result = res
                            if (res != "Error") {
                                val entry = "$num1 $op $num2 = $res\n"
                                history = entry + history
                            }
                        },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(op)
                    }
                }
            }
        }

        if (result.isNotEmpty()) {
            Text(text = "Result: $result", style = MaterialTheme.typography.headlineSmall)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                createFileLauncher.launch("history.txt")
            }) {
                Text("Save History to File...")
            }

            Button(onClick = {
                context.deleteFile(historyFile)
                history = ""
                result = "History Cleared!"
            }) {
                Text("Clear History")
            }
        }

        HorizontalDivider()

        Text(text = "Current Session History", style = MaterialTheme.typography.titleLarge)
        Text(
            text = history,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun calculate(n1: String, n2: String, op: String, isRoman: Boolean): String {
    return try {
        val val1: Double
        val val2: Double

        if (isRoman) {
            val1 = RomanConverter.fromRoman(n1)?.toDouble() ?: return "Error"
            val2 = RomanConverter.fromRoman(n2)?.toDouble() ?: return "Error"
        } else {
            val1 = n1.toDoubleOrNull() ?: return "Error"
            val2 = n2.toDoubleOrNull() ?: return "Error"
        }

        val res = when (op) {
            "+" -> val1 + val2
            "-" -> val1 - val2
            "*" -> val1 * val2
            "/" -> if (val2 != 0.0) val1 / val2 else return "Div by 0"
            "%" -> val1 % val2
            "^" -> val1.pow(val2)
            else -> return "Error"
        }

        if (isRoman) {
            if (res < 1) "N/A" else RomanConverter.toRoman(res.toInt())
        } else {
            if (res % 1.0 == 0.0) res.toInt().toString() else res.toString()
        }
    } catch (e: Exception) {
        "Error"
    }
}

fun saveToUri(context: android.content.Context, uri: android.net.Uri, content: String) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream: java.io.OutputStream ->
            outputStream.write(content.toByteArray())
        }
    } catch (e: Exception) {
    }
}

fun readHistory(context: android.content.Context, filename: String): String {
    return try {
        val file = java.io.File(context.filesDir, filename)
        if (file.exists()) {
            file.readText()
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}