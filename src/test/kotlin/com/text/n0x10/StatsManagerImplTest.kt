package com.text.n0x10

import org.junit.*

import org.junit.Assert.*

class StatsManagerImplTest {
    @Test
    fun `should calculate correct stat`() {
        val clock = TestClock(0)
        val windowSize: Long = 10

        val statsManager = StatsManagerImpl(clock, windowSize)
        assertEquals(Stats(0.0, 0.0, 0.0, 0.0, 0), statsManager.getStats())

        statsManager.addTransaction(Transaction(1.0, 0))
        assertEquals(Stats(1.0, 1.0, 1.0, 1.0, 1), statsManager.getStats())

        clock.time = 1
        statsManager.addTransaction(Transaction(2.0, 1))
        assertEquals(Stats(3.0, 1.5, 1.0, 2.0, 2), statsManager.getStats())

        clock.time = 9
        statsManager.addTransaction(Transaction(3.0, 9))
        assertEquals(Stats(6.0, 2.0, 1.0, 3.0, 3), statsManager.getStats())

        clock.time = 10
        statsManager.addTransaction(Transaction(20.0, 10))
        assertEquals(Stats(25.0, 12.5, 2.0, 20.0, 3), statsManager.getStats())
    }
}