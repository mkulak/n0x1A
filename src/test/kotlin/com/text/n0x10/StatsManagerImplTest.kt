package com.text.n0x10

import org.junit.Assert.assertEquals
import org.junit.Test

class StatsManagerImplTest {
    val delta = 1e-7
                                         ;
    @Test
    fun `should calculate correct stat`() {
        val clock = TestClock(0)
        val statsManager = StatsManagerImpl(clock, windowSize = 10)

        assertEquals(Stats(0.0, Double.NaN, Double.NaN, Double.NaN, 0), statsManager.get())

        statsManager.add(Transaction(1.0, 0))
        assertEquals(Stats(1.0, 1.0, 1.0, 1.0, 1), statsManager.get())

        clock.time = 1
        statsManager.add(Transaction(2.0, 1))
        assertEquals(Stats(3.0, 1.5, 1.0, 2.0, 2), statsManager.get())

        clock.time = 9
        statsManager.add(Transaction(3.0, 9))
        assertEquals(Stats(6.0, 2.0, 1.0, 3.0, 3), statsManager.get())

        clock.time = 10
        statsManager.add(Transaction(19.0, 10))
        assertEquals(Stats(24.0, 8.0, 2.0, 19.0, 3), statsManager.get())

        clock.time = 12
        assertEquals(Stats(22.0, 11.0, 3.0, 19.0, 2), statsManager.get())

        clock.time = 19
        assertEquals(Stats(19.0, 19.0, 19.0, 19.0, 1), statsManager.get())
        
        clock.time = 20
        assertEquals(Stats(0.0, Double.NaN, Double.NaN, Double.NaN, 0), statsManager.get())
    }
}