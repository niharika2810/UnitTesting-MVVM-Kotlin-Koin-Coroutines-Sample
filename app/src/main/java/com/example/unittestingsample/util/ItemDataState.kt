package com.example.unittestingsample.util

import com.example.unittestingsample.activities.main.data.Item

/**
 * @author Niharika.Arora
 */
sealed class ItemDataState {
    object ShowProgress : ItemDataState()
    data class Error(val message: String?) : ItemDataState()
    data class Success(val body: List<Item>? = null) : ItemDataState()
}