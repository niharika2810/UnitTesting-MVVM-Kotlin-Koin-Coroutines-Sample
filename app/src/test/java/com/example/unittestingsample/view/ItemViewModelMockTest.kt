package org.koin.sampleapp.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.unittestingsample.CoroutineTestRule
import com.example.unittestingsample.activities.main.data.Headers
import com.example.unittestingsample.activities.main.data.Item
import com.example.unittestingsample.activities.main.viewModel.ItemViewModel
import com.example.unittestingsample.backend.ServiceUtil
import com.example.unittestingsample.util.ItemDataState
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
import org.powermock.modules.junit4.PowerMockRunner

/**
 * author Niharika Arora
 */
@ExperimentalCoroutinesApi
@RunWith(PowerMockRunner::class)
class ItemViewModelMockTest {
    private val serviceUtil: ServiceUtil = mock()

    private lateinit var itemViewModel: ItemViewModel

    @Mock
    private lateinit var items: ArrayList<Item>


    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val coRoutineTestRule = CoroutineTestRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val mockObserverForStates = mock<Observer<ItemDataState>>()

    @Mock
    private lateinit var headersMap: HashMap<String, String>

    @Mock
    private lateinit var headers: Headers

    @Before
    fun before() {
        itemViewModel = ItemViewModel(serviceUtil).apply {
            uiState.observeForever(mockObserverForStates)
        }
    }

    @Test
    fun testIfHeadersMissingAndReport() {
        initValues("ClientId", "", "")

        runBlockingTest {
            `when`(serviceUtil.getList(headersMap)).thenReturn(items)

            itemViewModel.showList(headers)

            verify(mockObserverForStates).onChanged(ItemDataState.Error(ArgumentMatchers.any()))
            verifyNoMoreInteractions(mockObserverForStates)
        }
    }

    @Test
    fun testFetchListFromServer() {
        initValues("ClientId", "AccessToken", "UserId")

        runBlockingTest {
            `when`(serviceUtil.getList(headersMap)).thenReturn(items)

            itemViewModel.showList(headers)

            verify(mockObserverForStates).onChanged(ItemDataState.ShowProgress)
            verify(mockObserverForStates, times(2)).onChanged(
                ItemDataState.Success(ArgumentMatchers.any())
            )
            verifyNoMoreInteractions(mockObserverForStates)
        }
    }

    @Test
    fun testThrowErrorOnListCallbackFailed() {
        initValues("ClientId", "AccessToken", "UserId")

        runBlocking {
            val error = RuntimeException()

            `when`(serviceUtil.getList(headersMap)).thenThrow(error)

            itemViewModel.showList(headers)

            verify(mockObserverForStates).onChanged(ItemDataState.ShowProgress)
            verify(
                mockObserverForStates,
                times(2)
            ).onChanged(ItemDataState.Error(ArgumentMatchers.any()))
            verifyNoMoreInteractions(mockObserverForStates)
        }
    }

    private fun initValues(clientId: String, accessToken: String, userId: String) {
        `when`(headers.clientId).thenReturn(clientId)
        `when`(headers.accessToken).thenReturn(accessToken)
        `when`(headers.userId).thenReturn(userId)
    }

    private inline fun <reified T> mock(): T = mock(T::class.java)

}