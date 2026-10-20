package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.*
import com.example.myapplication.R
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.utils.formatNum

@Composable
fun ShopAdRewards(state: GameState, amount: Double, status: RewardedAdStatus,
    onWatch: (ShopAdReward, Double) -> Unit, onOpenCase: () -> Unit) {
    val busy = status == RewardedAdStatus.LOADING || status == RewardedAdStatus.SHOWING
    Card(colors = CardDefaults.cardColors(containerColor = AppColors.CardBackground)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.shop_ad_rewards), color = AppColors.Primary)
            Text(stringResource(R.string.shop_ad_notice), color = AppColors.TextMuted, fontSize = 11.sp)
            Button(onClick = { onWatch(ShopAdReward.DEBRIS, amount) }, enabled = !busy,
                modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.shop_ad_debris, formatNum(amount)))
            }
            Text(stringResource(R.string.shop_ad_scaling), color = AppColors.TextMuted, fontSize = 11.sp)
            Button(onClick = {
                if (state.shopCaseAdProgress >= ShopAdRewardEngine.CASE_ADS) onOpenCase()
                else onWatch(ShopAdReward.COMMON_CASE, 0.0)
            }, enabled = !busy && ShopAdRewardEngine.canWatchCase(state), modifier = Modifier.fillMaxWidth()) {
                Text(if (state.shopCaseAdProgress >= ShopAdRewardEngine.CASE_ADS)
                    stringResource(R.string.shop_ad_open_case)
                else stringResource(R.string.shop_ad_case, state.shopCaseAdProgress))
            }
            if (status == RewardedAdStatus.LOADING || status == RewardedAdStatus.SHOWING || status == RewardedAdStatus.UNAVAILABLE) {
                Text(stringResource(when (status) {
                    RewardedAdStatus.LOADING -> R.string.ad_loading
                    RewardedAdStatus.SHOWING -> R.string.ad_showing
                    else -> R.string.shop_ad_unavailable
                }), color = AppColors.TextMuted, fontSize = 11.sp)
            }
            if (BuildConfig.TEST_ADS) Text(stringResource(R.string.ad_test_mode), color = AppColors.TextMuted, fontSize = 11.sp)
        }
    }
}
