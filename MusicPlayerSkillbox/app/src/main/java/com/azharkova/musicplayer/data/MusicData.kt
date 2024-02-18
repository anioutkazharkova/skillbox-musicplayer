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
        ),
       /* SongItem(
            name = "Master Of Puppets",
            artist = "Metallica",
            cover = R.drawable.master_of_puppets_album_cover,
            music = R.raw.master_of_puppets
        ),
        SongItem(
            name = "Everyday Normal Guy 2",
            artist = "Jon Lajoie",
            cover = R.drawable.everyday_normal_guy_2_album_cover,
            music = R.raw.everyday_normal_guy_2
        ),
        SongItem(
            name = "Lose Yourself",
            artist = "Eminem",
            cover = R.drawable.lose_yourself_album_cover,
            music = R.raw.lose_yourself
        ),
        SongItem(
            name = "Crazy",
            artist = "Gnarls Barkley",
            cover = R.drawable.crazy_album_cover,
            music = R.raw.crazy
        ),
        SongItem(
            name = "Till I Collapse",
            artist = "Eminem",
            cover = R.drawable.till_i_collapse_album_cover,
            music = R.raw.till_i_collapse
        ),*/
    )
}