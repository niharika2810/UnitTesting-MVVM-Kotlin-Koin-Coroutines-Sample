package com.example.unittestingsample.backend

import retrofit2.Response
import retrofit2.http.*
import com.example.unittestingsample.activities.main.data.Item
import com.example.unittestingsample.activities.main.data.LoginModel


/**
 * author Niharika Arora
 */

interface ServiceUtil {

    @GET(value = "todo_items")
    suspend fun getList(@HeaderMap headers: HashMap<String, String>): List<Item>

    @Headers("Content-Type:application/json")
    @POST(value = "auth/sign_in")
    suspend fun authenticate(
        @Body body: HashMap<String, String>
    ): Response<LoginModel>
}