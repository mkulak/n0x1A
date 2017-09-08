package com.text.n0x10


data class Transaction(val amount: Double, val timestamp: Long)

data class Stats(val sum: Double, val avg : Double, val min: Double, val max: Double, val count: Int)

interface StatsManager {
    fun addTransaction(transaction: Transaction)
    fun getStats(): Stats
}

class StatsManagerImpl : StatsManager {
    override fun addTransaction(transaction: Transaction) {

    }

    override fun getStats(): Stats {
        return Stats(0.0, 0.0, 0.0, 0.0, 0)
    }
}
