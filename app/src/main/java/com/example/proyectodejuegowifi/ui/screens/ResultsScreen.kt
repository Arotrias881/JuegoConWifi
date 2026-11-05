package com.example.proyectodejuegowifi.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectodejuegowifi.R
import com.example.proyectodejuegowifi.ui.components.GameButton
import com.example.proyectodejuegowifi.ui.theme.*
import com.example.proyectodejuegowifi.viewmodel.GameViewModel
import com.example.proyectodejuegowifi.viewmodel.Player

@Composable
fun ResultsScreen(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()

    // El ganador es el que sigue vivo
    val winner = state.players.find { it.isAlive }
    val isIWinner = winner?.id == state.myPlayerId

    val scale = remember { Animatable(0.3f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.4f, stiffness = 200f)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        // Trofeo
        Text(
            text = if (isIWinner) "🏆" else "💀",
            fontSize = 52.sp,
            modifier = Modifier.scale(scale.value)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "FIN DEL JUEGO",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ColorMuted,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isIWinner) "¡GANASTE!" else "GANÓ ${winner?.name?.uppercase() ?: ""}",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = ColorPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = if (isIWinner) "Sobreviviste a todas las explosiones"
            else "Fue el último en sobrevivir",
            fontSize = 13.sp,
            color = ColorMuted,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // Imagen ganador
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(
                    id = if (isIWinner) R.drawable.estado_normal else R.drawable.estado_muerto
                ),
                contentDescription = "Resultado",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(24.dp))

        // Tabla de posiciones
        Text(
            text = "CLASIFICACIÓN",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = ColorMuted,
            letterSpacing = 1.5.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        // Los vivos primero, luego los muertos
        val sorted = state.players.sortedByDescending { it.isAlive }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(sorted) { index, player ->
                RankingItem(
                    position = index + 1,
                    player = player,
                    isMe = player.id == state.myPlayerId,
                    isWinner = player.isAlive
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        GameButton(
            text = "JUGAR DE NUEVO",
            icon = "✦",
            onClick = { viewModel.restartGame() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        )
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun RankingItem(
    position: Int,
    player: Player,
    isMe: Boolean,
    isWinner: Boolean
) {
    val bgColor = if (isWinner) ColorPrimary else ColorSurface
    val textColor = if (isWinner) Color.White else ColorPrimary
    val mutedColor = if (isWinner) Color(0xFFAAAAAA) else ColorMuted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, if (isWinner) ColorPrimary else ColorBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Posición
        Text(
            text = if (isWinner) "🏆" else "#$position",
            fontSize = if (isWinner) 20.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            color = mutedColor,
            modifier = Modifier.width(36.dp)
        )
        Spacer(Modifier.width(10.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isWinner) Color.White.copy(alpha = 0.15f) else ColorBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (!player.isAlive) "💀" else player.name.take(2).uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
        Spacer(Modifier.width(12.dp))

        Text(
            text = player.name,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        if (isMe) {
            Text(
                text = "TÚ",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = mutedColor,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isWinner) Color.White.copy(alpha = 0.15f) else Color(0xFFF0F0F0))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}
