package com.example.myapplication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import com.example.myapplication.ui.TutorialDialog
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TutorialDialogUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun stepSurvivesRecreationAndFinishesOnlyOnLastPage() {
        var finishes = 0
        val restoration = StateRestorationTester(compose)
        restoration.setContent { MyApplicationTheme { TutorialDialog { finishes++ } } }
        compose.onNodeWithTag("tutorial_next").performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("tutorial_step_1").assertIsDisplayed()
        compose.onNodeWithTag("tutorial_next").performClick()
        compose.onNodeWithTag("tutorial_next").performClick()
        compose.runOnIdle { assertEquals(0, finishes) }
        compose.onNodeWithTag("tutorial_next").performClick()
        compose.runOnIdle { assertEquals(1, finishes) }
    }

    @Test fun backReturnsToPreviousStepAndSkippingFinishes() {
        var finishes = 0
        compose.setContent { MyApplicationTheme { TutorialDialog { finishes++ } } }
        compose.onNodeWithTag("tutorial_back").assertIsNotEnabled()
        compose.onNodeWithTag("tutorial_next").performClick()
        compose.onNodeWithTag("tutorial_back").performScrollTo().performClick()
        compose.onNodeWithTag("tutorial_step_0").assertExists()
        compose.onNodeWithTag("tutorial_skip").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, finishes) }
    }
}
