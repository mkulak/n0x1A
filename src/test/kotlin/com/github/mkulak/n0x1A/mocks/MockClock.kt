package com.github.mkulak.n0x1A.mocks

import com.github.mkulak.n0x1A.Clock


class MockClock(var time: Long = 0) : Clock {
    override fun now(): Long = time
}