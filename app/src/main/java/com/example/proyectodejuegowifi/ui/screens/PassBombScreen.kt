package com.example.proyectodejuegowifi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectodejuegowifi.ui.components.PlayerAvatar
import com.example.proyectodejuegowifi.ui.theme.*
import com.example.proyectodejuegowifi.viewmodel.GameViewModel
import com.example.proyectodejuegowifi.viewmodel.Player

@Composable
fun PassBombScreen(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()

    // Solo jugadores vivos que NO son yo
    val targets = state.players.filter {
        it.isAlive && it.id != state.myPlayerId
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))

        Text("💣", fontSize = 48.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "PASAR LA BOMBA",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ColorMuted,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "¿A quién se la pasas?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = ColorPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Elige rápido, la cuenta regresiva sigue",
            fontSize = 13.sp,
            color = ColorMuted,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        // Timer en curso
        Text(
            text = "${state.timerSeconds}s",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = if (state.timerSeconds <= 5) Color(0xFFE53935) else ColorPrimary
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "JUGADORES",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = ColorMuted,
            letterSpacing = 1.5.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(targets) { player ->
                PassTargetItem(player = player) {
                    viewModel.passBombTo(player.id)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun PassTargetItem(player: Player, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ColorSurface)
            .border(1.5.dp, ColorBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerAvatar(
            name = player.name,
            isAlive = true,
            hasBomb = false,
            size = 48.dp,
            fontSize = 16.sp
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = player.name,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = ColorPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "💣  PASAR",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFF0F0F0))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
