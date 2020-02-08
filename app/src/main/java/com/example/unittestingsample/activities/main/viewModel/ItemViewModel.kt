package com.example.unittestingsample.activities.main.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unittestingsample.activities.main.data.Headers
import com.example.unittestingsample.activities.main.data.Item
import com.example.unittestingsample.backend.ServiceUtil
import com.example.unittestingsample.util.Constants
import com.example.unittestingsample.util.ItemDataState
import kotlinx.coroutines.launch

/**
 * author Niharika Arora
 */

class ItemViewModel(
    private val serviceUtil: ServiceUtil,
    private val headers: Headers
) :
    ViewModel() {

    val uiState = MutableLiveData<ItemDataState>()

    fun showList() {
        retrieveItems()
    }

    private fun retrieveItems() {
        if (headers.clientId.isNotEmpty() && headers.userId.isNotEmpty() && headers.accessToken.isNotEmpty()) {
            viewModelScope.launch {
                runCatching {
                    uiState.postValue(ItemDataState.ShowProgress(true))
                    fetchItems()
                }.onSuccess {
                    uiState.postValue(ItemDataState.Success(it))
                }.onFailure {
                    it.printStackTrace()
                    uiState.postValue(ItemDataState.Error(it.message))
                }
            }
        } else {
            uiState.postValue(ItemDataState.Error("Issues in Headers,please check"))
        }
    }

    private suspend fun fetchItems(): List<Item> {
        val map = HashMap<String, String>()
        map[Constants.CLIENT] = headers.clientId
        map[Constants.USER_ID] = headers.userId
        map[Constants.ACCESS_TOKEN] = headers.accessToken
        return serviceUtil.getList(map)
    }

    fun getObserverState() = uiState

}