package shaketaunt.mobile.mypapit.net.shaketaunt.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import kotlin.math.sqrt

/**
 * Pure threshold-and-window evaluator for accelerometer samples so shake detection can be unit-tested
 * without Android framework dependencies.
 */
class ShakeThresholdEvaluator(
    private val thresholdG: Float = DEFAULT_THRESHOLD_G,
    private val minHits: Int = DEFAULT_MIN_HITS,
    private val windowMs: Long = DEFAULT_WINDOW_MS,
    private val cooldownMs: Long = DEFAULT_COOLDOWN_MS,
) {
    private val hitTimestamps = ArrayDeque<Long>()
    private var lastTriggerMs: Long = Long.MIN_VALUE

    fun onSample(x: Float, y: Float, z: Float, timestampMs: Long): Boolean {
        if (lastTriggerMs != Long.MIN_VALUE && timestampMs - lastTriggerMs < cooldownMs) {
            return false
        }

        val gx = x / GRAVITY_EARTH_MPS2
        val gy = y / GRAVITY_EARTH_MPS2
        val gz = z / GRAVITY_EARTH_MPS2
        val gForce = sqrt(gx * gx + gy * gy + gz * gz)

        if (gForce < thresholdG) {
            return false
        }

        while (hitTimestamps.isNotEmpty() && timestampMs - hitTimestamps.first() > windowMs) {
            hitTimestamps.removeFirst()
        }
        hitTimestamps.addLast(timestampMs)

        if (hitTimestamps.size >= minHits) {
            hitTimestamps.clear()
            lastTriggerMs = timestampMs
            return true
        }
        return false
    }

    fun reset() {
        hitTimestamps.clear()
        lastTriggerMs = Long.MIN_VALUE
    }

    companion object {
        const val GRAVITY_EARTH_MPS2 = 9.80665f
        const val DEFAULT_THRESHOLD_G = 2.15f
        const val DEFAULT_MIN_HITS = 2
        const val DEFAULT_WINDOW_MS = 400L
        const val DEFAULT_COOLDOWN_MS = 700L
    }
}

/**
 * Lifecycle-managed accelerometer shake detector backed by [SensorManager].
 */
class AccelerometerShakeDetector(
    private val sensorManager: SensorManager?,
    private val onShake: () -> Unit,
    private val evaluator: ShakeThresholdEvaluator = ShakeThresholdEvaluator(),
    private val clockMs: () -> Long = { SystemClock.elapsedRealtime() },
) : SensorEventListener {

    private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var isListening = false

    val isAvailable: Boolean
        get() = accelerometer != null

    fun start(): Boolean {
        val sensor = accelerometer ?: return false
        if (isListening) return true
        val registered = sensorManager?.registerListener(
            this,
            sensor,
            SensorManager.SENSOR_DELAY_UI,
        ) ?: false
        isListening = registered
        return registered
    }

    fun stop() {
        if (isListening) {
            sensorManager?.unregisterListener(this)
            isListening = false
        }
        evaluator.reset()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER || event.values.size < 3) {
            return
        }
        val triggered = evaluator.onSample(
            x = event.values[0],
            y = event.values[1],
            z = event.values[2],
            timestampMs = clockMs(),
        )
        if (triggered) {
            onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op: shake detection relies on relative g-force impulses.
    }
}
