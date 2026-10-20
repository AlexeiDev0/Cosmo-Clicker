package com.example.myapplication

enum class ShopAdReward { DEBRIS, COMMON_CASE }

object ShopAdRewardEngine {
    const val CASE_ADS = 3

    fun debrisReward(state: GameState, fleet: Map<String, FleetConfig>, clickItems: List<ItemConfig> = emptyList()): Double {
        val planet = state.ownedPlanets.maxByOrNull(EconomyBalance::planetIndex) ?: "p1"
        val income = EconomyBalance.passiveIncome(state.copy(activeEffects = emptyMap()), fleet, 0L, clickItems)
        return EconomyBalance.roundReward(maxOf(5_000.0, EconomyBalance.scaledReward(5_000.0, planet),
            income.takeIf { it.isFinite() }?.times(60.0) ?: 0.0)).coerceAtLeast(5_000.0)
    }

    fun canWatchCase(state: GameState): Boolean = !state.isOpeningCase &&
        state.lastDroppedDroneId == null && !state.showCaseBundleSummary && state.pendingCaseOpenings == 0

    fun claim(state: GameState, offerId: String): GameState? {
        if (offerId.isBlank() || state.shopAdOfferId != offerId) return null
        val cleared = state.copy(shopAdOfferId = null, shopAdOfferType = null, shopAdOfferAmount = 0.0)
        return when (state.shopAdOfferType) {
            ShopAdReward.DEBRIS -> {
                val amount = state.shopAdOfferAmount
                val balance = state.totalDebris + amount
                if (!amount.isFinite() || amount < 5_000.0 || !balance.isFinite() || balance <= state.totalDebris) null
                else cleared.copy(totalDebris = balance)
            }
            ShopAdReward.COMMON_CASE -> cleared.copy(shopCaseAdProgress = (state.shopCaseAdProgress + 1).coerceAtMost(CASE_ADS))
            null -> null
        }
    }

    fun openEarnedCase(state: GameState): GameState? {
        if (state.shopCaseAdProgress < CASE_ADS || !canWatchCase(state)) return null
        return state.copy(shopCaseAdProgress = 0, isOpeningCase = true, openingCaseType = CaseType.COMMON,
            isRewardCaseOpening = true, caseBundleRewards = emptyMap(), pendingCaseOpenings = 0)
    }
}
