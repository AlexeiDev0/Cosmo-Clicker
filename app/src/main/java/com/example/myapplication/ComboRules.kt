package com.example.myapplication

object ComboRules {
    const val MAX_LEVEL = 50
    const val WINDOW_MILLIS = 350L

    fun nextLevel(current: Int, elapsedMillis: Long, hasPreviousClick: Boolean): Int =
        if (hasPreviousClick && elapsedMillis in 0 until WINDOW_MILLIS) {
            (current.coerceIn(0, MAX_LEVEL) + 1).coerceAtMost(MAX_LEVEL)
        } else 1
}
