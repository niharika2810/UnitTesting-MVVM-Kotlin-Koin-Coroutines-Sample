package com.example.unittestingsample.activities.main.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.Headers
import com.example.unittestingsample.backend.ServiceUtil
import com.example.unittestingsample.util.Constants
import com.example.unittestingsample.util.Event

/**
 * author Niharika Arora
 */

class LoginViewModel constructor(
    private val serviceUtil: ServiceUtil,
    private val inputName: String, private val password: String
) : ViewModel() {
    private lateinit var map: HashMap<String, String>
    private val _uiState = MutableLiveData<LoginDataState>()
    val uiState: LiveData<LoginDataState> get() = _uiState

    fun doLogin() {
        if (inputName.isNotEmpty() && password.isNotEmpty()) {
            viewModelScope.launch {
                runCatching {
                    emitUiState(showProgress = true)
                    map = HashMap()
                    map[Constants.USERNAME] = inputName
                    map[Constants.PASSWORD] = password
                    serviceUtil.authenticate(map)
                }.onSuccess {
                    emitUiState(headers = Event(it.headers()))
                }.onFailure {
                    it.printStackTrace()
                    emitUiState(error = Event(it.message))
                }
            }
        } else {
            emitUiState(error = Event("Please enter the valid details"))
        }
    }


    private fun emitUiState(
        showProgress: Boolean = false,
        headers: Event<Headers>? = null,
        error: Event<String?>? = null
    ) {
        val dataState =
            LoginDataState(
                showProgress,
                headers,
                error
            )
        _uiState.value = dataState
    }
}

data class LoginDataState(
    val showProgress: Boolean,
    val loginHeaders: Event<Headers>?,
    val error: Event<String?>?
)