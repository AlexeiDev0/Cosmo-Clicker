package com.example.myapplication

/** Serializes complete-state writes and captures the latest state only after acquiring the lock. */
internal class StateSaveCoordinator<T> {
    private val lock = Any()

    fun save(currentState: () -> T, write: (T) -> Unit) {
        synchronized(lock) {
            write(currentState())
        }
    }
}
