package com.karaskiewicz.audioai.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.karaskiewicz.audioai.ui.screen.MainScreen
import com.karaskiewicz.audioai.ui.theme.AudioAITheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun mainScreen_displaysCorrectContent() {
    composeTestRule.setContent {
      AudioAITheme {
        MainScreen(onNavigateToSettings = {})
      }
    }

    // Check if main elements are displayed
    composeTestRule.onNodeWithText("Audio AI").assertIsDisplayed()
    composeTestRule.onNodeWithText("How to use:").assertIsDisplayed()
    composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
  }
}
