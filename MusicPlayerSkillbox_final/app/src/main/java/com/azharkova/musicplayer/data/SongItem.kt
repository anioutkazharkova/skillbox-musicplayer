package com.azharkova.musicplayer.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SongItem(
    val name: String,
    val artist: String,
    val music: Int,
    val cover: Int,
): Parcelable


@Parcelize
data class SongItemModel(
    val name: String,
    val artist: String,
    val music: Int,
    val cover: Int,
    var isSelected: Boolean = false,
    var state: PlayerStates = PlayerStates.STATE_IDLE
): Parcelable

fun SongItem.toModel(): SongItemModel {
    return SongItemModel(
        name, artist, music, cover
    )
}

enum class PlayerStates {
    /**
     * State when the player is idle, not ready to play.
     */
    STATE_IDLE,

    /**
     * State when the player is ready to start playback.
     */
    STATE_READY,

    /**
     * State when the player is buffering content.
     */
    STATE_BUFFERING,

    /**
     * State when the player has encountered an error.
     */
    STATE_ERROR,

    /**
     * State when the playback has ended.
     */
    STATE_END,

    /**
     * State when the player is actively playing content.
     */
    STATE_PLAYING,

    /**
     * State when the player has paused the playback.
     */
    STATE_PAUSE,

    /**
     * State when the player has moved to the next track.
     */
    STATE_NEXT_TRACK
}



