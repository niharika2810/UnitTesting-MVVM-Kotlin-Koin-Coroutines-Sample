package com.example.unittestingsample.activities.main.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * author Niharika Arora
 */

@Serializable
data class Item(
    @SerialName(value = "id") val id: Int, @SerialName(value = "description") val description: String
)