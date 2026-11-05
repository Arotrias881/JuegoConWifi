package com.example.proyectodejuegowifi.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.proyectodejuegowifi.ui.components.OutlineGameButton
import com.example.proyectodejuegowifi.ui.theme.*
import com.example.proyectodejuegowifi.viewmodel.GameViewModel

@Composable
fun ExplodedScreen(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    val isMe = state.eliminatedPlayerId == state.myPlayerId
    val alivePlayers = state.players.filter { it.isAlive }

    // Animación de entrada
    val scale = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(52.dp))

        // Imagen muerto
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(scale.value)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.estado_muerto),
                contentDescription = "Eliminado",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(24.dp))

        // Emoji y título
        Text("💥", fontSize = 40.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "¡BOOM!",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = ColorPrimary,
            letterSpacing = 4.sp
        )
        Spacer(Modifier.height(10.dp))

        // Quién murió
        if (isMe) {
            Text(
                text = "La bomba explotó en tus manos",
                fontSize = 16.sp,
                color = Color(0xFFE53935),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Quedas eliminado esta ronda",
                fontSize = 13.sp,
                color = ColorMuted,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "${state.eliminatedPlayerName} fue eliminado",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "La bomba explotó en sus manos",
                fontSize = 13.sp,
                color = ColorMuted,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(28.dp))

        // Jugadores que quedan
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ColorSurface)
                .border(1.dp, ColorBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "SIGUEN EN JUEGO (${alivePlayers.size})",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorMuted,
                    letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    alivePlayers.forEach { player ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(ColorPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = player.name.take(2).uppercase(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = player.name.take(6),
                                fontSize = 10.sp,
                                color = ColorSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Acciones
        if (alivePlayers.size > 1) {
            GameButton(
                text = "SIGUIENTE RONDA",
                icon = "✦",
                onClick = { viewModel.continueAfterExplosion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        } else {
            GameButton(
                text = "VER RESULTADOS",
                icon = "🏆",
                onClick = { viewModel.continueAfterExplosion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        }
        Spacer(Modifier.height(10.dp))
        OutlineGameButton(
            text = "ABANDONAR PARTIDA",
            onClick = { viewModel.restartGame() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
        Spacer(Modifier.height(28.dp))
    }
}
