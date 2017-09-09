package com.github.mkulak.n0x1A


interface Clock {
    fun now(): Long
}

class SystemClock : Clock {
    override fun now(): Long = System.currentTimeMillis()
}