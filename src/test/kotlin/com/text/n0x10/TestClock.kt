package com.text.n0x10


class TestClock(var time: Long = 0) : Clock {
    override fun now(): Long = time
}