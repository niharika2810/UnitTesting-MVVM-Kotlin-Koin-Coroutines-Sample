package com.example.unittestingsample.activities.main.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.unittestingsample.activities.main.data.Headers
import com.example.unittestingsample.activities.main.data.Item
import com.example.unittestingsample.backend.ServiceUtil
import com.example.unittestingsample.util.Constants
import com.example.unittestingsample.util.Event

/**
 * author Niharika Arora
 */

class ItemViewModel(
    private val serviceUtil: ServiceUtil,
    private val headers: Headers
) :
    ViewModel() {


    private val _uiState = MutableLiveData<MovieDataState>()
    val uiState: LiveData<MovieDataState> get() = _uiState

    init {
        retrieveItems()
    }

    private fun retrieveItems() {
        if (headers.clientId.isNotEmpty() && headers.userId.isNotEmpty() && headers.accessToken.isNotEmpty()) {
            viewModelScope.launch {
                runCatching {
                    emitUiState(showProgress = true)
                    fetchItems()
                }.onSuccess {
                    emitUiState(items = Event(it))
                }.onFailure {
                    it.printStackTrace()
                    emitUiState(error = Event(it.message))
                }
            }
        } else {
            emitUiState(error = Event("Issues in Headers,please check"))
        }
    }

    private suspend fun fetchItems(): List<Item> {
        val map = HashMap<String, String>()
        map[Constants.CLIENT] = headers.clientId
        map[Constants.USER_ID] = headers.userId
        map[Constants.ACCESS_TOKEN] = headers.accessToken
        return serviceUtil.getList(map)
    }

    private fun emitUiState(
        showProgress: Boolean = false,
        items: Event<List<Item>>? = null,
        error: Event<String?>? = null
    ) {
        val dataState = MovieDataState(showProgress, items, error)
        _uiState.value = dataState
    }
}

data class MovieDataState(
    val showProgress: Boolean,
    val items: Event<List<Item>>?,
    val error: Event<String?>?
)