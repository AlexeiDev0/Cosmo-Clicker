package com.example.myapplication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.example.myapplication.ui.components.QuestPanel
import com.example.myapplication.ui.components.QuestItemRow
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.utils.formatNum
import org.junit.Rule
import org.junit.Test

class QuestRewardUiTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun dailyBonusRemainsAvailableAfterAllMissionsAreClaimed() {
        compose.setContent {
            MyApplicationTheme { QuestPanel(GameState(), {}, {}, {}) }
        }
        compose.onNodeWithText(context.getString(R.string.daily_reward_title)).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.daily_reward_claim)).assertIsDisplayed()
    }

    @Test fun mixedRewardShowsDebrisCasesAndPrestigeTogether() {
        val quest = Quest("mixed", QuestType.CLICK_PLANET, "", 1.0, 1.0,
            rewardDebris = 100.0, rewardCases = 2, rewardPrestigePoints = 1, isCompleted = true)
        compose.setContent { MyApplicationTheme { QuestItemRow(quest, {}) } }
        compose.onNodeWithText(context.getString(R.string.reward_debris, formatNum(100.0))).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.reward_cases, 2)).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.reward_prestige_points, 1)).assertIsDisplayed()
    }
}
