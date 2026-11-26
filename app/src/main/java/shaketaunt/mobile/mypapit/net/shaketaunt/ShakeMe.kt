/*
 * Copyright 2018-2026 Mohammad Hafiz bin Ismail <mypapit@gmail.com>
 * http://blog.mypapit.net
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package shaketaunt.mobile.mypapit.net.shaketaunt

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.core.net.toUri
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import shaketaunt.mobile.mypapit.net.shaketaunt.sensor.AccelerometerShakeDetector
import shaketaunt.mobile.mypapit.net.shaketaunt.speech.TauntSpeaker
import shaketaunt.mobile.mypapit.net.shaketaunt.ui.ShakeTauntScreen
import shaketaunt.mobile.mypapit.net.shaketaunt.ui.ShakeTauntViewModel
import shaketaunt.mobile.mypapit.net.shaketaunt.ui.TauntEvent
import shaketaunt.mobile.mypapit.net.shaketaunt.ui.theme.ShakeTaunterTheme

class ShakeMe : ComponentActivity() {

    private val viewModel: ShakeTauntViewModel by lazy {
        ViewModelProvider(this)[ShakeTauntViewModel::class.java]
    }

    private lateinit var speaker: TauntSpeaker
    private lateinit var shakeDetector: AccelerometerShakeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        speaker = TauntSpeaker(this).apply { initialize() }

        val sensorManager = getSystemService(SENSOR_SERVICE) as? SensorManager
        shakeDetector = AccelerometerShakeDetector(
            sensorManager = sensorManager,
            onShake = {
                window.decorView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                viewModel.onShakeDetected()
            },
        )
        viewModel.onAccelerometerAvailable(shakeDetector.isAvailable)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is TauntEvent.Speak -> speaker.speak(event.text)
                        TauntEvent.StopSpeech -> speaker.stop()
                    }
                }
            }
        }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ShakeTaunterTheme {
                ShakeTauntScreen(
                    uiState = uiState,
                    onManualTaunt = viewModel::onManualTaunt,
                    onReplayTaunt = viewModel::onReplayTaunt,
                    onToggleMute = viewModel::onToggleMute,
                    onShowAbout = viewModel::onShowAbout,
                    onDismissAbout = viewModel::onDismissAbout,
                    onOpenGitHub = ::openGitHubPage,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.start()
    }

    override fun onPause() {
        shakeDetector.stop()
        speaker.stop()
        super.onPause()
    }

    override fun onDestroy() {
        speaker.shutdown()
        super.onDestroy()
    }

    private fun openGitHubPage() {
        val intent = Intent(Intent.ACTION_VIEW, GITHUB_URL.toUri())
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Log.w(TAG, "No browser available to open $GITHUB_URL", ex)
        }
    }

    companion object {
        private const val TAG = "ShakeMe"
        private const val GITHUB_URL = "https://github.com/mypapit/"
    }
}
