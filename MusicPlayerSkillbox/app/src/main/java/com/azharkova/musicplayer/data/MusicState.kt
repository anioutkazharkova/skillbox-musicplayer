package com.azharkova.musicplayer.data

data class MusicState(
    val currentPosition: Int = -1,
    val playerPosition: Int = -1,
    val isPlaying: Boolean
)