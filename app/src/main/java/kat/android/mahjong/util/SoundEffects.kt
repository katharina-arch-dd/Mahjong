package kat.android.mahjong.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

object SoundEffects {
    private var toneGen: ToneGenerator? = null

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
        } catch (_: Exception) {
            toneGen = null
        }
    }

    fun playClick() {
        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
    }

    fun playMatch() {
        toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 80)
    }

    fun playError() {
        toneGen?.startTone(ToneGenerator.TONE_PROP_NACK, 100)
    }

    fun playWin() {
        toneGen?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 300)
    }
}
