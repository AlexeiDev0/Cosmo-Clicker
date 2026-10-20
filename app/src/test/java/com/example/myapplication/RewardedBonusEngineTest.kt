package com.example.myapplication

import org.junit.Assert.*
import org.junit.Test

class RewardedBonusEngineTest {
    private val offer = GameState(totalDebris = 125.0, lastOfflineReward = 25.0,
        lastOfflineSeconds = 60L, offlineBonusOfferId = "window-a")

    @Test fun addsOnlyTheBonusAndConsumesTheOffer() {
        val claimed = RewardedBonusEngine.claim(offer, "window-a")!!
        assertEquals(150.0, claimed.totalDebris, 0.0)
        assertNull(claimed.offlineBonusOfferId)
        assertEquals(0.0, claimed.lastOfflineReward, 0.0)
        assertNull(RewardedBonusEngine.claim(claimed, "window-a"))
    }

    @Test fun rejectsCallbackFromAnotherWindowOrAfterReset() {
        assertNull(RewardedBonusEngine.claim(offer, "window-b"))
        assertNull(RewardedBonusEngine.claim(GameState(), "window-a"))
        assertNull(RewardedBonusEngine.claim(offer.copy(offlineBonusOfferId = null), "window-a"))
        assertFalse(RewardedBonusEngine.isEligible(offer.copy(offlineBonusOfferId = " ")))
    }

    @Test fun usesCurrentBalanceWithoutOverwritingPurchasesOrEarnings() {
        assertEquals(525.0, RewardedBonusEngine.claim(offer.copy(totalDebris = 500.0), "window-a")!!.totalDebris, 0.0)
    }

    @Test fun rejectsInvalidRewardsAndOverflow() {
        listOf(0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY).forEach {
            assertNull(RewardedBonusEngine.claim(offer.copy(lastOfflineReward = it), "window-a"))
        }
        assertNull(RewardedBonusEngine.claim(offer.copy(totalDebris = Double.MAX_VALUE,
            lastOfflineReward = Double.MAX_VALUE), "window-a"))
    }

    @Test fun shortAbsenceCannotOfferAnotherRewardedAd() {
        assertFalse(RewardedBonusEngine.isEligible(offer.copy(lastOfflineSeconds = 59L)))
        assertTrue(RewardedBonusEngine.isEligible(offer))
    }
}
