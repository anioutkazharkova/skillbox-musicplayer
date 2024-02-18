package com.azharkova.musicplayer.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azharkova.musicplayer.KoinDIFactory
import com.azharkova.musicplayer.data.SongItem
import com.azharkova.musicplayer.data.SongItemModel
import com.azharkova.musicplayer.data.getPlayList
import com.azharkova.musicplayer.resolve
import com.azharkova.musicplayer.service.MusicService
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SongItemView(item: SongItemModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp, 8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        GlideImage(model = item.cover,
                   contentDescription = "image",
                   contentScale = ContentScale.FillWidth,
                   modifier = Modifier
                       .width(120.dp)
                       .height(120.dp)
                       .padding(0.dp, 0.dp, 0.dp, 0.dp))
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp, 0.dp, 0.dp)) {
            Text(text = item.artist, style = MaterialTheme.typography.titleSmall)
            Text(text = item.name, style = MaterialTheme.typography.titleMedium, maxLines = 3)
        }

    }
}

class SongsListViewModel : ViewModel() {

    val service = KoinDIFactory.resolve<MusicManager>(MusicManager::class)

    val data:MutableSharedFlow<List<SongItemModel>> = MutableSharedFlow(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun loadData() {
        viewModelScope.launch {
            service?.songs?.collectLatest {
                data.tryEmit(it)
            }
        }
    }
}

class SongItemViewModel: ViewModel() {
    val service: MusicService? = KoinDIFactory.resolve<MusicService>(MusicService::class)
}

@Composable
fun SongsListView(vm: SongsListViewModel, navigate: (Int)->Unit) {
    val scope = rememberCoroutineScope()

    val data by vm.data.asSharedFlow().collectAsState(emptyList())
    LaunchedEffect(Unit) {
        vm.loadData()
    }
    LazyColumn(
        contentPadding = // 1.
        PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(data) {
            SongItemView(it,  modifier = Modifier.clickable {
                navigate.invoke(data.indexOf(it))
            })
        }
    }
}