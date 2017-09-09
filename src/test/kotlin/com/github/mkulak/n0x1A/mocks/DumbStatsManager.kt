package com.github.mkulak.n0x1A.mocks

import com.github.mkulak.n0x1A.*

class DumbStatsManager(val clock: Clock, val windowSizeMillis: Long) : StatsManager {
    val transactions = ArrayList<Transaction>()

    override fun add(transaction: Transaction) {
        transactions += transaction
    }

    override fun getStats(): Stats {
        val now = Math.max(clock.now(), transactions.map { it.timestamp }.max() ?: 0)
        val actual = transactions.filter { it.timestamp + windowSizeMillis > now }.map { it.amount }
        val sum = actual.sum()
        val count = actual.size
        val min = actual.min() ?: Double.NaN
        val max = actual.max() ?: Double.NaN
        val avg = if (count != 0) sum / count else Double.NaN
        return Stats(sum, avg, min, max, count)
    }
}