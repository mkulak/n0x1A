package com.github.mkulak.n0x1A


class TestClock(var time: Long = 0) : Clock {
    override fun now(): Long = time
}