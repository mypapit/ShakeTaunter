package shaketaunt.mobile.mypapit.net.shaketaunt.ui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import shaketaunt.mobile.mypapit.net.shaketaunt.data.TauntRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ShakeTauntViewModelTest {

    private fun createViewModel(): ShakeTauntViewModel {
        val repository = TauntRepository(
            phrases = listOf("fabulous", "witty", "silly!"),
            nextIndex = { 0 },
        )
        return ShakeTauntViewModel(repository)
    }

    @Test
    fun initialStateHasNoTauntAndZeroCount() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertNull(state.currentTaunt)
        assertEquals(0, state.shakeCount)
        assertFalse(state.isMuted)
        assertFalse(state.isAboutDialogVisible)
        assertTrue(state.hasAccelerometer)
    }

    @Test
    fun shakeUpdatesTauntCountAndEmitsSpeakEvent() = runTest {
        val viewModel = createViewModel()
        val events = mutableListOf<TauntEvent>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { events.add(it) }
        }

        viewModel.onShakeDetected()

        val state = viewModel.uiState.value
        assertEquals("You are fabulous", state.currentTaunt)
        assertEquals(1, state.shakeCount)
        assertEquals(1, state.tauntGeneration)
        assertEquals(listOf(TauntEvent.Speak("You are fabulous")), events)

        job.cancel()
    }

    @Test
    fun toggleMuteEmitsStopSpeechAndSuppressesSpeechEvents() = runTest {
        val viewModel = createViewModel()
        val events = mutableListOf<TauntEvent>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { events.add(it) }
        }

        viewModel.onToggleMute()
        assertTrue(viewModel.uiState.value.isMuted)
        assertEquals(listOf(TauntEvent.StopSpeech), events)

        viewModel.onManualTaunt()
        assertEquals("You are fabulous", viewModel.uiState.value.currentTaunt)
        assertEquals(1, viewModel.uiState.value.shakeCount)
        // Still only StopSpeech; Speak was suppressed while muted
        assertEquals(listOf(TauntEvent.StopSpeech), events)

        viewModel.onReplayTaunt()
        assertEquals(listOf(TauntEvent.StopSpeech), events)

        viewModel.onToggleMute()
        assertFalse(viewModel.uiState.value.isMuted)
        viewModel.onReplayTaunt()
        assertEquals(
            listOf(TauntEvent.StopSpeech, TauntEvent.Speak("You are fabulous")),
            events,
        )

        job.cancel()
    }

    @Test
    fun aboutDialogTogglesVisibility() {
        val viewModel = createViewModel()

        viewModel.onShowAbout()
        assertTrue(viewModel.uiState.value.isAboutDialogVisible)

        viewModel.onDismissAbout()
        assertFalse(viewModel.uiState.value.isAboutDialogVisible)
    }
}
