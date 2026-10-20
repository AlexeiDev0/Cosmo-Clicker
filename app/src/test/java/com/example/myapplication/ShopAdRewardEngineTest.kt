package com.example.myapplication

import org.junit.Assert.*
import org.junit.Test

class ShopAdRewardEngineTest {
    private fun offer(state: GameState, type: ShopAdReward, id: String = "ad") =
        state.copy(shopAdOfferId = id, shopAdOfferType = type, shopAdOfferAmount = 5_000.0)

    @Test fun debrisIsQuotedAndCannotBeClaimedTwice() {
        val before = offer(GameState(totalDebris = 200.0), ShopAdReward.DEBRIS)
        assertNull(ShopAdRewardEngine.claim(before, "stale"))
        val after = ShopAdRewardEngine.claim(before, "ad")!!
        assertEquals(5_200.0, after.totalDebris, 0.0)
        assertNull(ShopAdRewardEngine.claim(after, "ad"))
    }

    @Test fun threeConfirmedViewsEarnExactlyOneFreeCase() {
        var state = GameState()
        repeat(3) { index ->
            state = ShopAdRewardEngine.claim(offer(state, ShopAdReward.COMMON_CASE, "$index"), "$index")!!
            assertNull(ShopAdRewardEngine.claim(state, "$index"))
            assertEquals(index + 1, state.shopCaseAdProgress)
            if (index < 2) assertNull(ShopAdRewardEngine.openEarnedCase(state))
        }
        val opened = ShopAdRewardEngine.openEarnedCase(state)!!
        assertEquals(0, opened.shopCaseAdProgress)
        assertTrue(opened.isOpeningCase)
        assertTrue(opened.isRewardCaseOpening)
        assertEquals(CaseType.COMMON, opened.openingCaseType)
        assertEquals(state.totalDebris, opened.totalDebris, 0.0)
        assertEquals(state.casePurchasesByType, CaseController.purchaseCountsAfterOpening(opened, CaseType.COMMON))
        assertNull(ShopAdRewardEngine.openEarnedCase(opened))
    }

    @Test fun earnedCaseWaitsForAnExistingOpeningWithoutLosingProgress() {
        val state = GameState(shopCaseAdProgress = 3, isOpeningCase = true)
        assertNull(ShopAdRewardEngine.openEarnedCase(state))
        assertNotNull(ShopAdRewardEngine.openEarnedCase(state.copy(isOpeningCase = false)))
    }

    @Test fun rewardHasFiveThousandFloorAndGrowsWithUnlockedPlanets() {
        assertEquals(5_000.0, ShopAdRewardEngine.debrisReward(GameState(), emptyMap()), 0.0)
        val later = GameState(ownedPlanets = setOf("p1", "p10"))
        assertTrue(ShopAdRewardEngine.debrisReward(later, emptyMap()) > 5_000.0)
    }

    @Test fun invalidAndOverflowingAmountsCannotChangeBalance() {
        val state = offer(GameState(), ShopAdReward.DEBRIS)
        listOf(Double.NaN, Double.POSITIVE_INFINITY, -1.0, 0.0).forEach {
            assertNull(ShopAdRewardEngine.claim(state.copy(shopAdOfferAmount = it), "ad"))
        }
        assertNull(ShopAdRewardEngine.claim(state.copy(totalDebris = Double.MAX_VALUE,
            shopAdOfferAmount = Double.MAX_VALUE), "ad"))
    }

    @Test fun fleetGrowthScalesRewardButTemporaryBoostsDoNot() {
        val fleet = mapOf("drone_1" to FleetConfig("drone_1", "Drone", 0.0, 0, rarity = Rarity.LEGENDARY))
        val state = GameState(fleetCounts = mapOf("drone_1" to 2), activeFleetCounts = mapOf("drone_1" to 2))
        val amount = ShopAdRewardEngine.debrisReward(state, fleet)
        assertTrue(amount >= 300_000.0)
        assertEquals(amount, ShopAdRewardEngine.debrisReward(state.copy(
            activeEffects = mapOf(SkillType.TRADE_POWER.id to Long.MAX_VALUE)), fleet), 0.0)
        assertTrue(ShopAdRewardEngine.debrisReward(state.copy(activeFleetCounts = mapOf("drone_1" to 4)), fleet) > amount)
    }
}
