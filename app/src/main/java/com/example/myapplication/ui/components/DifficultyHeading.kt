package com.example.myapplication.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplication.QuestDifficulty
import com.example.myapplication.R
import com.example.myapplication.ui.theme.AppColors

@Composable
internal fun DifficultyHeading(difficulty: QuestDifficulty) {
    val label = when (difficulty) {
        QuestDifficulty.EASY -> R.string.quest_difficulty_easy
        QuestDifficulty.MEDIUM -> R.string.quest_difficulty_medium
        QuestDifficulty.HARD -> R.string.quest_difficulty_hard
    }
    val color = when (difficulty) {
        QuestDifficulty.EASY -> AppColors.Primary
        QuestDifficulty.MEDIUM -> AppColors.Secondary
        QuestDifficulty.HARD -> AppColors.Warning
    }
    Text(stringResource(label), color = color, fontSize = 17.sp, fontWeight = FontWeight.Bold)
}
