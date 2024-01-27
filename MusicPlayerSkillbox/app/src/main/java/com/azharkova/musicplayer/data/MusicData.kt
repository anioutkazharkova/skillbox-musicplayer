package com.azharkova.musicplayer.data

import com.azharkova.musicplayer.R

fun getPlayList(): List<Music> {
    return listOf(
        Music(
            name = "Master Of Puppets",
            artist = "Metallica",
            cover = R.drawable.master_of_puppets_album_cover,
            music = R.raw.master_of_puppets
        ),
        Music(
            name = "Everyday Normal Guy 2",
            artist = "Jon Lajoie",
            cover = R.drawable.everyday_normal_guy_2_album_cover,
            music = R.raw.everyday_normal_guy_2
        ),
        Music(
            name = "Lose Yourself",
            artist = "Eminem",
            cover = R.drawable.lose_yourself_album_cover,
            music = R.raw.lose_yourself
        ),
        Music(
            name = "Crazy",
            artist = "Gnarls Barkley",
            cover = R.drawable.crazy_album_cover,
            music = R.raw.crazy
        ),
        Music(
            name = "Till I Collapse",
            artist = "Eminem",
            cover = R.drawable.till_i_collapse_album_cover,
            music = R.raw.till_i_collapse
        ),
    )
}