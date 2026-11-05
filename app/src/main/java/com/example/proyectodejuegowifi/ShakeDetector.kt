package com.example.proyectodejuegowifi

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Detecta cuando el usuario agita el dispositivo usando el acelerómetro.
 * Umbral configurado en ~2.7g para evitar falsos positivos.
 */
class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

    private val shakeThreshold = 2.7f * SensorManager.GRAVITY_EARTH
    private var lastShakeTime = 0L
    private val cooldown = 1000L // ms entre sacudidas

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val magnitude = sqrt(x * x + y * y + z * z)

        val now = System.currentTimeMillis()
        if (magnitude > shakeThreshold && (now - lastShakeTime) > cooldown) {
            lastShakeTime = now
            onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
}
