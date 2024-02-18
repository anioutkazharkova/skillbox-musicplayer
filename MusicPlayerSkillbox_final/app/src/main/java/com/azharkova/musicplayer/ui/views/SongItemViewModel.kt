package com.azharkova.musicplayer.ui.views

import androidx.lifecycle.ViewModel
import com.azharkova.musicplayer.KoinDIFactory
import com.azharkova.musicplayer.resolve
import com.azharkova.musicplayer.service.MusicService

class SongItemViewModel: ViewModel() {
    val service: MusicService? = KoinDIFactory.resolve<MusicService>(MusicService::class)
}