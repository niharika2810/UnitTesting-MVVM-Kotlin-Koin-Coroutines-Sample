package com.example.unittestingsample.util

import com.example.unittestingsample.activities.main.data.LoginModel
import retrofit2.Response

/**
 * @author Niharika.Arora
 */
sealed class LoginDataState {
    data class ShowProgress(val showProgress: Boolean) : LoginDataState()
    data class Error(val message: String?) : LoginDataState()
    object ValidEmailState : LoginDataState()
    object ValidPasswordState : LoginDataState()
    data class InValidEmailState(val message: String?) : LoginDataState()
    data class InValidPasswordState(val message: String?) : LoginDataState()
    data class Success(val body: Response<LoginModel>?=null) : LoginDataState()
}