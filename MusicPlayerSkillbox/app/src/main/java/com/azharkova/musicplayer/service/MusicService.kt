package com.azharkova.musicplayer.service

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.azharkova.musicplayer.MainActivity
import com.azharkova.musicplayer.data.SongItem
import com.azharkova.musicplayer.data.getPlayList
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class MusicService {
    lateinit var player: ExoPlayer

    var currentSong: MutableSharedFlow<SongItem> = MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

    private var isSetup: Boolean = false

    var currentPosition: Int = -1
        set(value) {
            if (value >=0 && value < playList.size) {
                field = value
                currentSong.tryEmit(playList[value])
            }
        }

    private var packageName: String = ""

    val playList by lazy {
        getPlayList()
    }

    var duration: Long = 0L
        get() = player.duration

    var sliderPosition: Long = 0L
        get() = player.currentPosition

    var isPlaying: Boolean = false
        get() = player.isPlaying

    fun setupIfNeed(context: Context) {
        setupPlayer(context)
    }
    private fun setupPlayer(context: Context) {
        packageName = context.packageName
        player = ExoPlayer.Builder(context).build()
        player.playWhenReady = false
    }

    fun setupData() {
        if (!isSetup) {
            playList.forEach {
                val path = "android.resource://" + packageName + "/" + it.music
                val mediaItem = MediaItem.fromUri(Uri.parse(path))
                player.addMediaItem(mediaItem)
            }
            isSetup = true
        }
    }

    fun setupPosition(position: Int) {
        if (currentPosition == -1) {
            currentPosition = position
            player.seekTo(currentPosition,0)
        }
    }

    fun play() {
        if (currentPosition == -1 && playList.isNotEmpty()) {
            currentPosition = 0
            player.seekTo(0,0)
        }
        player.play()
    }

    fun stop() {
        player.pause()
    }

    fun prepare() {
        player.prepare()
    }

    fun seekTo(positionMs: Long) {
        if (isPlaying) {
            player.seekTo(positionMs)
        }
    }

    fun playStopIfNeed(position: Int) {
        if (position != currentPosition) {
            currentPosition = position
            player.seekTo(position, 0)
            player.playWhenReady = true
        } else {
            if (isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    fun goNext(): Int {
        player.seekToNextMediaItem()
        if (currentPosition < playList.count() - 1 && playList.isNotEmpty()) {
            currentPosition ++
        }
        return currentPosition
    }

    fun goPrev(): Int {
        player.seekToPreviousMediaItem()
        if (currentPosition > 0 && playList.isNotEmpty()) {
            currentPosition --
        }
        return currentPosition
    }

    companion object {
        var instance: MusicService = MusicService()
    }
}