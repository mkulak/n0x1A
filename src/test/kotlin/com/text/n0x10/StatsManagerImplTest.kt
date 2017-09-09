package com.text.n0x10

import org.junit.Assert.assertEquals
import org.junit.Test

class StatsManagerImplTest {
    val emptyStats = Stats(0.0, Double.NaN, Double.NaN, Double.NaN, 0)
                                         ;
    @Test
    fun `should calculate correct stat`() {
        val clock = TestClock()
        val statsManager = StatsManagerImpl(clock, windowSizeSeconds = 10)

        clock.time = 0.seconds
        assertEquals(emptyStats, statsManager.getStats())

        statsManager.add(Transaction(1.0, 0.seconds))
        assertEquals(Stats(1.0, 1.0, 1.0, 1.0, 1), statsManager.getStats())

        clock.time = 1.seconds
        statsManager.add(Transaction(2.0, 1.seconds))
        assertEquals(Stats(3.0, 1.5, 1.0, 2.0, 2), statsManager.getStats())

        clock.time = 9.seconds
        statsManager.add(Transaction(3.0, 9.seconds))
        assertEquals(Stats(6.0, 2.0, 1.0, 3.0, 3), statsManager.getStats())

        clock.time = 10.seconds
        statsManager.add(Transaction(19.0, 10.seconds))
        assertEquals(Stats(24.0, 8.0, 2.0, 19.0, 3), statsManager.getStats())

        clock.time = 12.seconds
        assertEquals(Stats(22.0, 11.0, 3.0, 19.0, 2), statsManager.getStats())

        clock.time = 19.seconds
        assertEquals(Stats(19.0, 19.0, 19.0, 19.0, 1), statsManager.getStats())
        
        clock.time = 20.seconds
        assertEquals(emptyStats, statsManager.getStats())

        clock.time = 100.seconds
        assertEquals(emptyStats, statsManager.getStats())
    }

    @Test
    fun `should handle out of order transactions`() {
        val clock = TestClock()
        val statsManager = StatsManagerImpl(clock, windowSizeSeconds = 60)

        clock.time = 10.seconds
        statsManager.add(Transaction(1.0, 10.seconds))

        clock.time = 11.seconds
        statsManager.add(Transaction(-3.3, 9.seconds))
        statsManager.add(Transaction(6.3, 10.seconds))
        statsManager.add(Transaction(0.3, 10.seconds))
        statsManager.add(Transaction(4.1, 8.seconds))
        assertEquals(Stats(8.4, 1.68, -3.3, 6.3, 5), statsManager.getStats())

        clock.time = 12.seconds
        statsManager.add(Transaction(9.6, 10.seconds))
        assertEquals(Stats(18.0, 3.0, -3.3, 9.6, 6), statsManager.getStats())

        clock.time = 70.seconds
        assertEquals(emptyStats, statsManager.getStats())

        statsManager.add(Transaction(4.0, 10.seconds))
        statsManager.add(Transaction(100.0, 0.seconds))
        statsManager.add(Transaction(3.0, 9.seconds))
        assertEquals(emptyStats, statsManager.getStats())

        statsManager.add(Transaction(5.0, 65.seconds))
        assertEquals(Stats(5.0, 5.0, 5.0, 5.0, 1), statsManager.getStats())

        statsManager.add(Transaction(10.0, 55.seconds))
        assertEquals(Stats(15.0, 7.5, 5.0, 10.0, 2), statsManager.getStats())

        statsManager.add(Transaction(30.0, 50.seconds))
        assertEquals(Stats(45.0, 15.0, 5.0, 30.0, 3), statsManager.getStats())
    }

    fun assertEquals(expected: Stats, actual: Stats) {
        val delta = 1e-10
        try {
            assertEquals(expected.sum, actual.sum, delta)
            assertEquals(expected.avg, actual.avg, delta)
            assertEquals(expected.min, actual.min, delta)
            assertEquals(expected.max, actual.max, delta)
            assertEquals(expected.count, actual.count)
        } catch (e: AssertionError) {
            throw AssertionError("\nExpected :$expected\nActual   :$actual")
        }
    }
    
    val Int.seconds: Long get() = this * 1000L
}