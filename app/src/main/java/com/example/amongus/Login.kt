package com.example.amongus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.amongus.model.User
import com.example.amongus.retrofit.RetrofitInstance
import com.example.amongus.ui.theme.AmongusTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmongusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginForm()
                }
            }
        }
    }
}


@Composable
fun LoginForm(modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var errorMsg by remember { mutableStateOf("") }
    var idUser by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text( text="Your Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text="Your password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    val userRegister = User(
                        _id = "",
                        username = username,
                        password = password,
                        connected = false,
                        role = null,
                        messages = emptyList()
                    )

                    RetrofitInstance
                        .api.login(userRegister)
                        .enqueue(object : Callback<User> {
                            override fun onResponse(call: Call<User>, response: Response<User>) {
                                if (response.isSuccessful) {
                                    println("User connected")
                                    val user = response.body()
                                    if (user != null) {
                                        val userId =
                                            user._id // Supposons que votre modèle User a un champ id
                                        println(userId)
                                        val intentGameBoard =
                                            Intent(context, GameBoard::class.java).apply {
                                                putExtra("USER_ID", userId)
                                                putExtra(
                                                    "USERNAME",
                                                    username
                                                ) // Vous pouvez également passer le nom d'utilisateur si nécessaire
                                            }
                                        context.startActivity(intentGameBoard)

                                    } else {
                                        println("Failed to register: ${response.code()}")
                                    }
                                }
                            }

                            override fun onFailure(call: Call<User>, t: Throwable) {
                                println("Nope connected : ${t.message}")
                                errorMsg = "Failed to connect: ${t.message}"
                            }
                        })
                } else {
                    // Gérer le cas où les champs ne sont pas remplis
                    println("Please fill all fields")
                    errorMsg = "Please fill all fields"

                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Login")
        }

        Text(text = errorMsg)
    }
}
