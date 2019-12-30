package com.example.unittestingsample.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unittestingsample.R
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.getViewModel
import com.example.unittestingsample.activities.main.adapter.ItemAdapter
import com.example.unittestingsample.activities.main.data.Headers
import com.example.unittestingsample.activities.main.viewModel.ItemViewModel
import com.example.unittestingsample.util.Constants
import com.example.unittestingsample.util.UserHeadersStore
import kotlinx.android.synthetic.main.activity_list.*

/**
 * author Niharika Arora
 */
class ListActivity : AppCompatActivity() {

    private lateinit var itemViewModel: ItemViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        val adapter = ItemAdapter()
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@ListActivity)
            this.adapter = adapter
        }

        setProperties()

        itemViewModel = getViewModel()
        itemViewModel.uiState.observe(this, Observer {
            val dataState = it ?: return@Observer
            progress_bar.visibility = if (dataState.showProgress) View.VISIBLE else View.GONE
            if (dataState.items != null && !dataState.items.consumed)
                dataState.items.consume()?.let { movies ->
                    adapter.submitList(movies)
                }
            if (dataState.error != null && !dataState.error.consumed)
                dataState.error.consume()?.let { errorResource ->
                    Toast.makeText(this, errorResource, Toast.LENGTH_SHORT)
                        .show()
                }
        })

    }

    private fun setProperties() {
        val headers = Headers()
        headers.clientId = UserHeadersStore.getClientId()
        headers.userId = UserHeadersStore.getUserId()
        headers.accessToken = UserHeadersStore.getAccessToken()
        getKoin().setProperty(Constants.HEADERS, headers)
    }
}
