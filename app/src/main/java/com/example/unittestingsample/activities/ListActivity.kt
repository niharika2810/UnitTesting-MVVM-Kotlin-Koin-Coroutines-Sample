package com.example.unittestingsample.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unittestingsample.R
import org.koin.androidx.viewmodel.ext.android.getViewModel
import com.example.unittestingsample.activities.main.adapter.ItemAdapter
import com.example.unittestingsample.activities.main.data.Headers
import com.example.unittestingsample.activities.main.viewModel.ItemViewModel
import com.example.unittestingsample.util.ItemDataState
import com.example.unittestingsample.util.UserHeadersStore
import kotlinx.android.synthetic.main.activity_list.*

/**
 * author Niharika Arora
 */
class ListActivity : AppCompatActivity() {

    private lateinit var headers: Headers
    private lateinit var itemViewModel: ItemViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setHeaders()
        itemViewModel = getViewModel()
        itemViewModel.showList(headers)
        observeStates()

    }

    private fun observeStates() = observeListState()

    private fun observeListState() {
        itemViewModel.getObserverState().observe(this, itemObserver)
    }

    private val itemObserver = Observer<ItemDataState> { dataState ->
        when (dataState) {

            is ItemDataState.ShowProgress -> {
                progress_bar.visibility = View.VISIBLE
            }
            is ItemDataState.Success -> {
                val adapter = ItemAdapter()
                recycler_view.apply {
                    layoutManager = LinearLayoutManager(this@ListActivity)
                    this.adapter = adapter
                }

                progress_bar.visibility = View.GONE
                adapter.submitList(dataState.body)
            }
            is ItemDataState.Error -> {
                progress_bar.visibility = View.GONE
                Toast.makeText(this, dataState.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun setHeaders() {
        headers = Headers()
        headers.clientId = UserHeadersStore.getClientId()
        headers.userId = UserHeadersStore.getUserId()
        headers.accessToken = UserHeadersStore.getAccessToken()
    }
}
