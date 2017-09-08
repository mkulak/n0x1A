package com.text.n0x10

import org.junit.*

import org.junit.Assert.*

class StatsManagerImplTest {
    val delta = 1e-7
                                         ;
    @Test
    fun `should be able to return empty stat`() {
        val statsManager = StatsManagerImpl(TestClock(0), 10)
        statsManager.getStats().apply {
            assertEquals(0, count)
            assertEquals(0.0, sum, delta)
            assertTrue(avg.isNaN())
            assertTrue(max.isNaN())
            assertTrue(min.isNaN())
        }
    }

    @Test
    fun `should calculate correct stat`() {
        val clock = TestClock(0)
        val windowSize: Long = 10
        val statsManager = StatsManagerImpl(clock, windowSize)

        statsManager.addTransaction(Transaction(1.0, 0))
        assertEquals(Stats(1.0, 1.0, 1.0, 1.0, 1), statsManager.getStats())

        clock.time = 1
        statsManager.addTransaction(Transaction(2.0, 1))
        assertEquals(Stats(3.0, 1.5, 1.0, 2.0, 2), statsManager.getStats())

        clock.time = 9
        statsManager.addTransaction(Transaction(3.0, 9))
        assertEquals(Stats(6.0, 2.0, 1.0, 3.0, 3), statsManager.getStats())

        clock.time = 10
        statsManager.addTransaction(Transaction(19.0, 10))
        assertEquals(Stats(24.0, 8.0, 2.0, 19.0, 3), statsManager.getStats())
    }
}