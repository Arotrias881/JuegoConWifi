package com.example.proyectodejuegowifi.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectodejuegowifi.R
import com.example.proyectodejuegowifi.ui.components.GameButton
import com.example.proyectodejuegowifi.ui.components.OutlineGameButton
import com.example.proyectodejuegowifi.ui.theme.*
import com.example.proyectodejuegowifi.viewmodel.GameViewModel

@Composable
fun LobbyScreen(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    var showJoinDialog by remember { mutableStateOf(false) }
    var joinCode by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Header
            Text(
                text = "💣",
                fontSize = 56.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "BOMBA",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = ColorPrimary,
                letterSpacing = 6.sp
            )
            Text(
                text = "El juego más explosivo",
                fontSize = 13.sp,
                color = ColorMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Imagen bomba
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bomba),
                    contentDescription = "Bomba",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Input nombre
            Text(
                text = "¿CÓMO TE LLAMAS?",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ColorMuted,
                letterSpacing = 2.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.currentPlayerName,
                onValueChange = { viewModel.setPlayerName(it) },
                placeholder = { Text("Tu nombre de jugador", color = ColorMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorPrimary,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorPrimary,
                    unfocusedTextColor = ColorPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Botones
            GameButton(
                text = "CREAR SALA",
                icon = "✦",
                onClick = {
                    if (state.currentPlayerName.isNotBlank()) {
                        viewModel.createRoom()
                    }
                },
                enabled = state.currentPlayerName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlineGameButton(
                text = "UNIRSE A SALA",
                onClick = { showJoinDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        }

        // Instrucciones en fondo
        Text(
            text = "Agita el celular para pasar la bomba",
            fontSize = 12.sp,
            color = ColorMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }

    // Diálogo para unirse
    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = {
                Text("Unirse a sala", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Ingresa el código de sala", color = ColorMuted, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = joinCode,
                        onValueChange = { joinCode = it.take(4) },
                        placeholder = { Text("Ej: 4821") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorPrimary,
                            unfocusedBorderColor = ColorBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (joinCode.length == 4 && state.currentPlayerName.isNotBlank()) {
                            viewModel.joinRoom(joinCode)
                            showJoinDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                ) {
                    Text("UNIRSE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinDialog = false }) {
                    Text("Cancelar", color = ColorMuted)
                }
            },
            containerColor = ColorSurface
        )
    }
}
