package com.azharkova.musicplayer.ui.views

import androidx.lifecycle.ViewModel
import com.azharkova.musicplayer.KoinDIFactory
import com.azharkova.musicplayer.data.SongItemModel
import com.azharkova.musicplayer.data.toModel
import com.azharkova.musicplayer.resolve
import com.azharkova.musicplayer.service.MusicService
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class SongsListViewModel : ViewModel() {

    private val service = KoinDIFactory.resolve<MusicService>(MusicService::class)

    val data: MutableSharedFlow<List<SongItemModel>> =
        MutableSharedFlow(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun loadData() {
       service?.setupData()
       data.tryEmit(service?.playList.orEmpty().map { it.toModel() })
    }
}