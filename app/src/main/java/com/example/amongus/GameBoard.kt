package com.example.amongus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.amongus.model.User
import com.example.amongus.retrofit.RetrofitInstance
import com.example.amongus.ui.theme.AmongusTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GameBoard : ComponentActivity() {
    private val username: String by lazy {
        intent.getStringExtra("USERNAME") ?: "Anonymous"
    }
    private val userid: String by lazy {
        intent.getStringExtra("USER_ID") ?: ""
    }
    private val connectedUsers = mutableStateListOf<User>()
    //private val currentUser = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmongusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplayGameBoard(name = username, connectedUsers = connectedUsers)
                }
            }
        }

        // Request Display All connected users
        RetrofitInstance.api.findConnected().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    users?.let {
                        connectedUsers.addAll(it)
                    }
                } else {
                    println("Failed to fetch connected users: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                println("Nope : ${t.message}")
            }
        })

        // gerer logout
        //RetrofitInstance.api.disconnect().enqueue(object: Callback<User> {
        //    override fun onResponse(call: Call<List<User>>, response: Response<List<User>>){
        //        if(response.isSuccessful){
        //            println(response.body())
        //        }
        //    }
        //})
    }

}


@Composable
fun DisplayGameBoard(name : String, connectedUsers: List<User>, modifier: Modifier = Modifier) {
    Column (modifier = modifier){
        Row (
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "Welcome in the game ${name}")
        }

        // Vérifier si la liste des utilisateurs connectés est vide
        if (connectedUsers.isEmpty()) {
            Text(text = "No connected users")
        } else {
            // Afficher la liste des utilisateurs connectés
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Connected Users:")
                connectedUsers.forEach { user ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserAvatar(user = user, modifier = Modifier.size(48.dp))
                        Text(text = "User : ${user.username}" +
                                if (user.role.isNullOrEmpty()) " | Role : - " else " | Role : ${user.role}")
                        Spacer(modifier = Modifier.width(8.dp).height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun UserAvatar(user: User, modifier: Modifier = Modifier) {
    val imageResId = R.drawable.anonymous

    Image(
        painter = painterResource(id = imageResId),
        contentDescription = "User Avatar",
        modifier = modifier
            //.width(75.dp)
    )
}

