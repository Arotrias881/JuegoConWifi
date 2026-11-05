package com.example.proyectodejuegowifi

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

/**
 * Gestiona los efectos de sonido del juego.
 * Usar release() en onDestroy de la Activity para liberar recursos.
 */
class SoundManager(private val context: Context) {

    private var explosionPlayer: MediaPlayer? = null

    fun playExplosion() {
        // Liberar instancia anterior si aún existe
        explosionPlayer?.release()

        explosionPlayer = MediaPlayer.create(context, R.raw.explosion)?.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setOnCompletionListener { mp ->
                mp.release()
                explosionPlayer = null
            }
            start()
        }
    }

    fun release() {
        explosionPlayer?.release()
        explosionPlayer = null
    }
}
