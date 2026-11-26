package shaketaunt.mobile.mypapit.net.shaketaunt.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import shaketaunt.mobile.mypapit.net.shaketaunt.data.TauntRepository

data class ShakeTauntUiState(
    val currentTaunt: String? = null,
    val shakeCount: Int = 0,
    val isMuted: Boolean = false,
    val isAboutDialogVisible: Boolean = false,
    val hasAccelerometer: Boolean = true,
    val tauntGeneration: Int = 0,
)

sealed interface TauntEvent {
    data class Speak(val text: String) : TauntEvent
    data object StopSpeech : TauntEvent
}

class ShakeTauntViewModel(
    private val repository: TauntRepository = TauntRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShakeTauntUiState())
    val uiState: StateFlow<ShakeTauntUiState> = _uiState.asStateFlow()

    private val _events = Channel<TauntEvent>(Channel.BUFFERED)
    val events: Flow<TauntEvent> = _events.receiveAsFlow()

    fun onShakeDetected() {
        triggerTaunt()
    }

    fun onManualTaunt() {
        triggerTaunt()
    }

    fun onReplayTaunt() {
        val state = _uiState.value
        val taunt = state.currentTaunt ?: return
        if (!state.isMuted) {
            _events.trySend(TauntEvent.Speak(taunt))
        }
    }

    fun onToggleMute() {
        var nowMuted = false
        _uiState.update { current ->
            nowMuted = !current.isMuted
            current.copy(isMuted = nowMuted)
        }
        if (nowMuted) {
            _events.trySend(TauntEvent.StopSpeech)
        }
    }

    fun onShowAbout() {
        _uiState.update { it.copy(isAboutDialogVisible = true) }
    }

    fun onDismissAbout() {
        _uiState.update { it.copy(isAboutDialogVisible = false) }
    }

    fun onAccelerometerAvailable(available: Boolean) {
        _uiState.update { it.copy(hasAccelerometer = available) }
    }

    private fun triggerTaunt() {
        var spokenTaunt: String? = null
        var shouldSpeak = false
        _uiState.update { current ->
            val nextTaunt = repository.nextTaunt(previousTaunt = current.currentTaunt)
            spokenTaunt = nextTaunt
            shouldSpeak = !current.isMuted
            current.copy(
                currentTaunt = nextTaunt,
                shakeCount = current.shakeCount + 1,
                tauntGeneration = current.tauntGeneration + 1,
            )
        }
        val text = spokenTaunt
        if (shouldSpeak && text != null) {
            _events.trySend(TauntEvent.Speak(text))
        }
    }
}
