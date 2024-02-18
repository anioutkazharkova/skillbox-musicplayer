package com.azharkova.musicplayer.service

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.azharkova.musicplayer.data.SongItem
import com.azharkova.musicplayer.data.getPlayList
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Музыкальный сервис
 * */
class MusicService constructor(context: Context) {
    private lateinit var player: ExoPlayer

    private var currentSong: MutableSharedFlow<SongItem>
    = MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

    private var isSetup: Boolean = false

    var selectedIndex: Int = -1
        set(value) {
            if (value >=0 && value < playList.size) {
                field = value
                currentSong.tryEmit(playList[value])
            }
        }

    private val packageName: String = context.packageName

    val playList by lazy {
        getPlayList()
    }

    var duration: Long = 0L
        get() = player.duration

    var sliderPosition: Long = 0L
        get() = player.currentPosition

    var isPlaying: Boolean = false
        get() = player.isPlaying

    init {
        setupPlayer(context)
        setupData()
        prepare()
    }

    private fun setupPlayer(context: Context) {
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
        if (selectedIndex == -1) {
            selectedIndex = position
            player.seekTo(selectedIndex, 0)
        }
    }

    fun play() {
        if (selectedIndex == -1 && playList.isNotEmpty()) {
            selectedIndex = 0
            player.seekTo(0,0)
        }
        player.play()
    }

    fun stop() {
        player.pause()
    }

    private fun prepare() {
        player.prepare()
    }

    fun seekTo(positionMs: Long) {
        if (isPlaying) {
            player.seekTo(positionMs)
        }
    }

    fun playStopIfNeed(position: Int) {
        if (position != selectedIndex) {
            selectedIndex = position
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
        if (selectedIndex < playList.count() - 1 && playList.isNotEmpty()) {
            selectedIndex ++
        }
        return selectedIndex
    }

    fun goPrev(): Int {
        player.seekToPreviousMediaItem()
        if (selectedIndex > 0 && playList.isNotEmpty()) {
            selectedIndex --
        }
        return selectedIndex
    }
}