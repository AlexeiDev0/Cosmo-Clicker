package com.example.myapplication

/** Stable discovery IDs shared by spawning, saved progress, quests and the collection. */
object DebrisCatalog {
    const val TYPE_COUNT = 48
    val ids: IntRange = 1..TYPE_COUNT

    private val pools = mapOf(
        Rarity.COMMON to listOf(1, 2, 7, 8, 15, 16, 17, 18, 29, 30, 31, 32, 33, 34),
        Rarity.UNCOMMON to listOf(3, 9, 10, 19, 20, 21, 35, 36, 37, 38, 39),
        Rarity.RARE to listOf(4, 11, 12, 22, 23, 24, 40, 41, 42, 43),
        Rarity.EPIC to listOf(5, 13, 25, 26, 44, 45, 46),
        Rarity.LEGENDARY to listOf(6, 14, 27, 28, 47, 48)
    )

    fun idsFor(rarity: Rarity): List<Int> =
        if (rarity == Rarity.VOID) listOf(6) else pools.getValue(rarity)

    fun rarityOf(id: Int): Rarity = pools.entries.firstOrNull { id in it.value }?.key ?: Rarity.COMMON

    fun decodeIds(values: Set<String>): Set<Int> =
        values.mapNotNull { it.toIntOrNull()?.takeIf { id -> id in ids } }.toSet()
}
