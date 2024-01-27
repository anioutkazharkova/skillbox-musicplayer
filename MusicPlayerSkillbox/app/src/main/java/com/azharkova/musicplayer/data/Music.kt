package com.azharkova.musicplayer.data

/***
 * Data class to represent a music in the list
 */
data class Music(
    val name: String,
    val artist: String,
    val music: Int,
    val cover: Int,
)