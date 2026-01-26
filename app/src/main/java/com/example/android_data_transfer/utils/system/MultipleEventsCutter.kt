package com.example.android_data_transfer.utils.system

interface MultipleEventsCutter {
    fun processEvent(event: () -> Unit)

    companion object
}

fun MultipleEventsCutter.Companion.get(): MultipleEventsCutter =
    MultipleEventsCutterImpl()

class MultipleEventsCutterImpl : MultipleEventsCutter {
    private val now: Long
        get() = System.currentTimeMillis()

    private var lastEventTimeMs: Long = 0

    override fun processEvent(event: () -> Unit) {
        if (now - lastEventTimeMs >= 400L) {
            event.invoke()
        }
        lastEventTimeMs = now
    }
}