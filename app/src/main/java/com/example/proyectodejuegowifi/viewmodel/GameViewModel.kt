package com.example.proyectodejuegowifi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

// ── Modelos de datos ──────────────────────────────────────────────────────────

data class Player(
    val id: Int,
    val name: String,
    val isAlive: Boolean = true,
    val score: Int = 0
)

enum class GameScreen {
    LOBBY,       // Sala de espera / ingreso de nombre
    WAITING,     // Esperando jugadores
    PLAYING,     // Partida en curso
    PASS_BOMB,   // Selector: pasar la bomba
    EXPLODED,    // La bomba explotó
    RESULTS      // Resultados finales
}

data class GameState(
    val screen: GameScreen = GameScreen.LOBBY,
    val players: List<Player> = emptyList(),
    val currentPlayerName: String = "",
    val myPlayerId: Int = 0,
    val bombHolderId: Int = -1,
    val timerSeconds: Int = 0,
    val totalTime: Int = 0,
    val isVibrating: Boolean = false,
    val eliminatedPlayerId: Int = -1,
    val eliminatedPlayerName: String = "",
    val round: Int = 1,
    val roomCode: String = ""
)

// ── ViewModel ────────────────────────────────────────────────────────────────

class GameViewModel : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var vibrateJob: Job? = null

    // Callbacks que la Activity registra para efectos físicos y sonido
    var onVibrate: (() -> Unit)? = null
    var onStopVibrate: (() -> Unit)? = null
    var onExplosionSound: (() -> Unit)? = null
    var onNavigate: ((GameScreen) -> Unit)? = null

    // ── Lobby ──

    fun setPlayerName(name: String) {
        _state.update { it.copy(currentPlayerName = name) }
    }

    fun createRoom() {
        val code = (1000..9999).random().toString()
        val me = Player(id = 0, name = _state.value.currentPlayerName)
        _state.update {
            it.copy(
                roomCode = code,
                players = listOf(me),
                myPlayerId = 0,
                screen = GameScreen.WAITING
            )
        }
        // Simular que se unen jugadores (en producción esto sería red real)
        simulatePlayersJoining()
    }

    fun joinRoom(code: String) {
        val me = Player(id = 0, name = _state.value.currentPlayerName)
        _state.update {
            it.copy(
                roomCode = code,
                players = listOf(me),
                myPlayerId = 0,
                screen = GameScreen.WAITING
            )
        }
        simulatePlayersJoining()
    }

    // Simulación: agrega jugadores ficticios para demo de UI
    private fun simulatePlayersJoining() {
        viewModelScope.launch {
            val fakeNames = listOf("Alex", "Sam", "Jordan", "Taylor")
            fakeNames.forEachIndexed { index, name ->
                delay(800L * (index + 1))
                _state.update { current ->
                    val updated = current.players.toMutableList().apply {
                        add(Player(id = index + 1, name = name))
                    }
                    current.copy(players = updated)
                }
            }
        }
    }

    fun startGame() {
        val alivePlayers = _state.value.players.filter { it.isAlive }
        if (alivePlayers.size < 2) return

        val firstHolder = alivePlayers.random().id
        val bombTime = Random.nextInt(10, 31) // 10–30 segundos

        _state.update {
            it.copy(
                screen = GameScreen.PLAYING,
                bombHolderId = firstHolder,
                timerSeconds = bombTime,
                totalTime = bombTime,
                isVibrating = firstHolder == it.myPlayerId
            )
        }

        if (_state.value.bombHolderId == _state.value.myPlayerId) {
            startVibration()
        }

        startTimer()
    }

    // ── Timer ──

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.timerSeconds > 0) {
                delay(1000L)
                _state.update { it.copy(timerSeconds = it.timerSeconds - 1) }
            }
            // Bomba explota
            onBombExplode()
        }
    }

    private fun onBombExplode() {
        stopVibration()
        onExplosionSound?.invoke()   // 💥 Reproducir sonido de explosión
        val holder = _state.value.players.find { it.id == _state.value.bombHolderId }
        val updatedPlayers = _state.value.players.map { p ->
            if (p.id == _state.value.bombHolderId) p.copy(isAlive = false) else p
        }
        _state.update {
            it.copy(
                screen = GameScreen.EXPLODED,
                players = updatedPlayers,
                eliminatedPlayerId = _state.value.bombHolderId,
                eliminatedPlayerName = holder?.name ?: "Desconocido",
                isVibrating = false
            )
        }
    }

    // ── Pasar la bomba ──

    fun openPassBomb() {
        _state.update { it.copy(screen = GameScreen.PASS_BOMB) }
    }

    fun passBombTo(targetId: Int) {
        stopVibration()
        _state.update {
            it.copy(
                screen = GameScreen.PLAYING,
                bombHolderId = targetId,
                isVibrating = targetId == it.myPlayerId
            )
        }
        if (_state.value.isVibrating) startVibration()
    }

    // ── Shake (agitar) ──

    fun onShakeDetected() {
        val current = _state.value
        if (current.screen != GameScreen.PLAYING) return
        if (current.bombHolderId != current.myPlayerId) return
        // Agitar = abrir selector de a quién pasar
        openPassBomb()
    }

    // ── Vibración ──

    private fun startVibration() {
        vibrateJob?.cancel()
        vibrateJob = viewModelScope.launch {
            while (true) {
                onVibrate?.invoke()
                delay(600L)
            }
        }
        _state.update { it.copy(isVibrating = true) }
    }

    private fun stopVibration() {
        vibrateJob?.cancel()
        onStopVibrate?.invoke()
        _state.update { it.copy(isVibrating = false) }
    }

    // ── Continuar ronda o ver resultados ──

    fun continueAfterExplosion() {
        val alive = _state.value.players.filter { it.isAlive }
        if (alive.size <= 1) {
            _state.update { it.copy(screen = GameScreen.RESULTS) }
        } else {
            val nextBombTime = Random.nextInt(10, 31)
            val nextHolder = alive.random().id
            _state.update {
                it.copy(
                    screen = GameScreen.PLAYING,
                    round = it.round + 1,
                    bombHolderId = nextHolder,
                    timerSeconds = nextBombTime,
                    totalTime = nextBombTime,
                    isVibrating = nextHolder == it.myPlayerId
                )
            }
            if (_state.value.isVibrating) startVibration()
            startTimer()
        }
    }

    fun restartGame() {
        timerJob?.cancel()
        stopVibration()
        _state.update { GameState() }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        vibrateJob?.cancel()
    }
}
