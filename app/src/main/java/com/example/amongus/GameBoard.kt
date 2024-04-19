package com.example.amongus

import android.content.Intent
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
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
                    DisplayGameBoard(
                        username = username,
                        userid = userid,
                        connectedUsers = connectedUsers,
                        currentUser = currentUser.value ?: User(
                            null,
                            "",
                            "",
                            null,
                            null,
                            null,
                            null
                            )
                    )
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

        // Request the current user
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
fun DisplayGameBoard(username: String, userid: String, connectedUsers: List<User>, currentUser: User, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column (modifier = modifier){
        Row (
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Welcome in the game messenger ${username}",
                modifier = Modifier,
                color = Color.Black,
                fontSize = 16.sp,
                style = TextStyle.Default
            )

            Button(
                onClick = {
                    val intentUserAccount = Intent(context, UserAccount::class.java).apply {
                        putExtra("USER_ID", userid)
                    }
                    context.startActivity(intentUserAccount)
                },
                modifier = Modifier.padding(16.dp)
            ) {
               Text(
                   text="My profile"
               )
            }
        }


        Button(
            onClick = {
                RetrofitInstance.api.disconnect(userid , currentUser).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            // Déconnexion réussie, vous pouvez gérer ici les actions post-déconnexion
                            // Par exemple, rediriger l'utilisateur vers une autre activité
                            println("Logout ok")

                            val intentHome = Intent(context, MainActivity::class.java)
                            context.startActivity(intentHome)

                        } else {
                            println("Failed to disconnect: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        println("Failed to disconnect: ${t.message}")
                    }
                })
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text="Disconnect"
            )
        }

        // Vérifier si la liste des utilisateurs connectés est vide
        if (connectedUsers.isEmpty()) {
            Text(
                text = "No connected users",
                modifier = Modifier,
                color = Color.Black,
                fontSize = 16.sp,
                style = TextStyle.Default
            )
        } else {
            // Afficher la liste des utilisateurs connectés
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Connected Users:",
                    modifier = Modifier,
                    color = Color.Black,
                    fontSize = 16.sp,
                    style = TextStyle.Default
                )
                connectedUsers.forEach { user ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserAvatar(user = user, modifier = Modifier.size(48.dp))
                        Text(
                            text =  if (user.username == username) "You" else "User : ${user.username}"  +
                                if (user.role.isNullOrEmpty()) " | Role : - " else " | Role : ${user.role}",
                            modifier = Modifier,
                            color = Color.Black,
                            fontSize = 16.sp,
                            style = TextStyle.Default
                        )
                        //Spacer(modifier = Modifier
                        //    .width(8.dp)
                        //    .height(24.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier
            .width(8.dp)
            .height(36.dp)
        )

        Button(
            onClick = {
               val intentUserList = Intent(context, UserList::class.java).apply {
                   putExtra("USER_ID", userid)
                   putExtra("USERNAME", username)
               }
               context.startActivity(intentUserList)

            },
            modifier = Modifier.padding(16.dp)) {
            Text(
                text="See all users",
                modifier = Modifier,
                fontSize = 16.sp,
                style = TextStyle.Default
            )
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



