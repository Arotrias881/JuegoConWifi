package com.example.proyectodejuegowifi

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectodejuegowifi.ui.screens.*
import com.example.proyectodejuegowifi.ui.theme.BombGameTheme
import com.example.proyectodejuegowifi.viewmodel.GameScreen
import com.example.proyectodejuegowifi.viewmodel.GameViewModel
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector
    private var vibrator: Vibrator? = null
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        soundManager = SoundManager(this)
        setupVibrator()
        setupShakeDetector()
        setupViewModelCallbacks()

        setContent {
            BombGameTheme {
                GameNavigation(viewModel)
            }
        }
    }

    private fun setupVibrator() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private fun setupShakeDetector() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector {
            viewModel.onShakeDetected()
        }
    }

    private fun setupViewModelCallbacks() {
        viewModel.onVibrate = {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        }
        viewModel.onStopVibrate = {
            vibrator?.cancel()
        }
        viewModel.onExplosionSound = {
            soundManager.playExplosion()
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let { sensor ->
            sensorManager.registerListener(
                shakeDetector,
                sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(shakeDetector)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

@Composable
fun GameNavigation(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()

    // Sincronizar estado del ViewModel con navegación
    LaunchedEffect(state.screen) {
        val route = state.screen.name.lowercase()
        val current = navController.currentDestination?.route
        if (current != route) {
            navController.navigate(route) {
                // Limpiar backstack para no acumular
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = GameScreen.LOBBY.name.lowercase()
        ) {
            composable(GameScreen.LOBBY.name.lowercase()) {
                LobbyScreen(viewModel)
            }
            composable(GameScreen.WAITING.name.lowercase()) {
                WaitingScreen(viewModel)
            }
            composable(GameScreen.PLAYING.name.lowercase()) {
                PlayingScreen(viewModel)
            }
            composable(GameScreen.PASS_BOMB.name.lowercase()) {
                PassBombScreen(viewModel)
            }
            composable(GameScreen.EXPLODED.name.lowercase()) {
                ExplodedScreen(viewModel)
            }
            composable(GameScreen.RESULTS.name.lowercase()) {
                ResultsScreen(viewModel)
            }
        }
    }
}
