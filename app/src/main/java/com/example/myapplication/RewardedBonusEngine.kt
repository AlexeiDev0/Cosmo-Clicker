package com.example.myapplication

/** An offer belongs to one offline window; consuming it makes repeated callbacks harmless. */
object RewardedBonusEngine {
    const val MIN_OFFLINE_SECONDS = 60L

    fun isEligible(state: GameState): Boolean = !state.offlineBonusOfferId.isNullOrBlank() &&
        state.lastOfflineSeconds >= MIN_OFFLINE_SECONDS &&
        state.lastOfflineReward.isFinite() && state.lastOfflineReward > 0.0

    fun claim(state: GameState, offerId: String): GameState? {
        if (!isEligible(state) || offerId.isBlank() || state.offlineBonusOfferId != offerId) return null
        val bonus = state.lastOfflineReward
        val balance = state.totalDebris + bonus
        if (!bonus.isFinite() || bonus <= 0.0 || !balance.isFinite() || balance <= state.totalDebris) return null
        return state.copy(totalDebris = balance, offlineBonusOfferId = null,
            lastOfflineReward = 0.0, lastOfflineSeconds = 0L)
    }
}
