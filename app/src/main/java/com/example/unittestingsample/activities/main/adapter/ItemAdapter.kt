package com.example.unittestingsample.activities.main.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.unittestingsample.activities.main.data.Item
import com.example.unittestingsample.activities.main.viewHolder.ItemAdapterViewHolder

/**
 * author Niharika Arora
 */

class ItemAdapter() :
    ListAdapter<Item, ItemAdapterViewHolder>(
        DIFF_CALLBACK
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemAdapterViewHolder.create(parent)

    override fun onBindViewHolder(holderAdapter: ItemAdapterViewHolder, position: Int) {
        holderAdapter.bind(getItem(position))
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>() {

            override fun areItemsTheSame(
                oldItem: Item,
                newItem: Item
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Item,
                newItem: Item
            ) =
                oldItem == newItem
        }
    }
}