package shaketaunt.mobile.mypapit.net.shaketaunt.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Lifecycle-safe wrapper around Android's [TextToSpeech] engine that initializes once, queues a
 * pending utterance until initialization finishes, and shuts down cleanly.
 */
class TauntSpeaker(context: Context) : TextToSpeech.OnInitListener {
    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isReady = false
    private var pendingText: String? = null
    private var utteranceCounter = 0L

    fun initialize() {
        if (isInitialized) return
        isInitialized = true
        tts = TextToSpeech(appContext, this)
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            isReady = false
            pendingText = null
            return
        }

        val engine = tts ?: return
        val usResult = engine.setLanguage(Locale.US)
        if (usResult == TextToSpeech.LANG_MISSING_DATA ||
            usResult == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            engine.setLanguage(Locale.getDefault())
        }

        isReady = true
        val queued = pendingText
        pendingText = null
        if (queued != null) {
            speakInternal(queued)
        }
    }

    fun speak(text: String) {
        if (!isInitialized) {
            initialize()
        }
        if (!isReady) {
            pendingText = text
            return
        }
        speakInternal(text)
    }

    fun stop() {
        pendingText = null
        tts?.stop()
    }

    fun shutdown() {
        pendingText = null
        isReady = false
        isInitialized = false
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    private fun speakInternal(text: String) {
        val utteranceId = "shake_taunt_${utteranceCounter++}"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }
}
