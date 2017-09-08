package com.text.n0x10

import java.util.concurrent.atomic.AtomicReference


class TestStatsManager(val response: Stats) : StatsManager {
    val received = AtomicReference<Transaction>()

    override fun add(transaction: Transaction) {
        received.set(transaction)
    }

    override fun get(): Stats = response
}