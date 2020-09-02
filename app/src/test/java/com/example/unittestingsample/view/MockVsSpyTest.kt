package com.example.unittestingsample.view

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author Niharika.Arora
 * A class explaining the difference in Spy vs Mock
 */
@RunWith(MockitoJUnitRunner::class)
class MockVsSpyTest {

    @Mock
    private lateinit var mockList: ArrayList<String>

    @Spy
    private val spyList = ArrayList<String>()

    @Test
    fun testMockList() {
        //by default, calling the methods of mock object will do nothing
        mockList.add("test")
        Assert.assertNull(mockList[0])
    }

    @Test
    fun testSpyList() {
        //spy object will call the real method when not stub
        spyList.add("test")
        Assert.assertEquals("test", spyList[0])
    }

    @Test
    fun testMockWithStub() {
        //try stubbing a method
        val expected = "Mock 100"
        Mockito.`when`(mockList[100]).thenReturn(expected)
        Assert.assertEquals(expected, mockList[100])
    }

    @Test
    fun testSpyWithStub() {
        //stubbing a spy method will result the same as the mock object
        val expected = "Spy 100"
        //take note of using doReturn instead of when
        Mockito.doReturn(expected).`when`(spyList).get(100)
        Assert.assertEquals(expected, spyList.get(100))
    }
}