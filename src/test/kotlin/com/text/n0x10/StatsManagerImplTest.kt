package com.text.n0x10

import org.junit.Assert.assertEquals
import org.junit.Test

class StatsManagerImplTest {
    val emptyStats = Stats(0.0, Double.NaN, Double.NaN, Double.NaN, 0)
                                         ;
    @Test
    fun `should calculate correct stat`() {
        val clock = TestClock()
        val statsManager = StatsManagerImpl(clock, windowSize = 10)

        clock.time = 0
        assertEquals(emptyStats, statsManager.get())

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
        assertEquals(emptyStats, statsManager.get())

        clock.time = 100
        assertEquals(emptyStats, statsManager.get())
    }

    @Test
    fun `should handle out of order transactions`() {
        val clock = TestClock()
        val statsManager = StatsManagerImpl(clock, windowSize = 60)

        clock.time = 10
        statsManager.add(Transaction(1.0, 10))

        clock.time = 11
        statsManager.add(Transaction(-3.3, 9))
        statsManager.add(Transaction(6.3, 10))
        statsManager.add(Transaction(0.3, 10))
        statsManager.add(Transaction(4.1, 8))
        assertEquals(Stats(8.4, 1.68, -3.3, 6.3, 5), statsManager.get())

        clock.time = 12
        statsManager.add(Transaction(9.6, 10))
        assertEquals(Stats(18.0, 3.0, -3.3, 9.6, 6), statsManager.get())

        clock.time = 70
        assertEquals(emptyStats, statsManager.get())

        statsManager.add(Transaction(4.0, 10))
        statsManager.add(Transaction(100.0, 0))
        statsManager.add(Transaction(3.0, 9))
        assertEquals(emptyStats, statsManager.get())

        statsManager.add(Transaction(5.0, 65))
        assertEquals(Stats(5.0, 5.0, 5.0, 5.0, 1), statsManager.get())

        statsManager.add(Transaction(10.0, 55))
        assertEquals(Stats(15.0, 7.5, 5.0, 10.0, 2), statsManager.get())

        statsManager.add(Transaction(30.0, 50))
        assertEquals(Stats(45.0, 15.0, 5.0, 30.0, 3), statsManager.get())
    }

    fun assertEquals(s1: Stats, s2: Stats) {
        val delta = 1e-10
        assertEquals(s1.sum, s2.sum, delta)
        assertEquals(s1.avg, s2.avg, delta)
        assertEquals(s1.min, s2.min, delta)
        assertEquals(s1.max, s2.max, delta)
        assertEquals(s1.count, s2.count)
    }
}