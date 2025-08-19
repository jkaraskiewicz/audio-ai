package com.karaskiewicz.scribely.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaskiewicz.scribely.ui.theme.UIConfig
import com.karaskiewicz.scribely.ui.theme.VT323FontFamily

/**
 * Pixel Art Style Button with retro gaming aesthetic
 */
@Composable
fun PixelButton(
  text: String,
  backgroundColor: Color,
  shadowColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  var isPressed by remember { mutableStateOf(false) }

  Box(
    modifier =
      modifier
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          enabled = enabled,
        ) {
          isPressed = true
          onClick()
          isPressed = false
        }
        .drawBehind {
          // Draw shadow/border effect
          val shadowOffset = if (isPressed) 2.dp.toPx() else 4.dp.toPx()
          drawRect(
            color = shadowColor,
            topLeft = Offset(shadowOffset, shadowOffset),
            size =
              size.copy(
                width = size.width - shadowOffset,
                height = size.height - shadowOffset,
              ),
          )
        }
        .background(backgroundColor)
        .border(4.dp, shadowColor)
        .offset(
          x = if (isPressed) 2.dp else 0.dp,
          y = if (isPressed) 2.dp else 0.dp,
        )
        .padding(horizontal = 24.dp, vertical = 12.dp),
  ) {
    Text(
      text = text,
      fontFamily = VT323FontFamily,
      fontSize = 24.sp,
      color = Color.White,
    )
  }
}

/**
 * Pixel Art Style Timer Display
 */
@Composable
fun PixelTimerDisplay(
  time: String,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier =
      modifier
        .background(UIConfig.PixelColors.InputBackground)
        .border(4.dp, UIConfig.PixelColors.Border)
        .padding(16.dp),
  ) {
    Text(
      text = time,
      color = UIConfig.PixelColors.TimerText,
      fontSize = 96.sp,
      fontFamily = VT323FontFamily,
    )
  }
}

/**
 * Pixel Art Style Text Field
 */
@Composable
fun PixelTextField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    Text(
      text = label,
      color = UIConfig.PixelColors.Text,
      fontSize = 24.sp,
      fontFamily = VT323FontFamily,
    )
    BasicTextField(
      value = value,
      onValueChange = onValueChange,
      textStyle =
        TextStyle(
          color = UIConfig.PixelColors.TimerText,
          fontSize = 24.sp,
          fontFamily = VT323FontFamily,
        ),
      modifier =
        Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
          .background(UIConfig.PixelColors.InputBackground)
          .border(2.dp, UIConfig.PixelColors.Border)
          .padding(8.dp),
    )
  }
}

/**
 * Pixel Art Style Header Text
 */
@Composable
fun PixelHeaderText(
  text: String,
  modifier: Modifier = Modifier,
) {
  Text(
    text = text,
    color = UIConfig.PixelColors.Text,
    fontSize = 28.sp,
    fontFamily = VT323FontFamily,
    modifier = modifier,
  )
}

/**
 * Pixel Art Style Clickable Text (for navigation)
 */
@Composable
fun PixelClickableText(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Text(
    text = text,
    color = UIConfig.PixelColors.Text,
    fontSize = 28.sp,
    fontFamily = VT323FontFamily,
    modifier =
      modifier
        .clickable { onClick() },
  )
}
