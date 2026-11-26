package shaketaunt.mobile.mypapit.net.shaketaunt.sensor

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShakeThresholdEvaluatorTest {

    @Test
    fun restingGravityDoesNotTriggerShake() {
        val evaluator = ShakeThresholdEvaluator()

        val first = evaluator.onSample(0f, ShakeThresholdEvaluator.GRAVITY_EARTH_MPS2, 0f, 100L)
        val second = evaluator.onSample(0f, ShakeThresholdEvaluator.GRAVITY_EARTH_MPS2, 0f, 200L)

        assertFalse(first)
        assertFalse(second)
    }

    @Test
    fun singleImpulseDoesNotTriggerWhenTwoHitsRequired() {
        val evaluator = ShakeThresholdEvaluator(thresholdG = 2.15f, minHits = 2)

        val triggered = evaluator.onSample(25f, 0f, 0f, 100L)

        assertFalse(triggered)
    }

    @Test
    fun twoImpulsesWithinWindowTriggerOnce() {
        val evaluator = ShakeThresholdEvaluator(
            thresholdG = 2.15f,
            minHits = 2,
            windowMs = 400L,
            cooldownMs = 700L,
        )

        assertFalse(evaluator.onSample(25f, 0f, 0f, 100L))
        assertTrue(evaluator.onSample(25f, 0f, 0f, 250L))
    }

    @Test
    fun cooldownSuppressesImmediateRetriggerAndAllowsAfterCooldown() {
        val evaluator = ShakeThresholdEvaluator(
            thresholdG = 2.15f,
            minHits = 2,
            windowMs = 400L,
            cooldownMs = 700L,
        )

        assertFalse(evaluator.onSample(25f, 0f, 0f, 100L))
        assertTrue(evaluator.onSample(25f, 0f, 0f, 250L))

        // Inside cooldown window (250 + 700 = 950)
        assertFalse(evaluator.onSample(25f, 0f, 0f, 500L))
        assertFalse(evaluator.onSample(25f, 0f, 0f, 650L))

        // After cooldown expires
        assertFalse(evaluator.onSample(25f, 0f, 0f, 1000L))
        assertTrue(evaluator.onSample(25f, 0f, 0f, 1150L))
    }

    @Test
    fun expiredHitsOutsideWindowDoNotAccumulate() {
        val evaluator = ShakeThresholdEvaluator(
            thresholdG = 2.15f,
            minHits = 2,
            windowMs = 400L,
            cooldownMs = 700L,
        )

        assertFalse(evaluator.onSample(25f, 0f, 0f, 100L))
        // 600ms > 400ms window, so the 100ms hit expires
        assertFalse(evaluator.onSample(25f, 0f, 0f, 700L))
        // Next hit within 400ms of 700ms triggers
        assertTrue(evaluator.onSample(25f, 0f, 0f, 900L))
    }
}
