package com.azharkova.musicplayer

import androidx.media3.exoplayer.ExoPlayer
import com.azharkova.musicplayer.player.MyPlayer
import com.azharkova.musicplayer.service.MusicService
import com.azharkova.musicplayer.ui.views.MusicManager
import com.azharkova.musicplayer.ui.views.SongItemViewModel
import com.azharkova.musicplayer.ui.views.SongsListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module
import kotlin.reflect.KClass

class KoinDI : KoinComponent {
    val serviceModule = module {
        single { MyPlayer(get()) }
        single { MusicManager(get(), get()) }
        single { MusicService() }
    }


    val vmModule = module {
        factory<SongsListViewModel> { SongsListViewModel() }
        factory<SongItemViewModel> {SongItemViewModel()}
    }

    fun start() = startKoin {
        androidContext(MainApplication.context)
        modules(listOf(serviceModule, vmModule))
    }
}

object KoinDIFactory {
    val di by lazy {
        KoinDI().apply {
            start()
        }
    }
}

fun<T:Any> KoinDIFactory.resolve(clazz: KClass<*>):T? {
    return di.getKoin().get(clazz)
}