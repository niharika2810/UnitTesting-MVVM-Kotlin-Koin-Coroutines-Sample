package com.example.unittestingsample.activities.main.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unittestingsample.backend.ServiceUtil
import com.example.unittestingsample.util.Constants
import com.example.unittestingsample.util.LoginDataState
import com.example.unittestingsample.util.UtilityClass
import kotlinx.coroutines.launch

/**
 * author Niharika Arora
 */

class LoginViewModel constructor(
    private val serviceUtil: ServiceUtil
) : ViewModel() {
    private lateinit var map: HashMap<String, String>
    val uiState = MutableLiveData<LoginDataState>()

    fun doLogin(userEmail: String, password: String) {
        if (areUserCredentialsValid(userEmail, password)) {
            viewModelScope.launch {
                runCatching {
                    map = HashMap()
                    map[Constants.USERNAME] = userEmail
                    map[Constants.PASSWORD] = password
                    serviceUtil.authenticate(map)
                }.onSuccess {
                    uiState.postValue(LoginDataState.Success(it))
                }.onFailure {
                    it.printStackTrace()
                    uiState.postValue(LoginDataState.Error("Request Failed,Please try later."))
                }
            }
        }
    }


    private fun areUserCredentialsValid(userEmail: String, password: String): Boolean {
        if (!UtilityClass.isEmailValid(userEmail)) {
            uiState.postValue(LoginDataState.InValidEmailState("Please enter a valid email ID"))
            return false
        } else if (!UtilityClass.isPasswordValid(password)) {
            uiState.postValue(LoginDataState.InValidPasswordState("Please enter a valid length password"))
            return false
        } else {
            uiState.postValue(LoginDataState.ValidCredentialsState)
            return true
        }
    }

    fun getObserverState() = uiState

}