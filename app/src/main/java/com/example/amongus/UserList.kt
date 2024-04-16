package com.example.amongus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.amongus.model.User
import com.example.amongus.retrofit.RetrofitInstance
import com.example.amongus.ui.theme.AmongusTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserList : ComponentActivity() {
    private val username: String by lazy {
        intent.getStringExtra("USERNAME") ?: "Anonymous"
    }
    private val userid: String by lazy {
        intent.getStringExtra("USER_ID") ?: ""
    }

    private val allUsers = mutableStateListOf<User>()
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
                    println(userid)
                    SeeAllUser(
                        username = username,
                        userid = userid,
                        allUsers = allUsers,
                        currentUser = currentUser.value ?: User(
                            null,
                            "",
                            "",
                            null,
                            null,
                            null
                        )
                    )
                }
            }
        }

        // Get all users
        RetrofitInstance.api.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    users?.let {
                        allUsers.addAll(it)
                    }
                } else {
                    println("Failed to fetch all users: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                println("Nope : ${t.message}")
            }
        })

        // Get current user
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
fun SeeAllUser(username : String, userid : String, allUsers: List<User>, currentUser: User, modifier: Modifier = Modifier){
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
                text = "Hello ${username}",
                modifier = Modifier,
                color = Color.Black,
                fontSize = 16.sp,
                style = TextStyle.Default
            )
        }

        if(allUsers.isEmpty()) {
            Text(
                text = "Unavailaible List of user",
                modifier = Modifier,
                color = Color.Black,
                fontSize = 16.sp,
                style = TextStyle.Default
            )
        } else {
            // Afficher la liste des utilisateurs
            Column (modifier = Modifier.padding(16.dp)){
                Text(
                    text = "Registered Users:",
                    modifier = Modifier,
                    color = Color.Black,
                    fontSize = 16.sp,
                    style = TextStyle.Default
                )
                allUsers.forEach { user ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DisplayAvatar(user = user, modifier = Modifier.size(48.dp))
                            Text(
                                text = "User : ${user.username}" + if (user.role.isNullOrEmpty()) " | Role : - " else " | Role : ${user.role}",
                                modifier = Modifier.weight(1f),
                                color = Color.Black,
                                fontSize = 16.sp,
                                style = TextStyle.Default
                            )
                            Button(onClick = { /*TODO*/ }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.MailOutline , contentDescription = "Send a message" )
                                }
                            }
                        }
                    }


                    /*ListItem(
                        headlineContent = {
                            Text(
                                text="One line list item with 24x24 icon",
                                modifier = Modifier,
                                color = Color.Black,
                                fontSize = 16.sp,
                                style = TextStyle.Default
                            )
                        },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = "Localized description",
                            )
                        }
                    )

                    //Divider(
                    //    modifier = Modifier.fillMaxHeight().width(1.dp)
                    //)
                    */

                }
            }
        }
    }
}

@Composable
fun DisplayAvatar(user: User, modifier: Modifier = Modifier) {
    val imageResId = R.drawable.anonymous

    Image(
        painter = painterResource(id = imageResId),
        contentDescription = "User Avatar",
        modifier = modifier
        //.width(75.dp)
    )
}
