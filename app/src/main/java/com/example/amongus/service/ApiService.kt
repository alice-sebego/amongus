package com.example.amongus.service

import com.example.amongus.model.File
import com.example.amongus.model.User
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("users/connected")
    fun findConnected(): Call<List<User>>

    @POST("users/register")
    fun register(@Body user : User): Call<User>

    @POST("users/login")
    fun login(@Body user : User): Call<User>

    @PUT("users/{userId}/disconnect")
    fun disconnect(@Path("userId") userId: String,@Body user: User): Call<User>

    @GET("users/{userId}")
    fun getUser(@Path("userId") userId: String): Call<User>

    @GET("users")
    fun getUsers(): Call<List<User>>

    @Multipart
    @POST("file/upload")
    fun uploadPictureProfile(@Part file: MultipartBody.Part): Call<File>


}