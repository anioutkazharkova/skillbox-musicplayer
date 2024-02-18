
```
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
```