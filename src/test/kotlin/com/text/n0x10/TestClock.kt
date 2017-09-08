package com.text.n0x10


class TestClock(var time: Long) : Clock {
    override fun now(): Long = time
}