package com.example.unittestingsample.view

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
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import retrofit2.Response

/**
 * @author Niharika Arora
 * A Class covering LoginViewModel test cases
 */
@ExperimentalCoroutinesApi
@RunWith(PowerMockRunner::class)
@PrepareForTest(UtilityClass::class)
class LoginViewModelMockTest {

    companion object {
        private const val EMAIL = "abc@example.com"
        private const val PASSWORD = "123456"

        @BeforeClass
        @JvmStatic
        fun setup() {
            println("startup")
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            println("teardown")
        }
    }

    @Mock
    private lateinit var loginModel: LoginModel

    @Mock
    private lateinit var serviceUtil: ServiceUtil

    private val mockObserverForStates = mock<Observer<LoginDataState>>()

    private lateinit var loginViewModel: LoginViewModel

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val coRoutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var map: HashMap<String, String>

    @Before
    fun before() {
        mockStatic(UtilityClass::class.java)
        loginViewModel = LoginViewModel(serviceUtil).apply {
            getObserverState().observeForever(mockObserverForStates)
        }
    }

    @Test
    fun testIfEmailInvalid_ReportEmailError() {
        //Arrange
        initValues(isEmailValid = false, isPasswordValid = false)

        //Act
        loginViewModel.doLogin(EMAIL, PASSWORD)

        //Assert
        verify(mockObserverForStates).onChanged(LoginDataState.InValidEmailState(ArgumentMatchers.any()))
        verifyNoMoreInteractions(mockObserverForStates)
    }

    @Test
    fun testIPasswordInvalid_ReportPasswordError() {
        //Arrange
        initValues(isEmailValid = true, isPasswordValid = false)

        //Act
        loginViewModel.doLogin(EMAIL, PASSWORD)

        //Assert
        verify(mockObserverForStates).onChanged(LoginDataState.InValidPasswordState(ArgumentMatchers.any()))
        verifyNoMoreInteractions(mockObserverForStates)
    }

    @Test
    fun testIfEmailAndPasswordValid_DoLogin() {
        //Arrange
        initValues(isEmailValid = true, isPasswordValid = true)
        runBlockingTest {
            `when`(serviceUtil.authenticate(ArgumentMatchers.anyMap<String, String>() as HashMap<String, String>)).thenReturn(Response.success(loginModel))
        }

        //Act
        loginViewModel.doLogin(EMAIL, PASSWORD)

        //Assert
        verify(mockObserverForStates).onChanged(LoginDataState.ValidCredentialsState)
        verify(
            mockObserverForStates,
            times(2)
        ).onChanged(LoginDataState.Success(ArgumentMatchers.any()))
        verifyNoMoreInteractions(mockObserverForStates)
    }

    @Test
    fun testThrowError_OnLoginFailed() {
        //Arrange
        initValues(isEmailValid = true, isPasswordValid = true)
        val error = RuntimeException()
        runBlocking {
            `when`(serviceUtil.authenticate(map)).thenThrow(error)
        }

        //Act
        loginViewModel.doLogin(EMAIL, PASSWORD)

        //Assert
        verify(mockObserverForStates).onChanged(LoginDataState.ValidCredentialsState)
        verify(mockObserverForStates, times(2))
            .onChanged(LoginDataState.Error(ArgumentMatchers.any()))
        verifyNoMoreInteractions(mockObserverForStates)
    }

    //A function stubbing values needed
    private fun initValues(
        isEmailValid: Boolean,
        isPasswordValid: Boolean
    ) {
        `when`(UtilityClass.isEmailValid(anyString())).thenAnswer { isEmailValid }

        `when`(UtilityClass.isPasswordValid(anyString())).thenAnswer { isPasswordValid }

    }

    //A helper function to mock classes with types (generics)
    private inline fun <reified T> mock(): T = mock(T::class.java)

    @After
    @Throws(Exception::class)
    fun tearDownClass() {
        framework().clearInlineMocks()
    }
}