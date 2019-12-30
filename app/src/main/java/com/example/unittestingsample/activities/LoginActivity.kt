package com.example.unittestingsample.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.unittestingsample.R
import com.example.unittestingsample.activities.main.viewModel.LoginViewModel
import com.example.unittestingsample.util.Constants
import com.example.unittestingsample.util.UserHeadersStore
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Headers
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * author Niharika Arora
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btn_login.setOnClickListener {
            hideKeyboard(btn_login)

            getKoin().setProperty(Constants.USERNAME, input_name.text.toString())
            getKoin().setProperty(Constants.PASSWORD, input_password.text.toString())
            loginViewModel = getViewModel()
            loginViewModel.doLogin()

            loginViewModel.uiState.observe(this, Observer {
                val dataState = it ?: return@Observer
                progress_bar_login.visibility =
                    if (dataState.showProgress) View.VISIBLE else View.GONE
                if (dataState.loginHeaders != null && !dataState.loginHeaders.consumed)
                    dataState.loginHeaders.consume()?.let { headers ->
                        saveHeaders(headers)
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        showItemsScreen()
                    }
                if (dataState.error != null && !dataState.error.consumed)
                    dataState.error.consume()?.let { errorResource ->
                        Toast.makeText(this, errorResource, Toast.LENGTH_SHORT).show()
                        // handle error state
                    }
            })
        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showItemsScreen() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveHeaders(headers: Headers) {
        UserHeadersStore.setClientId(headers[Constants.CLIENT])
        UserHeadersStore.setAccessToken(headers[Constants.ACCESS_TOKEN])
        UserHeadersStore.setUserId(headers[Constants.USER_ID])
    }
}
