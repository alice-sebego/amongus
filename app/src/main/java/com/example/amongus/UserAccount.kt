package com.example.amongus

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.amongus.model.ImgurResponse
import com.example.amongus.model.User
import com.example.amongus.retrofit.RetrofitInstance
import com.example.amongus.ui.theme.AmongusTheme
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class UserAccount : ComponentActivity() {
    private val userid: String by lazy {
        intent.getStringExtra("USER_ID") ?: ""
    }
    private val currentUser = mutableStateOf<User?>(null)

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // L'utilisateur a sélectionné une image
            // Appelez onPictureChanged avec l'URI de l'image sélectionnée
            onPictureChanged(it)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmongusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplayProfile(
                        currentUser.value ?: User(
                            null,
                            "",
                            "",
                            null,
                            null,
                            null,
                            null
                        ),
                        onPictureChanged = { uri ->
                            onPictureChanged(uri)
                        },
                        openGallery = ::openGallery
                    )
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
    private fun onPictureChanged(uri: Uri) {
        // Fonction appelée lorsque l'utilisateur change sa photo de profil
        // + ajouter ici la logique pour mettre à jour la photo de profil du backend

        val file = File(uri.path!!)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        // Appel de l'API pour uploader l'image
        val call = RetrofitInstance.api.uploadPictureProfile(body)
        call.enqueue(object : Callback<ImgurResponse> {
            override fun onResponse(call: Call<ImgurResponse>, response: Response<ImgurResponse>) {
                if (response.isSuccessful) {
                    val imgurData = response.body()?.data
                    val imgUrl = imgurData?.link
                    println(imgUrl)
                    // Utilisez imgUrl comme URL de l'image uploadée
                    // puis update user.picture
                } else {
                    // Gérer l'échec de l'upload
                }
            }

            override fun onFailure(call: Call<ImgurResponse>, t: Throwable) {
                // Gérer les erreurs de réseau ou autres
                println("Nope : ${t.message}")
            }
        })
    }
}

@Composable
fun DisplayProfile(
    user: User,
    modifier: Modifier = Modifier,
    onPictureChanged: (Uri) -> Unit,
    openGallery: () -> Unit){

    val newPictureUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Picture
            if (!user.picture.isNullOrBlank()) {
                AsyncImage(
                    model = user.picture,
                    contentDescription = "User profile picture",
                    modifier = Modifier
                        .height(120.dp)
                        .width(120.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                )
            } else {
                // Load default image from resources
                Image(
                    painter = painterResource(id = R.drawable.anonymous),
                    contentDescription = "Default profile picture",
                    modifier = Modifier
                        .height(120.dp)
                        .width(120.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                )
            }

            IconButton(
                onClick = {
                    openGallery()
                }
            ) {
                Icon(Icons.Filled.Create, contentDescription = "Change profile picture")
            }

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





