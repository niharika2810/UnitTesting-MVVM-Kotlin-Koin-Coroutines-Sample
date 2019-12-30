package com.example.unittestingsample.activities.main.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * author Niharika Arora
 */
@Serializable
data class LoginModel(@SerialName(value = "data") val data: LoginData) {

    @Serializable
    data class LoginData(@SerialName(value = "uid") val userId: String, @SerialName(value = "email") val email: String)
}