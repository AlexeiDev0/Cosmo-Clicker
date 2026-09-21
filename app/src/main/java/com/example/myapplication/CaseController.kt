package com.example.myapplication

object CaseController {
    fun purchaseCountsAfterOpening(state: GameState, type: CaseType): Map<CaseType, Int> =
        if (state.isRewardCaseOpening) state.casePurchasesByType
        else state.casePurchasesByType + (type to ((state.casePurchasesByType[type] ?: 0) + 1))

    fun cost(casesPurchased: Int, type: CaseType = CaseType.COMMON): Double =
        GameRules.calculateCaseCost(casesPurchased, type)

    fun bundleCost(casesPurchased: Int, type: CaseType, count: Int): Double =
        GameRules.calculateCaseBundleCost(casesPurchased, type, count)

    fun maxAffordable(balance: Double, casesPurchased: Int, type: CaseType): Int =
        GameRules.maxAffordableCases(balance, casesPurchased, type)

    fun startOpening(state: GameState, type: CaseType, count: Int): GameState? {
        val safeCount = count.coerceAtLeast(1)
        val price = bundleCost(state.casePurchasesByType[type] ?: 0, type, safeCount)
        if (state.isOpeningCase || state.lastDroppedDroneId != null || state.totalDebris < price) return null
        return state.copy(
            totalDebris = state.totalDebris - price,
            isOpeningCase = true,
            openingCaseType = type,
            pendingCaseOpenings = safeCount - 1,
            isRewardCaseOpening = false,
            caseBundleRewards = emptyMap(),
            showCaseBundleSummary = false,
            lastDroppedDroneId = null
        )
    }

    fun collectDisplayedReward(state: GameState): GameState? {
        if (state.lastDroppedDroneId == null) return null
        if (state.pendingCaseOpenings > 0 && state.openingCaseType != null) {
            return state.copy(
                lastDroppedDroneId = null,
                isOpeningCase = true,
                pendingCaseOpenings = state.pendingCaseOpenings - 1
            )
        }
        val isBundle = state.caseBundleRewards.values.sum() >= 2
        return state.copy(
            lastDroppedDroneId = null,
            openingCaseType = null,
            pendingCaseOpenings = 0,
            isRewardCaseOpening = false,
            showCaseBundleSummary = isBundle,
            caseBundleRewards = if (isBundle) state.caseBundleRewards else emptyMap()
        )
    }
}
