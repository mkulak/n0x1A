package com.text.n0x10


data class Transaction(val amount: Double, val timestamp: Long)

data class Stats(val sum: Double, val avg : Double, val min: Double, val max: Double, val count: Int)

interface StatsManager {
    fun add(transaction: Transaction)
    fun get(): Stats
}

class StatsManagerImpl(val clock: Clock, val windowSize: Long) : StatsManager {
    val transactions = ArrayList<Transaction>()

    override fun add(transaction: Transaction) {
        synchronized(this) {
            transactions += transaction
        }
    }

    override fun get(): Stats {
        synchronized(this) {
            val now = clock.now()
            val actual = transactions.filter { it.timestamp + windowSize > now }.map { it.amount }
            val sum = actual.sum()
            val count = actual.size
            val min = actual.min() ?: Double.NaN
            val max = actual.max() ?: Double.NaN
            val avg = if (count != 0) sum / count else Double.NaN
            return Stats(sum, avg, min, max, count)
        }
    }
}
