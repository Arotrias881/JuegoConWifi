package com.example.proyectodejuegowifi.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.example.proyectodejuegowifi.ui.components.PlayerAvatar
import com.example.proyectodejuegowifi.ui.components.PulsingDot
import com.example.proyectodejuegowifi.ui.theme.*
import com.example.proyectodejuegowifi.viewmodel.GameViewModel

@Composable
fun PlayingScreen(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    val iMyTurn = state.bombHolderId == state.myPlayerId
    val holder = state.players.find { it.id == state.bombHolderId }
    val progress = if (state.totalTime > 0) state.timerSeconds.toFloat() / state.totalTime else 0f

    // Animación shake de la bomba cuando me toca
    val infiniteTransition = rememberInfiniteTransition(label = "bomb")
    val shake by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )
    val bombScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        // Header: ronda y código sala
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ColorSurface)
                    .border(1.dp, ColorBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text("RONDA ${state.round}", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = ColorMuted, letterSpacing = 1.sp)
            }

            if (iMyTurn) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    PulsingDot()
                    Text("ES TU TURNO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935), letterSpacing = 1.sp)
                }
            } else {
                Text(
                    text = "Sala ${state.roomCode}",
                    fontSize = 12.sp,
                    color = ColorMuted
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Jugadores activos en fila
        val alivePlayers = state.players.filter { it.isAlive }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(state.players) { player ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PlayerAvatar(
                        name = player.name,
                        isAlive = player.isAlive,
                        hasBomb = player.id == state.bombHolderId,
                        size = 48.dp,
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = player.name.take(5),
                        fontSize = 10.sp,
                        color = if (player.isAlive) ColorSecondary else ColorMuted,
                        fontWeight = if (player.id == state.bombHolderId) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // Imagen bomba / jugador
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .then(
                    if (iMyTurn) Modifier.rotate(shake).scale(bombScale) else Modifier
                )
        ) {
            Image(
                painter = painterResource(
                    id = if (iMyTurn) R.drawable.bomba else R.drawable.estado_normal
                ),
                contentDescription = if (iMyTurn) "Tienes la bomba" else "Estado normal",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(16.dp))

        // Quién tiene la bomba
        if (iMyTurn) {
            Text(
                text = "¡TIENES LA BOMBA!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = ColorPrimary
            )
            Text(
                text = "Agita el celular para pasarla",
                fontSize = 13.sp,
                color = ColorMuted,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = holder?.name?.uppercase() ?: "",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = ColorPrimary
            )
            Text(
                text = "tiene la bomba",
                fontSize = 13.sp,
                color = ColorMuted
            )
        }

        Spacer(Modifier.height(28.dp))

        // Timer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${state.timerSeconds}s",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = if (state.timerSeconds <= 5) Color(0xFFE53935) else ColorPrimary
            )
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = if (state.timerSeconds <= 5) Color(0xFFE53935) else ColorPrimary,
                trackColor = ColorBorder
            )
        }

        Spacer(Modifier.height(28.dp))

        // Botón pasar bomba
        if (iMyTurn) {
            GameButton(
                text = "PASAR LA BOMBA",
                icon = "💣",
                onClick = { viewModel.openPassBomb() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        }
    }
}
