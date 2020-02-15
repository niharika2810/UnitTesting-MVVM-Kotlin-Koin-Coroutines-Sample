package com.example.unittestingsample.view

import android.text.TextUtils
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.unittestingsample.CoroutineTestRule
import com.example.unittestingsample.activities.main.data.LoginModel
import com.example.unittestingsample.activities.main.viewModel.LoginViewModel
import com.example.unittestingsample.backend.ServiceUtil
import com.example.unittestingsample.util.LoginDataState
import com.example.unittestingsample.util.UtilityClass
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import retrofit2.Response

/**
 * author Niharika Arora
 */
@ExperimentalCoroutinesApi
@RunWith(PowerMockRunner::class)
@PrepareForTest(UtilityClass::class, TextUtils::class)
class LoginViewModelMockTest {
    private val serviceUtil: ServiceUtil = mock()

    lateinit var loginViewModel: LoginViewModel

    @Mock
    private lateinit var loginModel: LoginModel

    @Mock
    private lateinit var loginData: LoginModel.LoginData

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val coRoutineTestRule = CoroutineTestRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val mockObserverForStates = mock<Observer<LoginDataState>>()

    @Mock
    private lateinit var map: HashMap<String, String>

    @Before
    fun before() {
        mockStatic(UtilityClass::class.java)
        mockStatic(TextUtils::class.java)
        MockitoAnnotations.initMocks(this)
        loginData = LoginModel.LoginData("User-Id", "Email")
        loginModel = LoginModel(loginData)
        loginViewModel = LoginViewModel(serviceUtil).apply {
            uiState.observeForever(mockObserverForStates)
        }
    }

    @Test
    fun testIfEmailInvalidAndReport() {
        initValues(false, false)

        runBlockingTest {
            `when`(serviceUtil.authenticate(map)).thenReturn(Response.success(loginModel))

            loginViewModel.doLogin("", "")

            verify(mockObserverForStates).onChanged(
                LoginDataState.InValidEmailState(
                    ArgumentMatchers.any()
                )
            )
            verifyNoMoreInteractions(mockObserverForStates)
        }
    }

    @Test
    fun testIPasswordInvalidAndReport() {
        initValues(true, false)

        runBlockingTest {
            `when`(serviceUtil.authenticate(map)).thenReturn(Response.success(loginModel))

            loginViewModel.doLogin("abc@example.com", "123")

            verify(mockObserverForStates).onChanged(LoginDataState.ValidEmailState)
            verify(mockObserverForStates, times(2)).onChanged(
                LoginDataState.InValidPasswordState(
                    ArgumentMatchers.any()
                )
            )
            verifyNoMoreInteractions(mockObserverForStates)
        }
    }

    @Test
    fun testIfEmailAndPasswordValidDoLogin() {
        initValues(true, true)

        runBlockingTest {
            `when`(serviceUtil.authenticate(map)).thenReturn(Response.success(loginModel))

            loginViewModel.doLogin("abc@example.com", "12345678")

            verify(mockObserverForStates).onChanged(LoginDataState.ShowProgress(true))
            verify(mockObserverForStates).onChanged(LoginDataState.ValidEmailState)
            verify(mockObserverForStates).onChanged(LoginDataState.ValidPasswordState)
            verify(mockObserverForStates, times(4)).onChanged(
                LoginDataState.Success(
                    ArgumentMatchers.any()
                )
            )
            verifyNoMoreInteractions(mockObserverForStates)
        }
    }

    @Test
    fun testThrowErrorOnLoginFailed() {
        initValues(true, true)

        runBlocking {
            val error = RuntimeException()

            `when`(serviceUtil.authenticate(map)).thenThrow(error)

            loginViewModel.doLogin("abc@example.com", "12345678")

            verify(mockObserverForStates).onChanged(LoginDataState.ShowProgress(true))
            verify(mockObserverForStates).onChanged(LoginDataState.ValidEmailState)
            verify(mockObserverForStates).onChanged(LoginDataState.ValidPasswordState)
            verify(
                mockObserverForStates,
                times(4)
            ).onChanged(LoginDataState.Error(ArgumentMatchers.any()))
            verifyNoMoreInteractions(mockObserverForStates)
        }
    }

    private fun initValues(
        isEmailValid: Boolean,
        isPasswordValid: Boolean
    ) {
        `when`(UtilityClass.isEmailValid(ArgumentMatchers.anyString())).thenReturn(isEmailValid)
        `when`(UtilityClass.isPasswordValid(ArgumentMatchers.anyString())).thenReturn(
            isPasswordValid
        )
    }

    //A helper function to mock classes with types (generics)
    private inline fun <reified T> mock(): T = mock(T::class.java)

}