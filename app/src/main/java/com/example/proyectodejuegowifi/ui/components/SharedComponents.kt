package com.example.proyectodejuegowifi.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectodejuegowifi.ui.theme.ColorBorder
import com.example.proyectodejuegowifi.ui.theme.ColorPrimary

// Botón principal estilo minimalista negro
@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: String = ""
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPrimary,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFCCCCCC),
            disabledContentColor = Color(0xFF888888)
        )
    ) {
        if (icon.isNotEmpty()) {
            Text(text = "$icon  $text", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        } else {
            Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Botón secundario con borde
@Composable
fun OutlineGameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = ColorPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ColorPrimary)
    ) {
        Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

// Avatar circular de jugador con iniciales
@Composable
fun PlayerAvatar(
    name: String,
    isAlive: Boolean,
    hasBomb: Boolean,
    size: Dp = 52.dp,
    fontSize: TextUnit = 18.sp
) {
    val initials = name.take(2).uppercase()
    val bgColor = when {
        !isAlive -> Color(0xFFEEEEEE)
        hasBomb -> Color(0xFF1A1A1A)
        else -> Color(0xFFF5F5F0)
    }
    val textColor = when {
        !isAlive -> Color(0xFFAAAAAA)
        hasBomb -> Color.White
        else -> ColorPrimary
    }
    val borderColor = when {
        !isAlive -> Color(0xFFDDDDDD)
        hasBomb -> ColorPrimary
        else -> ColorBorder
    }
    val borderWidth = if (hasBomb) 2.5.dp else 1.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor)
            .border(borderWidth, borderColor, CircleShape)
    ) {
        if (!isAlive) {
            Text("💀", fontSize = (fontSize.value * 0.9f).sp)
        } else {
            Text(
                text = initials,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

// Indicador pulsante (cuando tienes la bomba)
@Composable
fun PulsingDot(color: Color = Color(0xFFE53935), size: Dp = 12.dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}
