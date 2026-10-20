package com.example.myapplication

object CaseController {
    /** Older saves omitted the displayed reward, leaving paid bundle cases stranded. */
    fun restoreOpening(state: GameState): GameState {
        if (state.isOpeningCase || state.lastDroppedDroneId != null || state.showCaseBundleSummary ||
            state.pendingCaseOpenings <= 0 || state.openingCaseType == null) return state
        return state.copy(isOpeningCase = true, pendingCaseOpenings = state.pendingCaseOpenings - 1)
    }

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
        if (count <= 0 || !state.totalDebris.isFinite() || state.totalDebris < 0.0) return null
        val safeCount = count
        val price = bundleCost(state.casePurchasesByType[type] ?: 0, type, safeCount)
        if (!price.isFinite() || price <= 0.0 || state.isOpeningCase ||
            state.lastDroppedDroneId != null || state.showCaseBundleSummary || state.totalDebris < price) return null
        val remainingDebris = state.totalDebris - price
        if (!remainingDebris.isFinite() || remainingDebris >= state.totalDebris) return null
        return state.copy(
            totalDebris = remainingDebris,
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
