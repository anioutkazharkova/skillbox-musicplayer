package com.azharkova.musicplayer


import android.os.Bundle
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.azharkova.musicplayer.data.SongItem
import com.azharkova.musicplayer.data.getPlayList
import com.azharkova.musicplayer.service.MusicService
import com.azharkova.musicplayer.ui.views.SongItemScreen
import com.azharkova.musicplayer.ui.views.SongItemView
import com.azharkova.musicplayer.ui.views.SongsListView
import com.azharkova.musicplayer.ui.views.SongsListViewModel

enum class SongsScreen(@StringRes val value: Int) {
    SongsList(R.string.songs_list),
    SongsItem(R.string.songs_item)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: SongsScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {Text("Songs")},
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "back"
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val vm: SongsListViewModel = KoinDIFactory.resolve(SongsListViewModel::class) ?: SongsListViewModel()
    val currentScreen = SongsScreen.valueOf(
        backStackEntry?.destination?.route ?: SongsScreen.SongsList.name
    )
    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = SongsScreen.SongsList.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = SongsScreen.SongsList.name) {
                SongsListView(vm) {
                    navController.navigate(
                        SongsScreen.SongsItem.name,
                        args = bundleOf(
                            "ITEM" to it
                        )
                    )
                }
            }
            composable(route = SongsScreen.SongsItem.name) {
                SongItemScreen(it.arguments?.getInt("ITEM")?:0)
            }
        }
    }
}

fun NavController.navigate(
    route: String,
    args: Bundle,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    val nodeId = graph.findNode(route = route)?.id
    if (nodeId != null) {
        navigate(nodeId, args, navOptions, navigatorExtras)
    }
}