package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DebrisCatalogTest {
    @Test fun savedOldAndNewDiscoveriesSurviveDecodingWhileInvalidIdsAreRejected() {
        assertEquals(setOf(1, 28, 29, 48), DebrisCatalog.decodeIds(
            setOf("1", "28", "29", "48", "0", "-1", "49", "invalid", "99999999999999999999")))
        val all = DebrisCatalog.ids.toSet()
        assertEquals(all, DebrisCatalog.decodeIds(all.map(Int::toString).toSet()))
    }

    @Test fun rarityPoolsPartitionAllTypesAndPreserveExistingAssignments() {
        val pools = Rarity.entries.filter { it != Rarity.VOID }.map(DebrisCatalog::idsFor)
        assertEquals(48, pools.sumOf { it.size })
        assertEquals(DebrisCatalog.ids.toSet(), pools.flatten().toSet())
        val oldPools = mapOf(
            Rarity.COMMON to listOf(1, 2, 7, 8, 15, 16, 17, 18),
            Rarity.UNCOMMON to listOf(3, 9, 10, 19, 20, 21),
            Rarity.RARE to listOf(4, 11, 12, 22, 23, 24),
            Rarity.EPIC to listOf(5, 13, 25, 26),
            Rarity.LEGENDARY to listOf(6, 14, 27, 28))
        oldPools.forEach { (rarity, ids) ->
            ids.forEach { assertEquals(rarity, DebrisCatalog.rarityOf(it)) }
            assertTrue(DebrisCatalog.idsFor(rarity).containsAll(ids))
        }
    }
}
