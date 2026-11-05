package com.example.proyectodejuegowifi.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectodejuegowifi.ui.components.GameButton
import com.example.proyectodejuegowifi.ui.components.PlayerAvatar
import com.example.proyectodejuegowifi.ui.theme.*
import com.example.proyectodejuegowifi.viewmodel.GameViewModel

@Composable
fun WaitingScreen(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()

    // Pulso animado para el código de sala
    val infiniteTransition = rememberInfiniteTransition(label = "dot")
    val dot1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "d1"
    )
    val dot2 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 200), RepeatMode.Reverse),
        label = "d2"
    )
    val dot3 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 400), RepeatMode.Reverse),
        label = "d3"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))

        // Título
        Text(
            text = "SALA DE ESPERA",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ColorMuted,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(16.dp))

        // Código de sala
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(ColorSurface)
                .border(1.dp, ColorBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 32.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CÓDIGO DE SALA",
                    fontSize = 10.sp,
                    color = ColorMuted,
                    letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = state.roomCode,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = ColorPrimary,
                    letterSpacing = 8.sp
                )
                Text(
                    text = "Comparte este código con tus amigos",
                    fontSize = 11.sp,
                    color = ColorMuted
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // Cargando
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Esperando jugadores", fontSize = 13.sp, color = ColorMuted)
            Spacer(Modifier.width(4.dp))
            Box(
                Modifier.size(7.dp).scale(dot1).clip(CircleShape)
                    .background(ColorMuted)
            )
            Box(
                Modifier.size(7.dp).scale(dot2).clip(CircleShape)
                    .background(ColorMuted)
            )
            Box(
                Modifier.size(7.dp).scale(dot3).clip(CircleShape)
                    .background(ColorMuted)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Lista de jugadores
        Text(
            text = "JUGADORES (${state.players.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ColorMuted,
            letterSpacing = 1.5.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(state.players) { player ->
                PlayerListItem(
                    name = player.name,
                    isMe = player.id == state.myPlayerId
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Solo el host puede iniciar
        if (state.players.isNotEmpty() && state.players[0].id == state.myPlayerId) {
            GameButton(
                text = "INICIAR JUEGO",
                icon = "✦",
                onClick = { viewModel.startGame() },
                enabled = state.players.size >= 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
            if (state.players.size < 2) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "Se necesitan mínimo 2 jugadores",
                    fontSize = 11.sp,
                    color = ColorMuted
                )
            }
        } else {
            Text(
                "El anfitrión iniciará la partida",
                fontSize = 13.sp,
                color = ColorMuted
            )
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun PlayerListItem(name: String, isMe: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ColorSurface)
            .border(1.dp, ColorBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerAvatar(name = name, isAlive = true, hasBomb = false, size = 40.dp, fontSize = 14.sp)
        Spacer(Modifier.width(14.dp))
        Text(
            text = name,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = ColorPrimary,
            modifier = Modifier.weight(1f)
        )
        if (isMe) {
            Text(
                text = "TÚ",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ColorMuted,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFF0F0F0))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}
