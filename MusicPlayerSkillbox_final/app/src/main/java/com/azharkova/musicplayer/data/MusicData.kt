package com.azharkova.musicplayer.data

import com.azharkova.musicplayer.R

fun getPlayList(): List<SongItem> {
    return listOf(
        SongItem(
            name = "Bohemian Rhapsody",
            artist = "Queen",
            cover = R.drawable.bohemian,
            music = R.raw.bohemian
        ),
        SongItem(
            name = "Let it be",
            artist = "The Beatles",
            cover = R.drawable.let_it_be,
            music = R.raw.let_it_be
        ),
        SongItem(
            name = "Gimme, gimme!",
            artist = "ABBA",
            cover = R.drawable.gimme,
            music = R.raw.gimme
        )
    )
}