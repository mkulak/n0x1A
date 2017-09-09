package com.text.n0x10


data class Transaction(val amount: Double, val timestamp: Long)

data class Stats(val sum: Double, val avg : Double, val min: Double, val max: Double, val count: Int)

interface StatsManager {
    fun add(transaction: Transaction)
    fun getStats(): Stats
}

class StatsManagerImpl(val clock: Clock, val windowSizeSeconds: Int) : StatsManager {
    private val MILLIS_IN_SECOND = 1000
    private val emptyCell = Cell(Double.MAX_VALUE, Double.MIN_VALUE, 0.0, 0)
    private val emptyStats = Stats(0.0, Double.NaN, Double.NaN, Double.NaN, 0)

    private val data = Array<Cell?>(windowSizeSeconds) { null }
    private var endIndex = 0
    private var lastSecond = clock.now() / MILLIS_IN_SECOND

    override fun add(transaction: Transaction) {
        val newSecond = transaction.timestamp / MILLIS_IN_SECOND
        observeNewTime(newSecond)
        val timeDiff = (newSecond - lastSecond).coerceIn(-windowSizeSeconds.toLong(), 0).toInt()
        if (timeDiff == -windowSizeSeconds) return
        add(endIndex + timeDiff, transaction)
    }

    override fun getStats(): Stats {
        observeNewTime(clock.now() / MILLIS_IN_SECOND)
        var sum = 0.0
        var count = 0
        var min = Double.MAX_VALUE
        var max = Double.MIN_VALUE
        repeat(windowSizeSeconds) {
            val c = data[it]
            if (c != null) {
                sum += c.sum
                min = Math.min(min, c.min)
                max = Math.max(max, c.max)
                count += c.count
            }
        }
        return if (count > 0) Stats(sum, sum / count, min, max, count) else emptyStats
    }

    private fun observeNewTime(newSecond: Long) {
        val timeDiff = (newSecond - lastSecond).coerceIn(0, windowSizeSeconds.toLong()).toInt()
        repeat(timeDiff) {
            val index = (endIndex + it + 1) modulo windowSizeSeconds
            data[index] = null
        }
        endIndex += timeDiff
        lastSecond = Math.max(lastSecond, newSecond)
    }

    private fun add(index: Int, transaction: Transaction) {
        val realIndex = index modulo windowSizeSeconds
        val current = data[realIndex] ?: emptyCell
        data[realIndex] = add(current, transaction.amount)
    }

    private fun add(cell: Cell, amount: Double): Cell =
            Cell(Math.min(cell.min, amount), Math.max(cell.max, amount), cell.sum + amount, cell.count + 1)

    private infix fun Int.modulo(m: Int): Int = this.rem(m).let { if (it < 0) it + m else it }

    private data class Cell(val min: Double, val max: Double, val sum: Double, val count: Int)
}

class DumbStatsManager(val clock: Clock, val windowSize: Long) : StatsManager {
    val transactions = ArrayList<Transaction>()

    override fun add(transaction: Transaction) {
        transactions += transaction
    }

    override fun getStats(): Stats {
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
