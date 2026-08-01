package com.neurix.core.service.wakeword

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PorcupineWakeWordEngine @Inject constructor(
    @ApplicationContext private val context: Context
) : WakeWordEngine {
    private var isActive = false
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val wakePhrases = listOf("hey neurix", "hi neurix", "hey assistant")

    override fun startListening(onWakeWord: () -> Unit, onError: (String) -> Unit) {
        if (isActive) return
        isActive = true
        job = scope.launch {
            try {
                val size = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
                val rec = AudioRecord(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, size * 2)
                rec.startRecording()
                val buf = ShortArray(size)
                while (isActiveOrJobCancelled()) {
                    val r = rec.read(buf, 0, size)
                    if (r > 0) {
                        val energy = buf.take(r).map { (it * it).toLong() }.sum() / r
                        if (energy > 500000L) {
                            launch(Dispatchers.Main) { onWakeWord() }
                        }
                    }
                }
                rec.stop(); rec.release()
            } catch (e: SecurityException) {
                launch(Dispatchers.Main) { onError("Mic permission required") }
            } catch (e: Exception) {
                launch(Dispatchers.Main) { onError(e.message ?: "Wake word error") }
            }
        }
    }

    private fun isActiveOrJobCancelled(): Boolean = isActive && job?.isActive == true
    override fun stopListening() { isActive = false; job?.cancel() }
    override fun isActive(): Boolean = isActive
    override fun destroy() { isActive = false; job?.cancel(); scope.cancel() }
}
