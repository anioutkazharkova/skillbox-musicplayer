package com.azharkova.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.exoplayer.ExoPlayer
import com.azharkova.musicplayer.data.getPlayList
import com.azharkova.musicplayer.service.MusicService
import com.azharkova.musicplayer.ui.theme.MusicPlayerTheme
import com.azharkova.musicplayer.ui.views.SongScreen
import com.azharkova.musicplayer.ui.views.SongsListView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MusicService.instance.setupIfNeed(context = this)
        MusicService.instance.setupData()
        MusicService.instance.prepare()
        setContent {
            MusicPlayerTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SongsApp()
                }

            }
        }
    }

    companion object {
        lateinit var player: ExoPlayer
    }
}
