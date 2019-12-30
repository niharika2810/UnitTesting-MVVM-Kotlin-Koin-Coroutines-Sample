package com.example.unittestingsample.activities.main.viewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unittestingsample.R
import com.example.unittestingsample.activities.main.data.Item
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_item.view.*

/**
 * author Niharika Arora
 */

class ItemAdapterViewHolder constructor(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(item: Item) {
        containerView.item_description.text = item.description
    }

    companion object {
        fun create(
            parent: ViewGroup
        ): ItemAdapterViewHolder {
            return ItemAdapterViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.layout_item,
                    parent,
                    false
                )
            )
        }
    }
}
