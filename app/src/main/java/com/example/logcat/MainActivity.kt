package com.example.logcat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.logcat.ui.theme.LogCatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LogCatTheme {
                Program()
            }
        }
    }
}

@Composable
fun Program() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "dividescreen") {
        composable("dividescreen") { DivideScreen(navController) }
        composable("resultscreen/{num1}/{num2}",
            arguments = listOf(
                navArgument("num1") { type = NavType.StringType },
                navArgument("num2") { type = NavType.StringType }
            )
        ) { stackEntry ->
            val num1 = stackEntry.arguments?.getString("num1").toString()
            val num2 = stackEntry.arguments?.getString("num2").toString()
            ResultScreen(navController, num1, num2)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DivideScreen(navController: NavController = rememberNavController()) {
    val num1 = remember { mutableStateOf("") }
    val num2 = remember { mutableStateOf("") }

    val tag = "DivideScreen"
    LaunchedEffect(key1 = Unit) {
        Log.i(tag, "DivideScreen launched")
    }
    
    Column(Modifier.fillMaxSize().padding(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(value = num1.value, onValueChange = { num1.value = it },
            label = { Text("Делимое:") })
        Spacer(Modifier.height(24.dp))
        TextField(value = num2.value, onValueChange = { num2.value = it },
            label = { Text("Делитель:") })
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            Log.i(tag, "Navigating to ResultScreen")
            navController.navigate("resultscreen/${num1.value}/${num2.value}")
        }) {
            Text("Вычислить значение")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ResultScreen(navController: NavController = rememberNavController(), num1: String = "", num2: String = "") {
    val tag = "ResultScreen"
    val text = remember { mutableStateOf("") }
    val error = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = Unit) {
        Log.i(tag, "ResultScreen launched")
    }

    LaunchedEffect(key1 = num1 + num2) {
        try {
            if (num2 == "0") throw ArithmeticException("Деление на ноль")
            text.value = "Результат деления $num1 на $num2: ${num1.toFloat() / num2.toFloat()}"
        } catch (e: NumberFormatException) {
            Log.e(tag, "Ошибка NumberFormatException: ${e.message}")
            error.value = "Ошибка: неверный формат чисел!"
        } catch (e: ArithmeticException) {
            Log.e(tag, "Ошибка ArithmeticException: ${e.message}")
            error.value = "Ошибка: деление на 0!"
        }
    }

    Column(Modifier.fillMaxSize().padding(60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text.value, fontSize = 24.sp)
        if (error.value != null) {
            Text(error.value!!, color = Color.Red)
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            Log.i(tag, "Navigating to DivideScreen")
            navController.navigate("dividescreen")
        }) {
            Text("Назад", fontSize = 30.sp)
        }
    }
}