package com.example.amongus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.amongus.model.User
import com.example.amongus.retrofit.RetrofitInstance
import com.example.amongus.ui.theme.AmongusTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserAccount : ComponentActivity() {
    private val userid: String by lazy {
        intent.getStringExtra("USER_ID") ?: ""
    }
    private val currentUser = mutableStateOf<User?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmongusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                }
            }
        }

        // Get user
        RetrofitInstance.api.getUser(userid).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    println(user)
                    currentUser.value = user
                } else {
                    println("Failed to fetch current user: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                println("Nope : ${t.message}")
            }
        })
    }
}

@Composable
fun DisplayProfile(user: User, modifier: Modifier = Modifier){
    val context = LocalContext.current

    Column (modifier = modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome in your profile ${user.username} ",
                modifier = Modifier,
                color = Color.Black,
                fontSize = 20.sp,
                style = TextStyle.Default
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Picture
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your role : ${user.role}"
            )
        }
    }
}

