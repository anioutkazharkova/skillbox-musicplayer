package com.azharkova.musicplayer.data

import android.os.Parcelable
import com.azharkova.musicplayer.player.PlayerStates
import kotlinx.parcelize.Parcelize
/***
 * Data class to represent a music in the list
 */
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


