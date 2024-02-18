package com.azharkova.musicplayer.ui.views

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import com.azharkova.musicplayer.data.SongItemModel
import com.azharkova.musicplayer.data.getPlayList
import com.azharkova.musicplayer.data.toModel
import com.azharkova.musicplayer.player.MyPlayer
import com.azharkova.musicplayer.player.PlaybackState
import com.azharkova.musicplayer.player.PlayerEvents
import com.azharkova.musicplayer.player.PlayerStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicManager(var context: Context,
                   var myPlayer: MyPlayer) : PlayerEvents {
    var scope : CoroutineScope = CoroutineScope(SupervisorJob()+ Dispatchers.Main)

    /**
     * A mutable state list of all tracks.
     */
    private val _tracks = mutableStateListOf<SongItemModel>()
    /**
     * An immutable snapshot of the current list of tracks.
     */
    val tracks: List<SongItemModel> get() = _tracks

    val songs = MutableSharedFlow<List<SongItemModel>>(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /**
     * A private Boolean variable to keep track of whether a track is currently being played or not.
     */
    private var isTrackPlay: Boolean = false

    var selectedTrack: SongItemModel? by mutableStateOf(null)
    private set
        /**
         * A private property backed by mutable state that holds the index of the currently selected track.
         */
        private var selectedTrackIndex: Int by mutableStateOf(-1)

    /**
     * A nullable [Job] instance that represents the ongoing process of updating the playback state.
     */
    private var playbackStateJob: Job? = null

    /**
     * A private [MutableStateFlow] that holds the current [PlaybackState].
     * It is used to emit updates about the playback state to observers.
     */
    private val _playbackState = MutableStateFlow(PlaybackState(0L, 0L))
    /**
     * A public property that exposes the [_playbackState] as an immutable [StateFlow] for observers.
     */
    val playbackState: StateFlow<PlaybackState> get() = _playbackState

    /**
     * A private Boolean variable to keep track of whether the track selection is automatic (i.e., due to the completion of a track) or manual.
     */
    private var isAuto: Boolean = false

    /**
     * Initializes the ViewModel. It populates the list of tracks, sets up the media player,
     * and observes the player state.
     */
    init {
        _tracks.addAll(getPlayList().map { it.toModel() })
        songs.tryEmit(_tracks)
        myPlayer.iniPlayer(tracks.toMediaItemList(context))
        observePlayerState()
    }

    /**
     * Handles track selection.
     *
     * @param index The index of the selected track in the track list.
     */
    private fun onTrackSelected(index: Int) {
        if (selectedTrackIndex == -1) isTrackPlay = true
        if (selectedTrackIndex == -1 || selectedTrackIndex != index) {
            _tracks.resetTracks()
            selectedTrackIndex = index
            setUpTrack()
        }
    }

    private fun setUpTrack() {
        if (!isAuto) myPlayer.setUpTrack(selectedTrackIndex, isTrackPlay)
        isAuto = false
    }

    /**
     * Updates the playback state and launches or cancels the playback state job accordingly.
     *
     * @param state The new player state.
     */
    private fun updateState(state: PlayerStates) {
        if (selectedTrackIndex != -1) {
            isTrackPlay = state == PlayerStates.STATE_PLAYING
            _tracks[selectedTrackIndex].state = state
            _tracks[selectedTrackIndex].isSelected = true
            selectedTrack = null
            selectedTrack = tracks[selectedTrackIndex]

            updatePlaybackState(state)
            if (state == PlayerStates.STATE_NEXT_TRACK) {
                isAuto = true
                onNextClick()
            }
            if (state == PlayerStates.STATE_END) onTrackSelected(0)
            songs.tryEmit(_tracks)
        }
    }

    private fun observePlayerState() {
      scope.collectPlayerState(myPlayer, ::updateState)
    }

    private fun updatePlaybackState(state: PlayerStates) {
        playbackStateJob?.cancel()
        playbackStateJob = scope.launchPlaybackStateJob(_playbackState, state, myPlayer)
    }

    /**
     * Implementation of [PlayerEvents.onPreviousClick].
     * Changes to the previous track if one exists.
     */
    override fun onPreviousClick() {
        if (selectedTrackIndex > 0) onTrackSelected(selectedTrackIndex - 1)
    }

    /**
     * Implementation of [PlayerEvents.onNextClick].
     * Changes to the next track in the list if one exists.
     */
    override fun onNextClick() {
        if (selectedTrackIndex < tracks.size - 1) onTrackSelected(selectedTrackIndex + 1)
    }

    /**
     * Implementation of [PlayerEvents.onPlayPauseClick].
     * Toggles play/pause state of the current track.
     */
    override fun onPlayPauseClick() {
        myPlayer.playPause()
    }

    /**
     * Implementation of [PlayerEvents.onTrackClick].
     * Selects the clicked track from the track list.
     *
     * @param track The track that was clicked.
     */
    override fun onTrackClick(track: SongItemModel) {
        onTrackSelected(tracks.indexOf(track))
    }

    /**
     * Implementation of [PlayerEvents.onSeekBarPositionChanged].
     * Seeks to the specified position in the current track.
     *
     * @param position The position to seek to.
     */
    override fun onSeekBarPositionChanged(position: Long) {
        scope.launch { myPlayer.seekToPosition(position) }
    }

    /**
     * Cleans up the media player when the ViewModel is cleared.
     */
    fun onCleared() {
        myPlayer.releasePlayer()
    }
}

/**
 * Resets the state of each track in the list to the default state.
 */
fun MutableList<SongItemModel>.resetTracks() {
    this.forEach { track ->
        track.isSelected = false
        track.state = PlayerStates.STATE_IDLE
    }
}

/**
 * Converts a list of [Track] objects into a mutable list of [MediaItem] objects.
 *
 * @return A mutable list of [MediaItem] objects.
 */
fun List<SongItemModel>.toMediaItemList(context: Context): MutableList<MediaItem> {
    return this.map {
        val path = "android.resource://" + context.packageName + "/" + it.music
        MediaItem.fromUri(Uri.parse(path))
    }.toMutableList()
}

/**
 * Collects the player state from [myPlayer] and provides updates via the [updateState] function.
 *
 * @param myPlayer The player whose state is to be collected.
 * @param updateState A function to process the player state updates.
 */
fun CoroutineScope.collectPlayerState(
    myPlayer: MyPlayer, updateState: (PlayerStates) -> Unit
) {
    this.launch {
        myPlayer.playerState.collect {
            updateState(it)
        }
    }
}

/**
 * Launches a coroutine to periodically update the [playbackStateFlow] with the current
 * playback position and track duration from [myPlayer] as long as the player state is [STATE_PLAYING].
 *
 * @param playbackStateFlow The MutableStateFlow to be updated.
 * @param state The current player state.
 * @param myPlayer The player whose playback information is to be collected.
 */
fun CoroutineScope.launchPlaybackStateJob(
    playbackStateFlow: MutableStateFlow<PlaybackState>, state: PlayerStates, myPlayer: MyPlayer
) = launch {
    do {
        playbackStateFlow.emit(
            PlaybackState(
                currentPlaybackPosition = myPlayer.currentPlaybackPosition,
                currentTrackDuration = myPlayer.currentTrackDuration
            )
        )
        delay(1000) // delay for 1 second
    } while (state == PlayerStates.STATE_PLAYING && isActive)
}