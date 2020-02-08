package com.example.unittestingsample.util

import com.example.unittestingsample.activities.main.data.Item

/**
 * @author Niharika.Arora
 */
sealed class ItemDataState {
    data class ShowProgress(val showProgress: Boolean) : ItemDataState()
    data class Error(val message: String?) : ItemDataState()
    data class Success(val body: List<Item>? = null) : ItemDataState()
}