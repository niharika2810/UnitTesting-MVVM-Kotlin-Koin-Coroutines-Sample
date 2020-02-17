package com.example.unittestingsample.util

import com.example.unittestingsample.activities.main.data.LoginModel
import retrofit2.Response

/**
 * @author Niharika.Arora
 */
sealed class LoginDataState {
    data class Error(val message: String?) : LoginDataState()
    object ValidCredentialsState : LoginDataState()
    data class InValidEmailState(val message: String?) : LoginDataState()
    data class InValidPasswordState(val message: String?) : LoginDataState()
    data class Success(val body: Response<LoginModel>? = null) : LoginDataState()
}