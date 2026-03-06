package com.example.skgarm.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



val Teal = Color(0xFF1DB87B)
val BgPrimary = Color(0xFF0A0A0A)
val BgCard = Color(0xFF111111)
val BgSlot = Color(0xFF151515)
val BgSlotActive = Color(0xFF0F3D28)
val BgField = Color(0xFF1C1C1C)
val BgModal = Color(0xFF1A1A1A)
val BgPersonIcon = Color(0xFF2A2A2A)
val TextPrimary = Color.White
val TextSecondary = Color(0xFF888888)
val TextMuted = Color(0xFF555555)
val BorderDefault = Color(0xFF1A1A1A)
val BorderActive = Color(0xFF2A5C40)
val ErrorRed = Color(0xFFFF6B6B)



private val DarkColors = darkColorScheme(
    primary = Teal,
    background = BgPrimary,
    surface = BgCard,
    onPrimary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun SkgArmTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
