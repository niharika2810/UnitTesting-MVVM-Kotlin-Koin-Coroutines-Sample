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
import com.example.unittestingsample.util.LoginDataState
import com.example.unittestingsample.util.UserHeadersStore
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Headers
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * author Niharika Arora
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = getViewModel()
        observeStates()

        setButtonClick()
    }

    private fun setButtonClick() {

        btn_login.setOnClickListener {
            hideKeyboard(btn_login)

            loginViewModel.doLogin(input_name.text.toString(), input_password.text.toString())
        }
    }

    private fun observeStates() = observeAuthenticationState()

    private fun observeAuthenticationState() {
        loginViewModel.getObserverState().observe(this, authenticationObserver)
    }


    private val authenticationObserver = Observer<LoginDataState> { dataState ->
        when (dataState) {
            is LoginDataState.Success -> {
                progress_bar_login.visibility = View.GONE
                if (dataState.body?.headers() != null && dataState.body.headers().size > 0) {
                    saveHeaders(dataState.body.headers())
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    showItemsScreen()
                }
            }
            is LoginDataState.Error -> {
                progress_bar_login.visibility = View.GONE
                Toast.makeText(this, dataState.message, Toast.LENGTH_SHORT).show()
            }
            is LoginDataState.ValidCredentialsState -> {
                progress_bar_login.visibility = View.VISIBLE
                resetEmailError()
                resetPasswordError()
            }
            is LoginDataState.InValidEmailState -> {
                setEmailError()

            }
            is LoginDataState.InValidPasswordState -> {
                resetEmailError()
                setPasswordError()
            }
        }
    }

    private fun setEmailError() {
        text_input_name.error = "Please enter a valid email ID"
        text_input_name.isErrorEnabled = true
    }

    private fun setPasswordError() {
        text_input_password.error = "Please enter a valid password"
        text_input_password.isErrorEnabled = true
    }

    private fun resetEmailError() {
        text_input_name.error = null
        text_input_name.isErrorEnabled = false
    }

    private fun resetPasswordError() {
        text_input_password.error = null
        text_input_password.isErrorEnabled = false
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
