package com.azharkova.musicplayer.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.azharkova.musicplayer.KoinDIFactory
import com.azharkova.musicplayer.R
import com.azharkova.musicplayer.ext.convertToText
import com.azharkova.musicplayer.resolve
import com.azharkova.musicplayer.service.MusicService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun SongItemScreen(position: Int) {
    var service = KoinDIFactory.resolve<MusicService>(MusicService::class)!!
    LaunchedEffect(Unit) {
        service.setupPosition(position)
    }
    val currentIndex = remember {
        mutableIntStateOf(position)
    }
    val song = MutableStateFlow(service.playList[currentIndex.value]).collectAsState()

    val isPlaying = remember {
        mutableStateOf(false)
    }

    val currentSliderPosition = remember {
        mutableLongStateOf(0)
    }

    val sliderPosition = remember {
        mutableLongStateOf(0)
    }

    val totalDuration = remember {
        mutableLongStateOf(0)
    }

    fun isCurrent(): Boolean = currentIndex.value == service.selectedIndex

    LaunchedEffect(key1 = service.sliderPosition, key2 = isPlaying.value) {
        delay(1000)
        if (isCurrent()) {
            currentSliderPosition.longValue = service.sliderPosition
        }
    }

    LaunchedEffect(currentSliderPosition.longValue) {
        if (isCurrent()) {
            sliderPosition.longValue = currentSliderPosition.longValue
        }
    }

    LaunchedEffect(service.duration) {
        if (service.duration > 0) {
            totalDuration.longValue = service.duration
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val painter = painterResource(id = song.value.cover)
        val playing = currentIndex.value == service.selectedIndex && isPlaying.value
        AlbumCoverAnimation(isSongPlaying = playing, painter = painter)

        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            ) {
                TrackSlider(
                    value = sliderPosition.longValue.toFloat(),
                    onValueChange = {
                        sliderPosition.longValue = it.toLong()
                    },
                    onValueChangeFinished = {
                        currentSliderPosition.longValue = sliderPosition.longValue
                        service.seekTo(sliderPosition.longValue)
                    },
                    songDuration = totalDuration.longValue.toFloat()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {

                    Text(
                        text = (currentSliderPosition.longValue).convertToText(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )

                    val remainTime = totalDuration.longValue - currentSliderPosition.longValue
                    Text(
                        text = if (remainTime >= 0) remainTime.convertToText() else "",
                        modifier = Modifier
                            .padding(8.dp),
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }

            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlButton(icon = R.drawable.ic_previous, size = 40.dp, onClick = {
                    if (isCurrent()) {
                        currentIndex.value = service.goPrev()
                    }
                })
                Spacer(modifier = Modifier.width(20.dp))
                ControlButton(
                    icon = if (isPlaying.value) R.drawable.ic_pause else R.drawable.ic_play,
                    size = 100.dp,
                    onClick = {
                        service.playStopIfNeed(currentIndex.value)
                        isPlaying.value = !isPlaying.value
                    })
                Spacer(modifier = Modifier.width(20.dp))
                ControlButton(icon = R.drawable.ic_next, size = 40.dp, onClick = {
                    if (isCurrent()) {
                        currentIndex.value = service.goNext()
                    }
                })
            }
        }
    }
}