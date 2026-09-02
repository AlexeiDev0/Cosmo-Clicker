package com.example.myapplication

import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import org.junit.Assert.assertEquals
import org.junit.Test

class StateSaveCoordinatorTest {
    @Test
    fun queuedSaveCapturesLatestStateAfterPreviousWriteCompletes() {
        val coordinator = StateSaveCoordinator<Int>()
        val current = AtomicInteger(1)
        val firstWriteStarted = CountDownLatch(1)
        val releaseFirstWrite = CountDownLatch(1)
        val writes = Collections.synchronizedList(mutableListOf<Int>())

        val first = thread {
            coordinator.save(current::get) { value ->
                firstWriteStarted.countDown()
                releaseFirstWrite.await()
                writes += value
            }
        }
        firstWriteStarted.await()
        current.set(2)
        val second = thread { coordinator.save(current::get) { writes += it } }
        releaseFirstWrite.countDown()
        first.join()
        second.join()

        assertEquals(listOf(1, 2), writes)
    }
}
