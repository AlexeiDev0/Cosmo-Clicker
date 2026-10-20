package com.example.myapplication

/** Serializes complete-state writes and captures the latest state only after acquiring the lock. */
internal class StateSaveCoordinator<T> {
    private val lock = Any()

    fun <R> save(currentState: () -> T, write: (T) -> R): R =
        synchronized(lock) { write(currentState()) }
}
