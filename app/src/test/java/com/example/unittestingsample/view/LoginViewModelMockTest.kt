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
import org.mockito.junit.MockitoJUnitRunner
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import retrofit2.Response

/**
 * @author Niharika Arora
 * A Class covering LoginViewModel test cases
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(UtilityClass::class)
class LoginViewModelMockTest {
    //
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

    //
    private lateinit var loginViewModel: LoginViewModel

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()


    @Rule
    @JvmField
    val coRoutineTestRule = CoroutineTestRule()

    @Before
    fun before() {
        // Enable static mocking for all methods of a class.
        mockStatic(UtilityClass::class.java)

        // Initializing the class to be tested
        loginViewModel = LoginViewModel(serviceUtil).apply {
            getObserverState().observeForever(mockObserverForStates)
        }
    }

    //  The annotation tells JUnit that the <code>public void</code> method  to which it is attached can be run as a test case.
    @Test
    fun testIfEmailInvalid_ReportEmailError() {
        //Arrange
        `when`(UtilityClass.isEmailValid(anyString())).thenAnswer { false }

        //Act
        loginViewModel.doLogin(EMAIL, PASSWORD)

        //Assert
        verify(mockObserverForStates).onChanged(LoginDataState.InValidEmailState)
        verifyNoMoreInteractions(mockObserverForStates)
    }


    @Test
    fun testIPasswordInvalid_ReportPasswordError() {
        //Arrange
        `when`(UtilityClass.isEmailValid(anyString())).thenAnswer { true }
        `when`(UtilityClass.isPasswordValid(anyString())).thenAnswer { false }

        //Act
        loginViewModel.doLogin(EMAIL, PASSWORD)

        //Assert
        verify(mockObserverForStates).onChanged(LoginDataState.InValidPasswordState)
        verifyNoMoreInteractions(mockObserverForStates)
    }


    @ExperimentalCoroutinesApi
    @Test
    fun testIfEmailAndPasswordValid_DoLogin() {
        //Arrange
        `when`(UtilityClass.isEmailValid(anyString())).thenAnswer { true }
        `when`(UtilityClass.isPasswordValid(anyString())).thenAnswer { true }

        runBlockingTest {
            `when`(serviceUtil.authenticate(ArgumentMatchers.anyMap<String, String>() as HashMap<String, String>)).thenReturn(
                Response.success(loginModel)
            )
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
        `when`(UtilityClass.isEmailValid(anyString())).thenAnswer { true }
        `when`(UtilityClass.isPasswordValid(anyString())).thenAnswer { true }

        val error = RuntimeException()
        runBlocking {
            `when`(serviceUtil.authenticate(ArgumentMatchers.anyMap<String, String>() as HashMap<String, String>)).thenThrow(
                error
            )
        }

        //Act
        loginViewModel.doLogin(EMAIL, PASSWORD)

        //Assert
        verify(mockObserverForStates).onChanged(LoginDataState.ValidCredentialsState)
        verify(mockObserverForStates, times(2))
            .onChanged(LoginDataState.Error(ArgumentMatchers.any()))
        verifyNoMoreInteractions(mockObserverForStates)
    }

    //A helper function to mock classes with types (generics)
    private inline fun <reified T> mock(): T = mock(T::class.java)

    @After
    @Throws(Exception::class)
    fun tearDownClass() {
        framework().clearInlineMocks()
    }
}