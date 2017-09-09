package com.github.mkulak.n0x1A.mocks

import com.github.mkulak.n0x1A.*
import java.util.concurrent.atomic.AtomicReference


class MockStatsManager(val response: Stats) : StatsManager {
    val received = AtomicReference<Transaction>()

    override fun add(transaction: Transaction) {
        received.set(transaction)
    }

    override fun getStats(): Stats = response
}