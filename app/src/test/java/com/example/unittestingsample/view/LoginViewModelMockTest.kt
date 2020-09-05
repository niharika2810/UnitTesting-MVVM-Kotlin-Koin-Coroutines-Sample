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
 *
 * Test Index:
 *
 * 1. When email is invalid, show invalid email error
 *
 * 2. When password is invalid, show invalid password error
 *
 * 3. When email and password is valid, do login
 *
 * 4. When email and password is valid but server error show Error
 *
 */
//Add mock-maker-inline in your test->resources for Kotlin classes
//@ExperimentalCoroutinesApi
@RunWith(PowerMockRunner::class)
@PrepareForTest(UtilityClass::class)
class LoginViewModelMockTest {
    companion object {
        private const val EMAIL = "abc@example.com"
        private const val PASSWORD = "123456"

        //Method annotated with BeforeClass will be called once before all class tests execute and should be static
        @BeforeClass
        @JvmStatic
        fun setup() {
            println("Before Class")
        }

        //Method annotated with AfterClass will be called once after all class tests execute and should be static
        @AfterClass
        @JvmStatic
        fun teardown() {
            println("After Class")
        }
    }

    //Annotation for marking a field as Mock and here we mocked the Response class
    @Mock
    private lateinit var loginModel: LoginModel

    @Mock
    private lateinit var serviceUtil: ServiceUtil

    //
    //Creating mock for the observer
    private val mockObserverForStates = mock<Observer<LoginDataState>>()

    //    //A helper function to mock classes with types (generics)
    private inline fun <reified T> mock(): T = mock(T::class.java)

    //
    //Class to be tested
    private lateinit var loginViewModel: LoginViewModel

    //
//    //A JUnit Test Rule that swaps the background executor used by the Architecture Components with a
//    // different one which executes each task synchronously.
    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    //
//    //  This class is a unit test rule which overrides the default Dispatchers.Main dispatcher and replaces the default with our test dispatcher.
    @Rule
    @JvmField
    val coRoutineTestRule = CoroutineTestRule()

    //
//    //Method annotated with before will be executed before every test. You can put the initializations here.
    @Before
    fun before() {
        // Enable static mocking for all methods of a class.
        mockStatic(UtilityClass::class.java)

        // Initializing the class to be tested
        loginViewModel = LoginViewModel(serviceUtil)
        loginViewModel.getObserverState().observeForever(mockObserverForStates)
    }

    //
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

//        //Act
        loginViewModel.doLogin(EMAIL, PASSWORD)
//
//        //Assert
        verify(mockObserverForStates).onChanged(LoginDataState.InValidPasswordState)
        verifyNoMoreInteractions(mockObserverForStates)
    }


    @Test
    fun testIfEmailAndPasswordValid_DoLogin() {
// Arrange
        `when`(UtilityClass.isEmailValid(anyString())).thenAnswer { true }
        `when`(UtilityClass.isPasswordValid(anyString())).thenAnswer { true }

//        //TO test suspend functions in junit, we use **runBlockingTest**, for normal functions this is not needed.
        runBlockingTest {
            `when`(serviceUtil.authenticate(ArgumentMatchers.anyMap<String, String>() as HashMap<String, String>)).thenReturn(
                Response.success(loginModel)
            )
        }

//        //Act
        loginViewModel.doLogin(EMAIL, PASSWORD)
//
//        //Assert
        verify(mockObserverForStates).onChanged(LoginDataState.ValidCredentialsState)
        verify(
            mockObserverForStates, times(2)
        ).onChanged(LoginDataState.Success(ArgumentMatchers.any()))
        verifyNoMoreInteractions(mockObserverForStates)
    }

    @Test
    fun testThrowError_OnLoginFailed() {
        //Arrange
        `when`(UtilityClass.isEmailValid(anyString())).thenAnswer { true }
        `when`(UtilityClass.isPasswordValid(anyString())).thenAnswer { true }

        //You can assume any kind of exception here
        val error = RuntimeException()
        runBlocking {
            `when`(serviceUtil.authenticate(ArgumentMatchers.anyMap<String, String>() as HashMap<String, String>)).thenThrow(
                error
            )
        }
//
//        //Act
        loginViewModel.doLogin(EMAIL, PASSWORD)
//
//        //Assert
        verify(mockObserverForStates).onChanged(LoginDataState.ValidCredentialsState)
        verify(mockObserverForStates, times(2))
            .onChanged(LoginDataState.Error(ArgumentMatchers.any()))
        verifyNoMoreInteractions(mockObserverForStates)
    }

    //    //A method  annotated with test will run after every test, here I have cleared all inline mocks to prevent OOM and reset Observer
    @After
    @Throws(Exception::class)
    fun tearDownClass() {
        framework().clearInlineMocks()
        loginViewModel.getObserverState().removeObserver(mockObserverForStates)
    }
}