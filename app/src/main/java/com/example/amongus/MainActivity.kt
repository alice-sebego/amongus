package com.example.amongus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.core.content.ContextCompat.startActivity
import com.example.amongus.model.User
import com.example.amongus.retrofit.RetrofitInstance
import com.example.amongus.ui.theme.AmongusTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmongusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegisterForm()
                }
            }
        }
    }
}

@Composable
fun RegisterForm(modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column (modifier = modifier) {

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Your Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Your password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            keyboardActions = KeyboardActions(
                onDone = { /* Handle login action here */ }
            )
        )

        Button(
            onClick = {
                val userRegister = User(username = username, password = password, connected = false, role = null, messages = emptyList())
                RetrofitInstance.api.register(userRegister).enqueue(object : Callback<User>{
                    override fun onResponse(call: Call<User>, response: Response<User>){
                        if(response.isSuccessful){
                            println("User registered")
                            val intentLogin = Intent(context, Login::class.java)
                            context.startActivity(intentLogin)
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        println("Nope :" + t.message)
                    }
                })


            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Register")
        }

        Text(
            text = "Already Registered ? Login",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    val intentSignup = Intent(context, Login::class.java)
                    context.startActivity(intentSignup)
                }
        )

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AmongusTheme {
        RegisterForm()
    }
}