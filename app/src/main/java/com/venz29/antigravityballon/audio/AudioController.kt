package com.venz29.antigravityballon.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.venz29.antigravityballon.R

class AudioController(private val context: Context) {

    private val soundPool: SoundPool
    private val loadedSounds = mutableMapOf<Int, Boolean>()

    private var coinSoundId = 0
    private var bumpSoundId = 0
    private var clickSoundId = 0
    private var unlockSoundId = 0
    private var gameOverSoundId = 0

    private var mediaPlayer: MediaPlayer? = null

    var sfxEnabled: Boolean = true
    var musicEnabled: Boolean = true
        set(value) {
            field = value
            if (value) {
                resumeMusic()
            } else {
                pauseMusic()
            }
        }

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            loadedSounds[sampleId] = (status == 0)
        }

        try {
            coinSoundId = soundPool.load(context, R.raw.coin, 1)
            bumpSoundId = soundPool.load(context, R.raw.bump, 1)
            clickSoundId = soundPool.load(context, R.raw.click, 1)
            unlockSoundId = soundPool.load(context, R.raw.unlock, 1)
            gameOverSoundId = soundPool.load(context, R.raw.gameover, 1)
        } catch (e: Exception) {
            Log.w("AudioController", "Failed loading sound effects", e)
        }

        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.bgm_playful)?.apply {
                isLooping = true
                setVolume(0.55f, 0.55f)
            }
        } catch (e: Exception) {
            Log.w("AudioController", "Failed initializing background music player", e)
        }
    }

    fun playCoin() = playSound(coinSoundId, 0.9f)
    fun playBump() = playSound(bumpSoundId, 1.0f)
    fun playClick() = playSound(clickSoundId, 0.7f)
    fun playUnlock() = playSound(unlockSoundId, 1.0f)
    fun playGameOver() = playSound(gameOverSoundId, 0.9f)

    private fun playSound(soundId: Int, volume: Float) {
        if (!sfxEnabled || soundId == 0) return
        try {
            if (loadedSounds[soundId] == true) {
                soundPool.play(soundId, volume, volume, 1, 0, 1.0f)
            }
        } catch (e: Exception) {
            Log.w("AudioController", "Error playing sound", e)
        }
    }

    fun startMusic() {
        if (!musicEnabled) return
        try {
            if (mediaPlayer == null) {
                initMediaPlayer()
            }
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            Log.w("AudioController", "Error starting background music", e)
        }
    }

    fun pauseMusic() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            Log.w("AudioController", "Error pausing background music", e)
        }
    }

    fun resumeMusic() {
        if (!musicEnabled) return
        try {
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            Log.w("AudioController", "Error resuming background music", e)
        }
    }

    fun release() {
        try {
            soundPool.release()
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.w("AudioController", "Error releasing audio", e)
        }
    }
}
