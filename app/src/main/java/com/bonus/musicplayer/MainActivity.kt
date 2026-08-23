package com.bonus.musicplayer

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.SeekBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bonus.musicplayer.ui.theme.BONUSMusicTheme
import java.io.File
import kotlinx.coroutines.delay
import android.net.Uri
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.basicMarquee
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.core.content.ContextCompat
import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import android.graphics.Bitmap
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.aspectRatio
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult


val OneUISansFamily = FontFamily(
    Font(
        R.font.one_ui_sans_vf,
        weight = FontWeight.Normal
    )
)


data class MusicTrack(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val trackNumber: Int,
    val genre: String,
    val composer: String,
    val path: String,
    val duration: Long
)
data class Playlist(
    val id: Long,
    val name: String,
    val trackIds: List<Long> = emptyList()
)


fun loadMusicTracks(context: Context): List<MusicTrack> {

    val tracks = mutableListOf<MusicTrack>()

    val collection =
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.GENRE,
        MediaStore.Audio.Media.COMPOSER,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.ALBUM_ID
    )

    val selection =
        "${MediaStore.Audio.Media.IS_MUSIC} != 0"

    context.contentResolver.query(
        collection,
        projection,
        selection,
        null,
        "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
    )?.use { cursor ->

        val idColumn =
            cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media._ID
            )
        val titleColumn =
            cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.TITLE
            )
        val artistColumn =
            cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.ARTIST
            )
        val albumColumn =
            cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.ALBUM
            )
        val albumIdColumn =
            cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.ALBUM_ID
            )
        val trackNumberColumn =
            cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.TRACK
            )
        val genreColumn =
            cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.GENRE
            )
        val composerColumn =
            cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.COMPOSER
            )
        val pathColumn =
            cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.DATA
            )
        val durationColumn =
            cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.DURATION
            )

        while (cursor.moveToNext()) {

            tracks.add(
                MusicTrack(
                    id = cursor.getLong(idColumn),

                    title = cursor.getString(titleColumn)
                        ?: "Titre inconnu",

                    artist = cursor.getString(artistColumn)
                        ?: "Artiste inconnu",

                    album = cursor.getString(albumColumn)
                        ?: "Album inconnu",

                    albumId = cursor.getLong(albumIdColumn),

                    trackNumber = (
                            cursor.getInt(trackNumberColumn) % 100
                            ),
                    genre = cursor.getString(genreColumn)
                        ?: "Genre inconnu",
                    composer = cursor.getString(composerColumn)
                        ?: "Producteur inconnu",
                    path = cursor.getString(pathColumn),
                    duration = cursor.getLong(durationColumn)
                )
            )
        }
    }

    return tracks
}


fun formatDuration(milliseconds: Long): String {

    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%d:%02d".format(minutes, seconds)
}
fun loadAlbumArt(
    context: Context,
    albumId: Long
): Bitmap? {

    val albumArtUri = Uri.parse(
        "content://media/external/audio/albumart/$albumId"
    )

    return try {

        context.contentResolver.openInputStream(
            albumArtUri
        )?.use { inputStream ->

            BitmapFactory.decodeStream(
                inputStream
            )
        }

    } catch (e: Exception) {

        null
    }
}


class MainActivity : ComponentActivity() {

    lateinit var player: ExoPlayer
    private var mediaController by mutableStateOf<MediaController?>(null)
    val playbackPlayer: Player
        get() = mediaController
            ?: error("MediaController is not connected")
    val musicQueue = MusicQueue()


    override fun onDestroy() {

        if (::player.isInitialized) {
            player.release()
        }

        super.onDestroy()
    }

    private fun saveLastPlayedTrack(track: MusicTrack) {

        getSharedPreferences(
            "BONUS_Music",
            Context.MODE_PRIVATE
        )
            .edit()
            .putLong(
                "lastPlayedTrackId",
                track.id
            )
            .apply()
    }

    private fun loadLastPlayedTrack(): MusicTrack? {

        val preferences =
            getSharedPreferences(
                "BONUS_Music",
                Context.MODE_PRIVATE
            )

        val trackId =
            preferences.getLong(
                "lastPlayedTrackId",
                -1L
            )

        if (trackId == -1L) {
            return null
        }

        val tracks = loadMusicTracks(this)


        return tracks.firstOrNull {
            it.id == trackId
        }
    }


    private fun scanAudioFiles() {

        val downloadFolder = File(
            "/storage/emulated/0/Download"
        )

        if (!downloadFolder.exists()) return

        val audioExtensions = setOf(
            "mp3",
            "m4a",
            "aac",
            "flac",
            "wav",
            "ogg",
            "opus"
        )

        val audioFiles = downloadFolder
            .walkTopDown()
            .filter { file ->
                file.isFile &&
                        file.extension.lowercase() in audioExtensions
            }
            .map { it.absolutePath }
            .toList()

        if (audioFiles.isEmpty()) return

        MediaScannerConnection.scanFile(
            this,
            audioFiles.toTypedArray(),
            null
        ) { path, uri ->

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        scanAudioFiles()

        player = ExoPlayer.Builder(this).build()
        val sessionToken = SessionToken(
            this,
            ComponentName(
                this,
                PlaybackService::class.java
            )
        )
        val appPreferences =
            AppPreferences(this)


        val controllerFuture =
            MediaController.Builder(
                this,
                sessionToken
            ).buildAsync()

        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()

                val lastTrack = loadLastPlayedTrack()

                if (lastTrack != null) {

                    val mediaUri =
                        ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            lastTrack.id
                        )

                    val mediaItem =
                        MediaItem.fromUri(mediaUri)

                    mediaController?.setMediaItem(mediaItem)
                    mediaController?.prepare()

                    val preferences =
                        getSharedPreferences(
                            "BONUS_Music",
                            Context.MODE_PRIVATE
                        )

                    val lastPosition =
                        preferences.getLong(
                            "lastPlayedPosition",
                            0L
                        )

                    if (lastPosition > 0L) {
                        mediaController?.seekTo(lastPosition)
                    }
                }
            },
            ContextCompat.getMainExecutor(this)
        )
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = true

        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightNavigationBars = true


        val preferences =
            getSharedPreferences(
                "BONUS_Music",
                Context.MODE_PRIVATE
            )

        val welcomeShown =
            preferences.getBoolean(
                "welcomeShown",
                false
            )

        var themeMode by mutableStateOf(
            appPreferences.themeMode
        )


        val audioGranted =
            checkSelfPermission(
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        val notificationsGranted =
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        var libraryCategory by mutableStateOf("Titres")
        var libraryPlaylist by mutableStateOf<Playlist?>(null)
        var libraryArtist by mutableStateOf<String?>(null)
        var libraryAlbum by mutableStateOf<String?>(null)
        var libraryGenre by mutableStateOf<String?>(null)
        var libraryComposer by mutableStateOf<String?>(null)


        setContent {
            val systemDarkMode = isSystemInDarkTheme()

            val isDarkMode =
                when (themeMode) {
                    AppPreferences.ThemeMode.SYSTEM -> systemDarkMode
                    AppPreferences.ThemeMode.LIGHT -> false
                    AppPreferences.ThemeMode.DARK -> true
                }

            BONUSMusicTheme (
                darkTheme = isDarkMode
            ) {

                var showPlayerScreen by remember {
                    mutableStateOf(false)
                }
                var showSettingsScreen by remember {
                    mutableStateOf(false)
                }

                var selectedItem by remember {
                    mutableIntStateOf(0)
                }

                var selectedTrack by remember {
                    mutableStateOf<MusicTrack?>(null)
                }
                LaunchedEffect(Unit) {

                    val lastTrack = loadLastPlayedTrack()

                    if (lastTrack != null) {
                        selectedTrack = lastTrack
                    }
                }

                var welcomeShown by remember {
                    mutableStateOf(
                        getSharedPreferences(
                            "BONUS_Music",
                            Context.MODE_PRIVATE
                        ).getBoolean(
                            "welcomeShown",
                            false
                        )
                    )
                }

                var showPermissions by remember {
                    mutableStateOf(false)
                }
                val mediaControllerReady =
                    mediaController != null


                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (!welcomeShown) {

                            if (showPermissions) {

                                PermissionsScreen(
                                    onPermissionsGranted = {
                                        getSharedPreferences(
                                            "BONUS_Music",
                                            Context.MODE_PRIVATE
                                        )
                                            .edit()
                                            .putBoolean(
                                                "welcomeShown",
                                                true
                                            )
                                            .apply()

                                        welcomeShown = true
                                        showPermissions = false
                                    }
                                )

                            } else {

                                WelcomeScreen(
                                    onContinue = {
                                        showPermissions = true
                                    }
                                )
                            }

                        } else {

                            if (showPlayerScreen) {

                                PlayerScreen(
                                    modifier = Modifier.padding(
                                        innerPadding
                                    ),
                                    track = selectedTrack,
                                    player = playbackPlayer,
                                    onBack = {
                                        showPlayerScreen = false
                                    },
                                    onNext = {
                                        val nextTrack = musicQueue.next()

                                        if (nextTrack != null) {

                                            selectedTrack = nextTrack

                                            val mediaUri =
                                                ContentUris.withAppendedId(
                                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                    nextTrack.id
                                                )

                                            val mediaItem =
                                                MediaItem.fromUri(mediaUri)

                                            mediaController?.setMediaItem(
                                                mediaItem
                                            )
                                            mediaController?.prepare()
                                            mediaController?.play()
                                        }
                                    },
                                    onPrevious = {
                                        val previousTrack = musicQueue.previous()

                                        if (previousTrack != null) {

                                            selectedTrack = previousTrack

                                            val mediaUri =
                                                ContentUris.withAppendedId(
                                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                    previousTrack.id
                                                )

                                            val mediaItem =
                                                MediaItem.fromUri(mediaUri)

                                            mediaController?.setMediaItem(
                                                mediaItem
                                            )
                                            mediaController?.prepare()
                                            mediaController?.play()
                                        }
                                    },
                                    onTrackSelected = { track ->
                                        selectedTrack = track
                                    }
                                )

                            } else {

                                if (showSettingsScreen) {

                                    SettingsScreen(
                                        onBack = {
                                            showSettingsScreen = false
                                        },
                                        themeMode = themeMode,
                                        onThemeModeChanged = { mode ->
                                            themeMode = mode
                                            appPreferences.themeMode = mode
                                        }
                                    )

                                } else {

                                    when (selectedItem) {

                                        0 -> HomeScreen(
                                            modifier = Modifier.padding(
                                                innerPadding
                                            ),
                                            onOpenPlayer = {
                                                showPlayerScreen = true
                                            },
                                            onOpenSettings = {
                                                showSettingsScreen = true
                                            },
                                            selectedItem = selectedItem,
                                            onItemSelected = {
                                                selectedItem = it
                                            },
                                            selectedTrack = selectedTrack,
                                            player = player
                                        )

                                        1 -> SearchScreen(
                                            modifier = Modifier.padding(
                                                innerPadding
                                            ),
                                            onTrackSelected = { track ->

                                                selectedTrack = track
                                                saveLastPlayedTrack(track)

                                                val tracks =
                                                    loadMusicTracks(
                                                        this@MainActivity
                                                    )

                                                val startIndex =
                                                    tracks.indexOfFirst {
                                                        it.id == track.id
                                                    }

                                                musicQueue.setQueue(
                                                    tracks = tracks,
                                                    startIndex = startIndex
                                                )

                                                val mediaItems =
                                                    tracks.map { musicTrack ->

                                                        val mediaUri =
                                                            ContentUris.withAppendedId(
                                                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                                musicTrack.id
                                                            )

                                                        MediaItem.fromUri(
                                                            mediaUri
                                                        )
                                                    }

                                                mediaController?.setMediaItems(
                                                    mediaItems,
                                                    startIndex,
                                                    0L
                                                )

                                                mediaController?.prepare()
                                                mediaController?.play()
                                            }
                                        )

                                        2 -> LibraryScreen(
                                            modifier = Modifier.padding(
                                                innerPadding
                                            ),

                                            libraryCategory = libraryCategory,
                                            onLibraryCategoryChanged = {
                                                libraryCategory = it
                                            },

                                            libraryPlaylist = libraryPlaylist,
                                            onLibraryPlaylistChanged = {
                                                libraryPlaylist = it
                                            },

                                            libraryArtist = libraryArtist,
                                            onLibraryArtistChanged = {
                                                libraryArtist = it
                                            },

                                            libraryAlbum = libraryAlbum,
                                            onLibraryAlbumChanged = {
                                                libraryAlbum = it
                                            },

                                            libraryGenre = libraryGenre,
                                            onLibraryGenreChanged = {
                                                libraryGenre = it
                                            },

                                            libraryComposer = libraryComposer,
                                            onLibraryComposerChanged = {
                                                libraryComposer = it
                                            },

                                            onTrackSelected = { track ->

                                                selectedTrack = track
                                                saveLastPlayedTrack(track)

                                                val tracks =
                                                    loadMusicTracks(
                                                        this@MainActivity
                                                    )

                                                val startIndex =
                                                    tracks.indexOfFirst {
                                                        it.id == track.id
                                                    }

                                                musicQueue.setQueue(
                                                    tracks = tracks,
                                                    startIndex = startIndex
                                                )

                                                val mediaItems =
                                                    tracks.map { musicTrack ->

                                                        val mediaUri =
                                                            ContentUris.withAppendedId(
                                                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                                musicTrack.id
                                                            )

                                                        MediaItem.fromUri(
                                                            mediaUri
                                                        )
                                                    }

                                                mediaController?.setMediaItems(
                                                    mediaItems,
                                                    startIndex,
                                                    0L
                                                )

                                                mediaController?.prepare()
                                                mediaController?.play()
                                            },

                                            onPlaylistTrackSelected = { track, playlistTracks ->

                                                selectedTrack = track
                                                saveLastPlayedTrack(track)

                                                val startIndex =
                                                    playlistTracks.indexOfFirst {
                                                        it.id == track.id
                                                    }

                                                musicQueue.setQueue(
                                                    tracks = playlistTracks,
                                                    startIndex = startIndex
                                                )

                                                val mediaItems =
                                                    playlistTracks.map { musicTrack ->

                                                        val mediaUri =
                                                            ContentUris.withAppendedId(
                                                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                                musicTrack.id
                                                            )

                                                        MediaItem.fromUri(
                                                            mediaUri
                                                        )
                                                    }

                                                mediaController?.setMediaItems(
                                                    mediaItems,
                                                    startIndex,
                                                    0L
                                                )

                                                mediaController?.prepare()
                                                mediaController?.play()
                                            }
                                        )
                                    }

                                    // ═══════════════════════════════════════
                                    // MINI PLAYER
                                    // ═══════════════════════════════════════

                                    if (
                                        selectedTrack != null &&
                                        mediaControllerReady
                                    ) {

                                        MiniPlayer(
                                            modifier = Modifier
                                                .align(
                                                    Alignment.BottomCenter
                                                )
                                                .navigationBarsPadding()
                                                .padding(
                                                    start = 16.dp,
                                                    end = 16.dp,
                                                    bottom = 85.dp
                                                ),
                                            track = selectedTrack,
                                            player = playbackPlayer,
                                            onClick = {
                                                showPlayerScreen = true
                                            }
                                        )
                                    }

                                    // ═══════════════════════════════════════
                                    // BARRE DE NAVIGATION
                                    // ═══════════════════════════════════════

                                    Surface(
                                        modifier = Modifier
                                            .align(
                                                Alignment.BottomCenter
                                            )
                                            .padding(
                                                start = 16.dp,
                                                end = 16.dp,
                                                bottom = 11.dp
                                            )
                                            .navigationBarsPadding()
                                            .width(360.dp),
                                        shape = RoundedCornerShape(
                                            45.dp
                                        ),
                                        tonalElevation = 3.dp
                                    ) {

                                        BoxWithConstraints(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(72.dp)
                                        ) {

                                            val itemWidth =
                                                maxWidth / 3

                                            val indicatorOffset by animateDpAsState(
                                                targetValue =
                                                    itemWidth * selectedItem,
                                                animationSpec = tween(
                                                    durationMillis = 300,
                                                    easing = FastOutSlowInEasing
                                                ),
                                                label = "NavigationIndicator"
                                            )

                                            Surface(
                                                modifier = Modifier
                                                    .width(itemWidth)
                                                    .height(90.dp)
                                                    .offset(
                                                        x = indicatorOffset
                                                    )
                                                    .align(
                                                        Alignment.CenterStart
                                                    ),
                                                shape = RoundedCornerShape(
                                                    72.dp
                                                ),
                                                color =
                                                    MaterialTheme
                                                        .colorScheme
                                                        .secondaryContainer
                                            ) {
                                            }

                                            Row(
                                                modifier =
                                                    Modifier.fillMaxSize(),
                                                verticalAlignment =
                                                    Alignment.CenterVertically
                                            ) {

                                                NavigationItem(
                                                    modifier =
                                                        Modifier.weight(1f),
                                                    selected = false,
                                                    icon = Icons.Default.Home,
                                                    label = "Accueil",
                                                    onClick = {
                                                        selectedItem = 0
                                                    }
                                                )

                                                NavigationItem(
                                                    modifier =
                                                        Modifier.weight(1f),
                                                    selected = false,
                                                    icon = Icons.Default.Search,
                                                    label = "Rechercher",
                                                    onClick = {
                                                        selectedItem = 1
                                                    }
                                                )

                                                NavigationItem(
                                                    modifier =
                                                        Modifier.weight(1f),
                                                    selected = false,
                                                    icon = Icons.Default.LibraryMusic,
                                                    label = "Bibliothèque",
                                                    onClick = {
                                                        selectedItem = 2
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun WelcomeScreen(
        modifier: Modifier = Modifier,
        onContinue: () -> Unit
    ) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text = "BONUS Music",
                    fontFamily = OneUISansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color(0xFF030303)
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Votre musique, votre manière de l'écouter.",
                    fontFamily = OneUISansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color(0xFF666666)
                )
            }

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A73E8),
                    contentColor = Color.White
                )
            ) {

                Text(
                    text = "Continuer",
                    fontFamily = OneUISansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp
                )
            }
        }
    }


    @Composable
    fun PermissionsScreen(
        modifier: Modifier = Modifier,
        onPermissionsGranted: () -> Unit
    ) {

        var permissionGranted by remember {
            mutableStateOf(false)
        }

        var showPermissionError by remember {
            mutableStateOf(false)
        }


        val permissionLauncher =
            rememberLauncherForActivityResult(
                contract =
                    ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->

                val audioGranted =
                    permissions[
                        Manifest.permission.READ_MEDIA_AUDIO
                    ] == true

                val notificationsGranted =
                    if (
                        Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.TIRAMISU
                    ) {

                        permissions[
                            Manifest.permission.POST_NOTIFICATIONS
                        ] == true

                    } else {
                        true
                    }


                if (
                    audioGranted &&
                    notificationsGranted
                ) {

                    permissionGranted = true
                    onPermissionsGranted()

                } else if (!audioGranted) {

                    showPermissionError = true
                }
            }


        if (permissionGranted) {

            return
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 100.dp,
                    bottom = 24.dp
                )
        ) {

            Text(
                text =
                    "BONUS Music a besoin de\n" +
                            "ces autorisations",
                modifier =
                    Modifier.fillMaxWidth(),
                fontFamily =
                    OneUISansFamily,
                fontWeight =
                    FontWeight.Bold,
                fontSize = 34.sp,
                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(5.dp)
            )


            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.Center
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        painter =
                            painterResource(
                                R.drawable.ic_oui_audio
                            ),
                        contentDescription =
                            "Musique et fichiers audio",
                        modifier =
                            Modifier.size(32.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    Column {

                        Text(
                            text =
                                "Musique et fichiers audio",
                            fontFamily =
                                OneUISansFamily,
                            fontWeight =
                                FontWeight.Bold,
                            fontSize = 17.sp
                        )

                        Text(
                            text =
                                "Permet d'afficher votre bibliothèque musicale.",
                            fontSize = 14.sp
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Notifications,
                        contentDescription =
                            "Notifications",
                        modifier =
                            Modifier.size(32.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    Column {

                        Text(
                            text = "Notifications",
                            fontFamily =
                                OneUISansFamily,
                            fontWeight =
                                FontWeight.Bold,
                            fontSize = 17.sp
                        )

                        Text(
                            text =
                                "Permet de contrôler la lecture depuis la notification.",
                            fontSize = 14.sp
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                Text(
                    text =
                        "🎧 Appareils Bluetooth\n" +
                                "Permet de détecter vos écouteurs et enceintes Bluetooth."
                )
            }


            Button(
                onClick = {

                    val permissions =
                        mutableListOf(
                            Manifest.permission.READ_MEDIA_AUDIO
                        )

                    if (
                        Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.TIRAMISU
                    ) {

                        permissions.add(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }

                    permissionLauncher.launch(
                        permissions.toTypedArray()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A73E8),
                    contentColor = Color.White
                )
            ) {

                Text(
                    text = "Autoriser",
                    fontFamily = OneUISansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp
                )
            }
        }
    }


    @Composable
    fun HomeScreen(
        modifier: Modifier = Modifier,
        onOpenPlayer: () -> Unit = {},
        onOpenSettings: () -> Unit = {},
        selectedItem: Int,
        onItemSelected: (Int) -> Unit,
        selectedTrack: MusicTrack?,
        player: ExoPlayer
    ) {
        var showMenu by remember {
            mutableStateOf(false)
        }
        val context = LocalContext.current

        val tracks = remember {
            loadMusicTracks(context)
        }

        Box(
            modifier = modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 24.dp,
                        bottom = 100.dp
                    )
            ) {

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Accueil",
                        fontFamily =
                            OneUISansFamily,
                        fontWeight =
                            FontWeight.Bold,
                        fontSize = 36.sp
                    )

                    Box {

                        IconButton(
                            onClick = {
                                showMenu = !showMenu
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Plus d'options"
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = {
                                showMenu = false
                            }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Paramètres"
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onOpenSettings()
                                }
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                Text(
                    text = "Bienvenue",
                    fontFamily =
                        OneUISansFamily,
                    fontWeight =
                        FontWeight.Bold,
                    fontSize = 24.sp
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
                if (tracks.isEmpty()) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(22.dp)
                        ) {

                            Text(
                                text = "Il manque certaines autorisations",
                                fontFamily = OneUISansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "BONUS Music a besoin d'accéder à la musique stockée dans l'appareil pour pouvoir afficher vos musiques.",
                                fontFamily = OneUISansFamily,
                                fontSize = 15.sp
                            )

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            Button(
                                onClick = {
                                    val intent = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:${context.packageName}")
                                    )

                                    context.startActivity(intent)
                                }
                            ) {
                                Text(
                                    text = "Ouvrir les paramètres",
                                    fontFamily = OneUISansFamily,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(28.dp)
                ) {

                    Column(
                        modifier =
                            Modifier.padding(24.dp)
                    ) {

                        Text(
                            text = "Les plus écoutés cette semaine",
                            fontFamily =
                                OneUISansFamily,
                            fontWeight =
                                FontWeight.Bold,
                            fontSize = 22.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Vous n'avez rien écouté. Les titres les plus écoutés s'afficheront ici.",
                            fontFamily =
                                OneUISansFamily,
                        )
                    }
                }
            }
        }
    }


    @Composable
    fun NavigationItem(
        modifier: Modifier = Modifier,
        selected: Boolean,
        icon: ImageVector,
        label: String,
        onClick: () -> Unit
    ) {
        val animatedColor by animateColorAsState(
            targetValue =
                if (selected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                },
            label = "NavigationItemColor"
        )

        Surface(
            modifier = modifier,
            onClick = onClick,
            shape = RoundedCornerShape(72.dp),
            color = Color.Transparent
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 9.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = label
                )

                Text(
                    text = label,
                    fontFamily =
                        OneUISansFamily,
                    fontWeight =
                        FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }


    @Composable
    fun MiniPlayer(
        modifier: Modifier = Modifier,
        track: MusicTrack?,
        player: Player,
        onClick: () -> Unit
    ) {
        val context = LocalContext.current

        val albumArtUri = track?.albumId?.let {
            Uri.parse(
                "content://media/external/audio/albumart/$it"
            )
        }

        val albumArtBitmap = remember(albumArtUri) {
            albumArtUri?.let { uri ->
                try {
                    context.contentResolver
                        .openInputStream(uri)
                        ?.use {
                            BitmapFactory.decodeStream(it)
                        }
                } catch (e: Exception) {
                    null
                }
            }
        }
        val miniPlayerColor = remember(albumArtBitmap) {
            if (albumArtBitmap != null) {

                var red = 0L
                var green = 0L
                var blue = 0L
                var count = 0

                val step = 10

                for (y in 0 until albumArtBitmap.height step step) {
                    for (x in 0 until albumArtBitmap.width step step) {

                        val pixel = albumArtBitmap.getPixel(x, y)

                        red += android.graphics.Color.red(pixel)
                        green += android.graphics.Color.green(pixel)
                        blue += android.graphics.Color.blue(pixel)

                        count++
                    }
                }

                if (count > 0) {
                    Color(
                        red = (red / count).toInt(),
                        green = (green / count).toInt(),
                        blue = (blue / count).toInt()
                    )
                } else {
                    Color(0xFFE7E7E7)
                }

            } else {
                Color(0xFFE7E7E7)
            }
        }
        val isMiniPlayerDark = remember(miniPlayerColor) {
            val brightness =
                (
                        android.graphics.Color.red(
                            android.graphics.Color.rgb(
                                (miniPlayerColor.red * 255).toInt(),
                                (miniPlayerColor.green * 255).toInt(),
                                (miniPlayerColor.blue * 255).toInt()
                            )
                        ) +
                                android.graphics.Color.green(
                                    android.graphics.Color.rgb(
                                        (miniPlayerColor.red * 255).toInt(),
                                        (miniPlayerColor.green * 255).toInt(),
                                        (miniPlayerColor.blue * 255).toInt()
                                    )
                                ) +
                                android.graphics.Color.blue(
                                    android.graphics.Color.rgb(
                                        (miniPlayerColor.red * 255).toInt(),
                                        (miniPlayerColor.green * 255).toInt(),
                                        (miniPlayerColor.blue * 255).toInt()
                                    )
                                )
                        ) / 3

            brightness < 128
        }

        val miniPlayerContentColor = remember(miniPlayerColor) {
            val brightness =
                (
                        miniPlayerColor.red +
                                miniPlayerColor.green +
                                miniPlayerColor.blue
                        ) / 3f

            if (brightness < 0.5f) {
                Color.White
            } else {
                Color.Black
            }
        }

        var isPlaying by remember {
            mutableStateOf(player.isPlaying)
        }


        Surface(
            modifier = modifier
                .fillMaxWidth()
                .height(72.dp),
            shape =
                RoundedCornerShape(28.dp),
            tonalElevation = 3.dp,
            color = miniPlayerColor,
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ZONE QUI OUVRE LE PLAYER
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onClick()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(
                                RoundedCornerShape(12.dp)
                            )
                    ) {

                        if (albumArtBitmap != null) {

                            Image(
                                bitmap = albumArtBitmap.asImageBitmap(),
                                contentDescription = "Pochette de l'album",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                        } else {

                            Image(
                                painter = painterResource(
                                    id = R.drawable.default_art_cover
                                ),
                                contentDescription = "Pochette par défaut",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = track?.title ?: "Aucune musique",
                            fontFamily = OneUISansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee(),
                            color = miniPlayerContentColor
                        )

                        Text(
                            text = track?.artist ?: "Aucun artiste",
                            fontFamily = OneUISansFamily,
                            fontSize = 13.sp,
                            maxLines = 1,
                            color = miniPlayerContentColor
                        )
                    }
                }

                // BOUTON PLAY / PAUSE INDÉPENDANT
                IconButton(
                    onClick = {

                        if (player.isPlaying) {
                            player.pause()
                        } else {
                            player.play()
                        }

                        isPlaying = player.isPlaying
                    }
                ) {

                    Icon(
                        painter = painterResource(
                            id = if (isPlaying) {
                                R.drawable.ic_media_pause
                            } else {
                                R.drawable.ic_media_play
                            }
                        ),
                        contentDescription = if (isPlaying) {
                            "Pause"
                        } else {
                            "Lecture"
                        },
                        tint = miniPlayerContentColor
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PlayerScreen(
        modifier: Modifier = Modifier,
        track: MusicTrack?,
        player: Player,
        onBack: () -> Unit,
        onNext: () -> Unit,
        onPrevious: () -> Unit,
        onTrackSelected: (MusicTrack) -> Unit,

        ) {
        val context = LocalContext.current
        val activity = context as MainActivity
        val musicQueue = activity.musicQueue
        val albumArtUri = track?.albumId?.let {
            Uri.parse(
                "content://media/external/audio/albumart/$it"
            )
        }
        val albumArtBitmap = remember(albumArtUri) {
            albumArtUri?.let { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
        val isDarkCover = remember(albumArtBitmap) {
            if (albumArtBitmap == null) {
                false
            } else {
                var totalBrightness = 0L
                var pixelCount = 0

                val step = 10

                for (y in 0 until albumArtBitmap.height step step) {
                    for (x in 0 until albumArtBitmap.width step step) {

                        val pixel = albumArtBitmap.getPixel(x, y)

                        val red = android.graphics.Color.red(pixel)
                        val green = android.graphics.Color.green(pixel)
                        val blue = android.graphics.Color.blue(pixel)

                        totalBrightness +=
                            (red + green + blue) / 3

                        pixelCount++
                    }
                }

                if (pixelCount == 0) {
                    false
                } else {
                    (totalBrightness / pixelCount) < 128
                }
            }
        }
        val playerContentColor =
            if (isDarkCover) {
                Color.White
            } else {
                Color.Black
            }


        var currentPosition by remember {
            mutableLongStateOf(0L)
        }

        var isShuffleEnabled by remember {
            mutableStateOf(player.shuffleModeEnabled)
        }
        LaunchedEffect(player) {
            while (true) {
                isShuffleEnabled = player.shuffleModeEnabled
                delay(100)
            }
        }
        var showAddToPlaylistDialog by remember {
            mutableStateOf(false)
        }

        var isPlaying by remember {
            mutableStateOf(player?.isPlaying == true)
        }

        LaunchedEffect(player, track) {

            currentPosition = player?.currentPosition ?: 0L

            var lastSavedPosition = -1L

            while (true) {

                currentPosition = player?.currentPosition ?: 0L

                val position = currentPosition

                if (
                    player != null &&
                    track != null &&
                    kotlin.math.abs(
                        position - lastSavedPosition
                    ) >= 1000L
                ) {

                    context
                        .getSharedPreferences(
                            "BONUS_Music",
                            Context.MODE_PRIVATE
                        )
                        .edit()
                        .putLong(
                            "lastPlayedPosition",
                            position
                        )
                        .apply()

                    lastSavedPosition = position
                }

                delay(100)
            }
        }
        var showQueue by remember {
            mutableStateOf(false)
        }


        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // Fond flouté
            if (albumArtUri != null) {
                Crossfade(
                    targetState = albumArtUri,
                    animationSpec = tween(
                        durationMillis = 1600
                    ),
                    label = "AlbumBackgroundTransition"
                ) { uri ->

                    if (uri != null) {

                        AndroidView(
                            factory = { context ->
                                android.widget.ImageView(context).apply {
                                    scaleType =
                                        android.widget.ImageView.ScaleType.CENTER_CROP
                                    setImageURI(uri)
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(15.dp)
                        )

                    } else {

                        Image(
                            painter = painterResource(
                                id = R.drawable.default_art_cover
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(15.dp)
                        )
                    }
                }
            }

            // Contenu du lecteur
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription =
                                "Retour",
                            tint = playerContentColor
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(70.dp)
                )


                if (albumArtBitmap != null) {
                    Image(
                        bitmap = albumArtBitmap.asImageBitmap(),
                        contentDescription = "Pochette de l'album",
                        modifier = Modifier
                            .size(280.dp)
                            .clip(RoundedCornerShape(28.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(
                            id = R.drawable.default_art_cover
                        ),
                        contentDescription = "Pochette par défaut",
                        modifier = Modifier
                            .size(280.dp)
                            .clip(RoundedCornerShape(28.dp)),
                        contentScale = ContentScale.Crop
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )


                Text(
                    text =
                        track?.title
                            ?: "Titre inconnu",
                    fontFamily =
                        OneUISansFamily,
                    fontWeight =
                        FontWeight.Bold,
                    fontSize = 24.sp,
                    maxLines = 1,
                    color = playerContentColor,
                    modifier = Modifier.basicMarquee(),
                )


                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )


                Text(
                    text =
                        track?.artist
                            ?: "Artiste inconnu",
                    fontFamily =
                        OneUISansFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = playerContentColor
                )


                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = {
                            showQueue = true
                        }
                    ) {
                        Text(
                            text = "File d'attente",
                            fontFamily = OneUISansFamily,
                            fontWeight = FontWeight.Bold,
                            color = playerContentColor
                        )
                    }

                    IconButton(
                        onClick = {
                            showAddToPlaylistDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ajouter à une playlist",
                            tint = playerContentColor
                        )
                    }
                }


                AndroidView(
                    factory = { context ->

                        SeekBar(context).apply {

                            max = 1000

                            progress = 0

                            progressDrawable =
                                context.getDrawable(
                                    R.drawable
                                        .bonus_seekbar_progress_drawable
                                )

                            thumb =
                                context.getDrawable(
                                    R.drawable
                                        .bonus_seekbar_thumb
                                )


                            setOnSeekBarChangeListener(

                                object :
                                    SeekBar
                                    .OnSeekBarChangeListener {

                                    override fun
                                            onProgressChanged(
                                        seekBar: SeekBar?,
                                        progress: Int,
                                        fromUser: Boolean
                                    ) {

                                        if (fromUser) {

                                            this@apply
                                                .progress =
                                                progress

                                            if (
                                                player.duration > 0
                                            ) {

                                                player.seekTo(
                                                    (
                                                            player.duration
                                                                .toDouble()
                                                                .times(
                                                                    progress
                                                                        .toDouble()
                                                                        .div(1000)
                                                                )
                                                            )
                                                        .toLong()
                                                )
                                            }
                                        }
                                    }


                                    override fun
                                            onStartTrackingTouch(
                                        seekBar: SeekBar?
                                    ) {
                                    }


                                    override fun
                                            onStopTrackingTouch(
                                        seekBar: SeekBar?
                                    ) {
                                    }
                                }
                            )
                        }
                    },
                    update = { seekBar ->

                        val duration = player.duration

                        if (duration > 0) {

                            val newProgress =
                                (
                                        currentPosition.toFloat()
                                            .div(duration.toFloat())
                                            .times(1000)
                                        ).toInt()
                                    .coerceIn(0, 1000)

                            seekBar.progress = newProgress

                        } else {

                            seekBar.progress = 0
                        }
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .offset(y = (-40).dp)
                )


                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .offset(y = (-40).dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {


                    Text(
                        text = formatDuration(currentPosition),
                        fontFamily = OneUISansFamily,
                        fontSize = 13.sp,
                        color = playerContentColor
                    )
                    Text(
                        text = formatDuration(
                            if (player.duration > 0) {
                                player.duration
                            } else {
                                track?.duration ?: 0
                            }
                        ),
                        fontFamily = OneUISansFamily,
                        fontSize = 13.sp,
                        color = playerContentColor
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(32.dp)
                )


                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .offset(y = (-60).dp),
                    horizontalArrangement =
                        Arrangement.SpaceEvenly,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    // Aléatoire
                    IconButton(
                        onClick = {
                            player.shuffleModeEnabled = !player.shuffleModeEnabled
                            isShuffleEnabled = player.shuffleModeEnabled
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isShuffleEnabled) {
                                    R.drawable.ic_shuffle_on
                                } else {
                                    R.drawable.ic_shuffle_off
                                }
                            ),
                            contentDescription = "Aléatoire",
                            modifier = Modifier.size(25.dp),
                            tint = playerContentColor
                        )
                    }

                    // Précédent
                    IconButton(
                        onClick = onPrevious
                    ) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_media_previous
                            ),
                            contentDescription = "Précédent",
                            tint = playerContentColor
                        )
                    }


                    // Play / Pause
                    IconButton(
                        onClick = {

                            if (player.isPlaying) {

                                player.pause()

                            } else {

                                player.play()
                            }

                            isPlaying =
                                player.isPlaying
                        }
                    ) {

                        Icon(
                            painter =
                                painterResource(
                                    id =
                                        if (isPlaying) {
                                            R.drawable
                                                .ic_media_pause
                                        } else {
                                            R.drawable
                                                .ic_media_play
                                        }
                                ),
                            contentDescription =
                                if (isPlaying) {
                                    "Pause"
                                } else {
                                    "Lecture"
                                },
                            tint = playerContentColor
                        )
                    }


                    // Suivant
                    IconButton(
                        onClick = onNext
                    ) {
                        Icon(
                            painter =
                                painterResource(
                                    id =
                                        R.drawable
                                            .ic_media_next
                                ),
                            contentDescription =
                                "Suivant",
                            tint = playerContentColor
                        )
                    }


                    // Répéter
                    IconButton(
                        onClick = {
                            musicQueue.toggleRepeatMode()
                        }
                    ) {
                        Icon(
                            painter =
                                painterResource(
                                    id =
                                        when (musicQueue.repeatMode) {

                                            RepeatMode.OFF ->
                                                R.drawable.ic_repeat_off

                                            RepeatMode.ONE ->
                                                R.drawable.ic_repeat_one

                                            RepeatMode.ALL ->
                                                R.drawable.ic_repeat_all
                                        }
                                ),
                            contentDescription =
                                when (musicQueue.repeatMode) {

                                    RepeatMode.OFF ->
                                        "Ne rien répéter"

                                    RepeatMode.ONE ->
                                        "Répéter le morceau"

                                    RepeatMode.ALL ->
                                        "Répéter tous les morceaux"
                                },
                            modifier =
                                Modifier.size(30.dp),
                            tint = playerContentColor
                        )
                    }
                }
            }
            if (showQueue) {

                val sheetState =
                    rememberModalBottomSheetState(
                        skipPartiallyExpanded = false
                    )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(alpha = 0.25f)
                        )
                        .blur(18.dp)
                )

                ModalBottomSheet(
                    onDismissRequest = {
                        showQueue = false
                    },
                    sheetState = sheetState
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 24.dp,
                                end = 24.dp,
                                bottom = 24.dp
                            ),

                        ) {

                        Text(
                            text = "File d'attente",
                            fontFamily = OneUISansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        if (musicQueue.tracks.isEmpty()) {

                            Text(
                                text = "La file d'attente est vide.",
                                fontFamily = OneUISansFamily,
                                fontSize = 16.sp
                            )

                        } else {

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 500.dp)
                            ) {

                                items(
                                    items = musicQueue.tracks,
                                    key = { it.id }
                                ) { queueTrack ->

                                    val isCurrent =
                                        queueTrack.id == musicQueue.currentTrack?.id

                                    ListItem(

                                        headlineContent = {
                                            Text(
                                                text = queueTrack.title,
                                                fontFamily = OneUISansFamily,
                                                fontWeight =
                                                    if (isCurrent) {
                                                        FontWeight.Bold
                                                    } else {
                                                        FontWeight.Bold
                                                    },
                                                maxLines = 1
                                            )
                                        },

                                        supportingContent = {
                                            Text(
                                                text = queueTrack.artist,
                                                fontFamily = OneUISansFamily,
                                                maxLines = 1
                                            )
                                        },

                                        leadingContent = {

                                            if (isCurrent) {

                                                Icon(
                                                    painter = painterResource(
                                                        R.drawable.ic_media_play
                                                    ),
                                                    contentDescription =
                                                        "En cours de lecture",
                                                    tint = MaterialTheme
                                                        .colorScheme
                                                        .primary
                                                )
                                            }
                                        },

                                        modifier = Modifier.clickable {

                                            val index =
                                                musicQueue.tracks.indexOf(queueTrack)

                                            val selectedQueueTrack =
                                                musicQueue.playAt(index)

                                            if (selectedQueueTrack != null) {

                                                // Met à jour le morceau affiché dans BONUS Music
                                                onTrackSelected(
                                                    selectedQueueTrack
                                                )

                                                // Reconstruit la file complète pour le MediaController
                                                val mediaItems =
                                                    musicQueue.tracks.map { musicTrack ->

                                                        val mediaUri =
                                                            ContentUris.withAppendedId(
                                                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                                musicTrack.id
                                                            )

                                                        MediaItem.fromUri(
                                                            mediaUri
                                                        )
                                                    }

                                                // Lance le morceau sélectionné
                                                mediaController?.setMediaItems(
                                                    mediaItems,
                                                    index,
                                                    0L
                                                )

                                                mediaController?.prepare()
                                                mediaController?.play()

                                                // Sauvegarde le dernier morceau écouté
                                                saveLastPlayedTrack(
                                                    selectedQueueTrack
                                                )

                                                showQueue = false
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (showAddToPlaylistDialog && track != null) {

                val context = LocalContext.current

                val playlistManager =
                    remember {
                        PlaylistManager(context)
                    }

                val playlists =
                    remember {
                        playlistManager.getPlaylists()
                    }

                AlertDialog(
                    onDismissRequest = {
                        showAddToPlaylistDialog = false
                    },

                    title = {
                        Text(
                            text = "Ajouter à une playlist",
                            fontFamily = OneUISansFamily,
                            fontWeight = FontWeight.Bold
                        )
                    },

                    text = {

                        Column {

                            if (playlists.isEmpty()) {

                                Text(
                                    text = "Aucune playlist disponible.",
                                    fontFamily = OneUISansFamily
                                )

                            } else {

                                playlists.forEach { playlist ->

                                    TextButton(
                                        onClick = {

                                            playlistManager
                                                .addTrackToPlaylist(
                                                    playlistId = playlist.id,
                                                    trackId = track.id
                                                )

                                            showAddToPlaylistDialog = false
                                        },

                                        modifier =
                                            Modifier.fillMaxWidth()
                                    ) {

                                        Text(
                                            text = playlist.name,
                                            fontFamily = OneUISansFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    },

                    confirmButton = {

                        TextButton(
                            onClick = {
                                showAddToPlaylistDialog = false
                            }
                        ) {

                            Text(
                                text = "Annuler",
                                fontFamily = OneUISansFamily
                            )
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun SearchScreen(
        modifier: Modifier = Modifier,
        onTrackSelected: (MusicTrack) -> Unit
    ) {
        val context = LocalContext.current

        val tracks = remember {
            loadMusicTracks(context)
        }

        var searchQuery by remember {
            mutableStateOf("")
        }

        val filteredTracks = remember(
            searchQuery,
            tracks
        ) {
            if (searchQuery.isBlank()) {
                emptyList()
            } else {
                tracks.filter { track ->

                    track.title.contains(
                        searchQuery,
                        ignoreCase = true
                    ) ||
                            track.artist.contains(
                                searchQuery,
                                ignoreCase = true
                            ) ||
                            track.album.contains(
                                searchQuery,
                                ignoreCase = true
                            )

                }
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 50.dp
                )
        ) {

            Text(
                text = "Rechercher",
                fontFamily = OneUISansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 2.dp
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 18.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Rechercher"
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                        },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontFamily = OneUISansFamily,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->

                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Rechercher dans votre musique",
                                    fontFamily = OneUISansFamily,
                                    fontSize = 16.sp
                                )
                            }

                            innerTextField()
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                items(filteredTracks) { track ->

                    val albumArtUri =
                        track.albumId?.let {
                            Uri.parse(
                                "content://media/external/audio/albumart/$it"
                            )
                        }

                    val albumArtBitmap = remember(albumArtUri) {
                        albumArtUri?.let { uri ->
                            try {
                                context.contentResolver
                                    .openInputStream(uri)
                                    ?.use {
                                        BitmapFactory.decodeStream(it)
                                    }
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onTrackSelected(track)
                            }
                            .padding(
                                vertical = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        if (albumArtBitmap != null) {

                            Image(
                                bitmap = albumArtBitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentScale = ContentScale.Crop
                            )

                        } else {

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.ic_oui_audio
                                    ),
                                    contentDescription = null
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.width(14.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = track.title,
                                fontFamily = OneUISansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "${track.artist} • ${track.album}",
                                fontFamily = OneUISansFamily,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun LibraryScreen(
        modifier: Modifier = Modifier,

        libraryCategory: String,
        onLibraryCategoryChanged: (String) -> Unit,

        libraryPlaylist: Playlist?,
        onLibraryPlaylistChanged: (Playlist?) -> Unit,

        libraryArtist: String?,
        onLibraryArtistChanged: (String?) -> Unit,

        libraryAlbum: String?,
        onLibraryAlbumChanged: (String?) -> Unit,

        libraryGenre: String?,
        onLibraryGenreChanged: (String?) -> Unit,

        libraryComposer: String?,
        onLibraryComposerChanged: (String?) -> Unit,

        onTrackSelected: (MusicTrack) -> Unit,

        onPlaylistTrackSelected: (
            MusicTrack,
            List<MusicTrack>
        ) -> Unit
    ) {

        val context = LocalContext.current

        val playlistManager =
            remember {
                PlaylistManager(context)
            }

        var playlists by remember {
            mutableStateOf(
                playlistManager.getPlaylists()
            )
        }

        var showCreatePlaylistDialog by remember {
            mutableStateOf(false)
        }

        var playlistName by remember {
            mutableStateOf("")
        }

        val selectedCategory = libraryCategory

        var selectedPlaylist by remember {
            mutableStateOf(libraryPlaylist)
        }

        val selectedArtist = libraryArtist
        val selectedAlbum = libraryAlbum
        val selectedGenre = libraryGenre
        val selectedComposer = libraryComposer

        var showAddToPlaylistDialog by remember {
            mutableStateOf(false)
        }

        val categories = listOf(
            "Playlists",
            "Titres",
            "Artistes",
            "Albums",
            "Genres",
            "Producteurs",
            "Dossiers"
        )

        val tracks = remember {
            loadMusicTracks(context)
        }

        Box(
            modifier = modifier.fillMaxSize()
        ) {

            // ═══════════════════════════════════════
            // CONTENU NORMAL
            // ═══════════════════════════════════════

            if (selectedPlaylist != null) {

                val currentPlaylist =
                    selectedPlaylist!!

                val playlistTracks =
                    currentPlaylist.trackIds.mapNotNull { trackId ->
                        tracks.firstOrNull { track ->
                            track.id == trackId
                        }
                    }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = 50.dp,
                            bottom = 100.dp
                        )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        IconButton(
                            onClick = {
                                selectedPlaylist = null
                                onLibraryPlaylistChanged(null)
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.ArrowBack,
                                contentDescription =
                                    "Retour"
                            )
                        }

                        Text(
                            text = currentPlaylist.name,
                            fontFamily =
                                OneUISansFamily,
                            fontWeight =
                                FontWeight.Bold,
                            fontSize = 28.sp,
                            maxLines = 1
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Button(
                        onClick = {
                            showAddToPlaylistDialog = true
                        },
                        modifier =
                            Modifier.padding(horizontal = 16.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Add,
                            contentDescription = null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text(
                            text =
                                "Ajouter à la playlist",
                            fontFamily =
                                OneUISansFamily,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    if (playlistTracks.isEmpty()) {

                        Box(
                            modifier =
                                Modifier.fillMaxSize(),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text =
                                    "Cette playlist est vide.",
                                fontFamily =
                                    OneUISansFamily,
                                fontSize = 16.sp
                            )
                        }

                    } else {

                        LazyColumn(
                            modifier =
                                Modifier.fillMaxSize()
                        ) {

                            items(
                                items = playlistTracks,
                                key = { it.id }
                            ) { track ->

                                ListItem(

                                    headlineContent = {

                                        Text(
                                            text =
                                                track.title,
                                            fontFamily =
                                                OneUISansFamily,
                                            fontWeight =
                                                FontWeight.Bold
                                        )
                                    },

                                    supportingContent = {

                                        Text(
                                            text =
                                                track.artist,
                                            fontFamily =
                                                OneUISansFamily
                                        )
                                    },

                                    modifier =
                                        Modifier.clickable {

                                            onPlaylistTrackSelected(
                                                track,
                                                playlistTracks
                                            )
                                        }
                                )
                            }
                        }
                    }
                }

            } else {

                // ═══════════════════════════════════════
                // BIBLIOTHÈQUE NORMALE
                // ═══════════════════════════════════════

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 50.dp,
                            bottom = 100.dp
                        )
                ) {

                    Text(
                        text = "Bibliothèque",
                        fontFamily =
                            OneUISansFamily,
                        fontWeight =
                            FontWeight.Bold,
                        fontSize = 36.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(
                                    rememberScrollState()
                                ),
                        horizontalArrangement =
                            Arrangement.spacedBy(28.dp)
                    ) {

                        categories.forEach { category ->

                            Column(
                                horizontalAlignment =
                                    Alignment.CenterHorizontally,
                                modifier =
                                    Modifier.clickable {

                                        onLibraryCategoryChanged(
                                            category
                                        )
                                    }
                            ) {

                                Text(
                                    text = category,
                                    fontFamily =
                                        OneUISansFamily,
                                    fontWeight =
                                        if (
                                            selectedCategory ==
                                            category
                                        ) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Bold
                                        },
                                    fontSize = 16.sp,
                                    color =
                                        if (selectedCategory == category) {
                                            MaterialTheme.colorScheme.onBackground
                                        } else {
                                            MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.6f
                                            )
                                        }
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(8.dp)
                                )

                                if (
                                    selectedCategory ==
                                    category
                                ) {

                                    Box(
                                        modifier =
                                            Modifier
                                                .width(28.dp)
                                                .height(3.dp)
                                                .clip(
                                                    RoundedCornerShape(
                                                        3.dp
                                                    )
                                                )
                                                .background(
                                                    MaterialTheme.colorScheme.onBackground
                                                )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )

                    when (selectedCategory) {

                        // ═══════════════════════════════
                        // PLAYLISTS
                        // ═══════════════════════════════

                        "Playlists" -> {

                            Column(
                                modifier =
                                    Modifier.fillMaxSize()
                            ) {

                                Button(
                                    onClick = {

                                        playlistName = ""

                                        showCreatePlaylistDialog =
                                            true
                                    }
                                ) {

                                    Text(
                                        text =
                                            "Créer une playlist",
                                        fontFamily =
                                            OneUISansFamily,
                                        fontWeight =
                                            FontWeight.Bold
                                    )
                                }

                                Spacer(
                                    modifier =
                                        Modifier.height(16.dp)
                                )

                                if (playlists.isEmpty()) {

                                    Text(
                                        text =
                                            "Aucune playlist pour le moment.",
                                        fontFamily =
                                            OneUISansFamily,
                                        fontSize = 16.sp
                                    )

                                } else {

                                    LazyColumn(
                                        modifier =
                                            Modifier.fillMaxSize()
                                    ) {

                                        items(
                                            items = playlists,
                                            key = { it.id }
                                        ) { playlist ->

                                            ListItem(

                                                headlineContent = {

                                                    Text(
                                                        text =
                                                            playlist.name,
                                                        fontFamily =
                                                            OneUISansFamily,
                                                        fontWeight =
                                                            FontWeight.Bold
                                                    )
                                                },

                                                supportingContent = {

                                                    Text(
                                                        text =
                                                            "${playlist.trackIds.size} titre" +
                                                                    if (
                                                                        playlist.trackIds.size > 1
                                                                    ) {
                                                                        "s"
                                                                    } else {
                                                                        ""
                                                                    },
                                                        fontFamily =
                                                            OneUISansFamily
                                                    )
                                                },

                                                trailingContent = {

                                                    IconButton(
                                                        onClick = {

                                                            playlistManager
                                                                .deletePlaylist(
                                                                    playlist.id
                                                                )

                                                            playlists =
                                                                playlistManager
                                                                    .getPlaylists()
                                                        }
                                                    ) {

                                                        Icon(
                                                            imageVector =
                                                                Icons.Default.Delete,
                                                            contentDescription =
                                                                "Supprimer"
                                                        )
                                                    }
                                                },

                                                modifier =
                                                    Modifier.clickable {

                                                        selectedPlaylist =
                                                            playlist

                                                        onLibraryPlaylistChanged(
                                                            playlist
                                                        )
                                                    }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ═══════════════════════════════
                        // TITRES
                        // ═══════════════════════════════

                        "Titres" -> {

                            if (tracks.isEmpty()) {

                                Text(
                                    text =
                                        "Aucun titre trouvé. Essayez d'aller dans les paramètres de votre téléphone pour pouvoir autoriser l'accès à votre musique.",
                                    fontFamily =
                                        OneUISansFamily,
                                    fontSize = 16.sp
                                )

                            } else {

                                LazyColumn(
                                    modifier =
                                        Modifier.fillMaxSize()
                                ) {

                                    items(
                                        items = tracks,
                                        key = { it.id }
                                    ) { track ->

                                        ListItem(

                                            headlineContent = {

                                                Text(
                                                    text =
                                                        track.title,
                                                    fontFamily =
                                                        OneUISansFamily,
                                                    fontWeight =
                                                        FontWeight.Bold
                                                )
                                            },

                                            supportingContent = {

                                                Text(
                                                    text =
                                                        track.artist,
                                                    fontFamily =
                                                        OneUISansFamily
                                                )
                                            },

                                            modifier =
                                                Modifier.clickable {

                                                    onTrackSelected(
                                                        track
                                                    )
                                                }
                                        )
                                    }
                                }
                            }
                        }

                        // ═══════════════════════════════
                        // ARTISTES
                        // ═══════════════════════════════

                        "Artistes" -> {

                            val artists =
                                tracks
                                    .map { it.artist }
                                    .filter {
                                        it.isNotBlank()
                                    }
                                    .distinct()
                                    .sorted()

                            if (artists.isEmpty()) {

                                Text(
                                    text =
                                        "Aucun artiste trouvé. Essayez d'aller dans les paramètres de votre téléphone pour pouvoir autoriser l'accès à votre musique.",
                                    fontFamily =
                                        OneUISansFamily,
                                    fontSize = 16.sp
                                )

                            } else {

                                LazyColumn(
                                    modifier =
                                        Modifier.fillMaxSize()
                                ) {

                                    items(
                                        items = artists
                                    ) { artist ->

                                        val artistTrackCount =
                                            tracks.count {
                                                it.artist == artist
                                            }

                                        ListItem(

                                            headlineContent = {

                                                Text(
                                                    text =
                                                        artist,
                                                    fontFamily =
                                                        OneUISansFamily,
                                                    fontWeight =
                                                        FontWeight.Bold
                                                )
                                            },

                                            supportingContent = {

                                                Text(
                                                    text =
                                                        "$artistTrackCount titre" +
                                                                if (
                                                                    artistTrackCount > 1
                                                                ) {
                                                                    "s"
                                                                } else {
                                                                    ""
                                                                },
                                                    fontFamily =
                                                        OneUISansFamily
                                                )
                                            },

                                            modifier =
                                                Modifier.clickable {

                                                    onLibraryArtistChanged(
                                                        artist
                                                    )
                                                }
                                        )
                                    }
                                }
                            }
                        }

                        // ═══════════════════════════════
                        // ALBUMS
                        // ═══════════════════════════════

                        "Albums" -> {

                            val albums = tracks
                                .map { it.albumId to it.album }
                                .distinct()
                                .sortedBy { it.second }

                            if (albums.isEmpty()) {

                                Text(
                                    text =
                                        "Aucun album trouvé. Essayez d'aller dans les paramètres de votre téléphone pour pouvoir autoriser l'accès à votre musique.",
                                    fontFamily =
                                        OneUISansFamily,
                                    fontSize = 16.sp
                                )

                            } else {

                                LazyVerticalGrid(
                                    columns =
                                        GridCells.Fixed(2),
                                    modifier =
                                        Modifier.fillMaxSize(),
                                    horizontalArrangement =
                                        Arrangement.spacedBy(16.dp),
                                    verticalArrangement =
                                        Arrangement.spacedBy(20.dp)
                                ) {

                                    items(albums) {
                                            (albumId, album) ->

                                        val albumTracks =
                                            tracks.filter {
                                                it.albumId == albumId
                                            }

                                        val albumArtist =
                                            albumTracks
                                                .firstOrNull()
                                                ?.artist
                                                ?: "Artiste inconnu"

                                        val albumArtBitmap =
                                            remember(albumId) {
                                                loadAlbumArt(
                                                    context,
                                                    albumId
                                                )
                                            }

                                        Column(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        onLibraryAlbumChanged(
                                                            album
                                                        )
                                                    }
                                        ) {

                                            if (
                                                albumArtBitmap != null
                                            ) {

                                                Image(
                                                    bitmap =
                                                        albumArtBitmap
                                                            .asImageBitmap(),
                                                    contentDescription =
                                                        "Pochette de $album",
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .aspectRatio(1f)
                                                            .clip(
                                                                RoundedCornerShape(
                                                                    20.dp
                                                                )
                                                            ),
                                                    contentScale =
                                                        ContentScale.Crop
                                                )

                                            } else {

                                                Image(
                                                    painter =
                                                        painterResource(
                                                            id =
                                                                R.drawable
                                                                    .default_art_cover
                                                        ),
                                                    contentDescription =
                                                        "Pochette par défaut",
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .aspectRatio(1f)
                                                            .clip(
                                                                RoundedCornerShape(
                                                                    20.dp
                                                                )
                                                            ),
                                                    contentScale =
                                                        ContentScale.Crop
                                                )
                                            }

                                            Spacer(
                                                modifier =
                                                    Modifier.height(8.dp)
                                            )

                                            Text(
                                                text = album,
                                                fontFamily =
                                                    OneUISansFamily,
                                                fontWeight =
                                                    FontWeight.Bold,
                                                fontSize = 16.sp,
                                                maxLines = 1,
                                                overflow =
                                                    TextOverflow.Ellipsis
                                            )

                                            Text(
                                                text =
                                                    albumArtist,
                                                fontFamily =
                                                    OneUISansFamily,
                                                fontSize = 14.sp,
                                                maxLines = 1,
                                                overflow =
                                                    TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ═══════════════════════════════
                        // GENRES
                        // ═══════════════════════════════

                        "Genres" -> {

                            val genres = tracks
                                .map { it.genre }
                                .filter {
                                    it.isNotBlank()
                                }
                                .distinct()
                                .sorted()

                            if (genres.isEmpty()) {

                                Text(
                                    text =
                                        "Aucun genre trouvé. Essayez d'aller dans les paramètres de votre téléphone pour pouvoir autoriser l'accès à votre musique.",
                                    fontFamily =
                                        OneUISansFamily,
                                    fontSize = 16.sp
                                )

                            } else {

                                LazyColumn(
                                    modifier =
                                        Modifier.fillMaxSize()
                                ) {

                                    items(genres) {
                                            genre ->

                                        val genreTrackCount =
                                            tracks.count {
                                                it.genre == genre
                                            }

                                        ListItem(

                                            headlineContent = {

                                                Text(
                                                    text =
                                                        genre,
                                                    fontFamily =
                                                        OneUISansFamily,
                                                    fontWeight =
                                                        FontWeight.Bold
                                                )
                                            },

                                            supportingContent = {

                                                Text(
                                                    text =
                                                        "$genreTrackCount titre" +
                                                                if (
                                                                    genreTrackCount > 1
                                                                ) {
                                                                    "s"
                                                                } else {
                                                                    ""
                                                                },
                                                    fontFamily =
                                                        OneUISansFamily
                                                )
                                            },

                                            modifier =
                                                Modifier.clickable {

                                                    onLibraryGenreChanged(
                                                        genre
                                                    )
                                                }
                                        )
                                    }
                                }
                            }
                        }

                        // ═══════════════════════════════
                        // PRODUCTEURS
                        // ═══════════════════════════════

                        "Producteurs" -> {

                            val producers = tracks
                                .map { it.composer }
                                .filter {
                                    it.isNotBlank()
                                }
                                .distinct()
                                .sorted()

                            if (producers.isEmpty()) {

                                Text(
                                    text =
                                        "Aucun producteur trouvé. Essayez d'aller dans les paramètres de votre téléphone pour pouvoir autoriser l'accès à votre musique.",
                                    fontFamily =
                                        OneUISansFamily,
                                    fontSize = 16.sp
                                )

                            } else {

                                LazyColumn(
                                    modifier =
                                        Modifier.fillMaxSize()
                                ) {

                                    items(producers) {
                                            producer ->

                                        val producerTrackCount =
                                            tracks.count {
                                                it.composer == producer
                                            }

                                        ListItem(

                                            headlineContent = {

                                                Text(
                                                    text =
                                                        producer,
                                                    fontFamily =
                                                        OneUISansFamily,
                                                    fontWeight =
                                                        FontWeight.Bold
                                                )
                                            },

                                            supportingContent = {

                                                Text(
                                                    text =
                                                        "$producerTrackCount titre" +
                                                                if (
                                                                    producerTrackCount > 1
                                                                ) {
                                                                    "s"
                                                                } else {
                                                                    ""
                                                                },
                                                    fontFamily =
                                                        OneUISansFamily
                                                )
                                            },

                                            modifier =
                                                Modifier.clickable {

                                                    onLibraryComposerChanged(
                                                        producer
                                                    )
                                                }
                                        )
                                    }
                                }
                            }
                        }

                        // ═══════════════════════════════
                        // DOSSIERS
                        // ═══════════════════════════════

                        "Dossiers" -> {

                            Text(
                                text =
                                    "Impossible d'accédez aux dossiers. Veuillez accorder l'autorisation ''Musique et audio'' dans les paramètres. Si vous avez déjà accordé les autorisations, reportez-vous à la section Aide.",
                                fontFamily =
                                    OneUISansFamily,
                                fontWeight =
                                    FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // ═══════════════════════════════════════
            // ÉCRAN PRODUCTEUR
            // ═══════════════════════════════════════

            if (
                selectedCategory == "Producteurs" &&
                selectedComposer != null
            ) {

                val composer =
                    selectedComposer

                val composerTracks =
                    tracks.filter {
                        it.composer == composer
                    }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.background
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.fillMaxSize()
                    ) {

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 50.dp,
                                        start = 16.dp,
                                        end = 16.dp
                                    ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            IconButton(
                                onClick = {
                                    onLibraryComposerChanged(null)
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.ArrowBack,
                                    contentDescription =
                                        "Retour"
                                )
                            }

                            Text(
                                text = composer,
                                fontFamily =
                                    OneUISansFamily,
                                fontWeight =
                                    FontWeight.Bold,
                                fontSize = 28.sp,
                                maxLines = 1,
                                overflow =
                                    TextOverflow.Ellipsis
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        LazyColumn(
                            modifier =
                                Modifier.fillMaxSize()
                        ) {

                            items(
                                items = composerTracks,
                                key = { it.id }
                            ) { track ->

                                ListItem(

                                    headlineContent = {

                                        Text(
                                            text =
                                                track.title,
                                            fontFamily =
                                                OneUISansFamily,
                                            fontWeight =
                                                FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    },

                                    supportingContent = {

                                        Text(
                                            text =
                                                track.artist,
                                            fontFamily =
                                                OneUISansFamily,
                                            maxLines = 1
                                        )
                                    },

                                    modifier =
                                        Modifier.clickable {

                                            onPlaylistTrackSelected(
                                                track,
                                                composerTracks
                                            )
                                        }
                                )
                            }
                        }
                    }
                }
            }

            // ═══════════════════════════════════════
            // ÉCRAN GENRE
            // ═══════════════════════════════════════

            if (
                selectedCategory == "Genres" &&
                selectedGenre != null
            ) {

                val genre =
                    selectedGenre

                val genreTracks =
                    tracks.filter {
                        it.genre == genre
                    }

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .background
                            )
                ) {

                    Column(
                        modifier =
                            Modifier.fillMaxSize()
                    ) {

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 50.dp,
                                        start = 16.dp,
                                        end = 16.dp
                                    ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            IconButton(
                                onClick = {
                                    onLibraryGenreChanged(null)
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.ArrowBack,
                                    contentDescription =
                                        "Retour"
                                )
                            }

                            Text(
                                text = genre,
                                fontFamily =
                                    OneUISansFamily,
                                fontWeight =
                                    FontWeight.Bold,
                                fontSize = 28.sp,
                                maxLines = 1
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        LazyColumn(
                            modifier =
                                Modifier.fillMaxSize()
                        ) {

                            items(
                                items = genreTracks,
                                key = { it.id }
                            ) { track ->

                                ListItem(

                                    headlineContent = {

                                        Text(
                                            text =
                                                track.title,
                                            fontFamily =
                                                OneUISansFamily,
                                            fontWeight =
                                                FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    },

                                    supportingContent = {

                                        Text(
                                            text =
                                                track.artist,
                                            fontFamily =
                                                OneUISansFamily,
                                            maxLines = 1
                                        )
                                    },

                                    modifier =
                                        Modifier.clickable {

                                            onPlaylistTrackSelected(
                                                track,
                                                genreTracks
                                            )
                                        }
                                )
                            }
                        }
                    }
                }
            }

            // ═══════════════════════════════════════
            // ÉCRAN ALBUM
            // ═══════════════════════════════════════

            if (
                selectedCategory == "Albums" &&
                selectedAlbum != null
            ) {

                val album =
                    selectedAlbum

                val albumTracks =
                    tracks
                        .filter {
                            it.album == album
                        }
                        .sortedBy {
                            it.trackNumber
                        }

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .background
                            )
                ) {

                    Column(
                        modifier =
                            Modifier.fillMaxSize()
                    ) {

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 50.dp,
                                        start = 16.dp,
                                        end = 16.dp
                                    ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            IconButton(
                                onClick = {
                                    onLibraryAlbumChanged(null)
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.ArrowBack,
                                    contentDescription =
                                        "Retour"
                                )
                            }

                            Text(
                                text = album,
                                fontFamily =
                                    OneUISansFamily,
                                fontWeight =
                                    FontWeight.Bold,
                                fontSize = 28.sp,
                                maxLines = 1
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        LazyColumn(
                            modifier =
                                Modifier.fillMaxSize()
                        ) {

                            items(
                                items = albumTracks,
                                key = { it.id }
                            ) { track ->

                                ListItem(

                                    headlineContent = {

                                        Text(
                                            text =
                                                "${track.trackNumber}. ${track.title}",
                                            fontFamily =
                                                OneUISansFamily,
                                            fontWeight =
                                                FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    },

                                    supportingContent = {

                                        Text(
                                            text =
                                                track.artist,
                                            fontFamily =
                                                OneUISansFamily,
                                            maxLines = 1
                                        )
                                    },

                                    modifier =
                                        Modifier.clickable {

                                            onPlaylistTrackSelected(
                                                track,
                                                albumTracks
                                            )
                                        }
                                )
                            }
                        }
                    }
                }
            }

            // ═══════════════════════════════════════
            // ÉCRAN ARTISTE
            // ═══════════════════════════════════════

            if (
                selectedCategory == "Artistes" &&
                selectedArtist != null
            ) {

                val artist =
                    selectedArtist

                val artistTracks =
                    tracks.filter {
                        it.artist == artist
                    }

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .background
                            )
                ) {

                    Column(
                        modifier =
                            Modifier.fillMaxSize()
                    ) {

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 50.dp,
                                        start = 16.dp,
                                        end = 16.dp
                                    ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            IconButton(
                                onClick = {
                                    onLibraryArtistChanged(null)
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.ArrowBack,
                                    contentDescription =
                                        "Retour"
                                )
                            }

                            Text(
                                text = artist,
                                fontFamily =
                                    OneUISansFamily,
                                fontWeight =
                                    FontWeight.Bold,
                                fontSize = 28.sp,
                                maxLines = 1
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        if (
                            artistTracks.isEmpty()
                        ) {

                            Box(
                                modifier =
                                    Modifier.fillMaxSize(),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text =
                                        "Aucun titre trouvé.",
                                    fontFamily =
                                        OneUISansFamily,
                                    fontSize = 16.sp
                                )
                            }

                        } else {

                            LazyColumn(
                                modifier =
                                    Modifier.fillMaxSize()
                            ) {

                                items(
                                    items = artistTracks,
                                    key = { it.id }
                                ) { track ->

                                    ListItem(

                                        headlineContent = {

                                            Text(
                                                text =
                                                    track.title,
                                                fontFamily =
                                                    OneUISansFamily,
                                                fontWeight =
                                                    FontWeight.Bold,
                                                maxLines = 1
                                            )
                                        },

                                        supportingContent = {

                                            Text(
                                                text =
                                                    track.artist,
                                                fontFamily =
                                                    OneUISansFamily,
                                                maxLines = 1
                                            )
                                        },

                                        modifier =
                                            Modifier.clickable {

                                                onPlaylistTrackSelected(
                                                    track,
                                                    artistTracks
                                                )
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ═══════════════════════════════════════
            // DIALOGUE D'AJOUT À LA PLAYLIST
            // ═══════════════════════════════════════

            if (
                showAddToPlaylistDialog &&
                selectedPlaylist != null
            ) {

                val currentPlaylist =
                    selectedPlaylist!!

                val availableTracks =
                    tracks.filter { track ->
                        track.id !in currentPlaylist.trackIds
                    }

                AlertDialog(

                    onDismissRequest = {
                        showAddToPlaylistDialog =
                            false
                    },

                    title = {

                        Text(
                            text =
                                "Ajouter à la playlist",
                            fontFamily =
                                OneUISansFamily,
                            fontWeight =
                                FontWeight.Bold
                        )
                    },

                    text = {

                        if (
                            availableTracks.isEmpty()
                        ) {

                            Text(
                                text =
                                    "Tous les titres sont déjà dans cette playlist.",
                                fontFamily =
                                    OneUISansFamily,
                                fontSize = 16.sp
                            )

                        } else {

                            LazyColumn(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .heightIn(
                                            max = 400.dp
                                        )
                            ) {

                                items(
                                    items =
                                        availableTracks,
                                    key = { it.id }
                                ) { track ->

                                    ListItem(

                                        headlineContent = {

                                            Text(
                                                text =
                                                    track.title,
                                                fontFamily =
                                                    OneUISansFamily,
                                                fontWeight =
                                                    FontWeight.Bold,
                                                maxLines = 1
                                            )
                                        },

                                        supportingContent = {

                                            Text(
                                                text =
                                                    track.artist,
                                                fontFamily =
                                                    OneUISansFamily,
                                                maxLines = 1
                                            )
                                        },

                                        modifier =
                                            Modifier.clickable {

                                                playlistManager
                                                    .addTrackToPlaylist(
                                                        playlistId =
                                                            currentPlaylist.id,
                                                        trackId =
                                                            track.id
                                                    )

                                                playlists =
                                                    playlistManager
                                                        .getPlaylists()

                                                selectedPlaylist =
                                                    playlists.firstOrNull {
                                                        it.id ==
                                                                currentPlaylist.id
                                                    }

                                                showAddToPlaylistDialog =
                                                    false
                                            }
                                    )
                                }
                            }
                        }
                    },

                    confirmButton = {

                        TextButton(
                            onClick = {
                                showAddToPlaylistDialog =
                                    false
                            }
                        ) {

                            Text(
                                text =
                                    "Fermer",
                                fontFamily =
                                    OneUISansFamily,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                )
            }

            // ═══════════════════════════════════════
            // DIALOGUE DE CRÉATION DE PLAYLIST
            // ═══════════════════════════════════════

            if (showCreatePlaylistDialog) {

                AlertDialog(

                    onDismissRequest = {
                        showCreatePlaylistDialog =
                            false
                    },

                    title = {

                        Text(
                            text =
                                "Créer une playlist",
                            fontFamily =
                                OneUISansFamily,
                            fontWeight =
                                FontWeight.Bold
                        )
                    },

                    text = {

                        OutlinedTextField(

                            value =
                                playlistName,

                            onValueChange = {
                                playlistName = it
                            },

                            singleLine = true,

                            label = {

                                Text(
                                    text =
                                        "Nom de la playlist",
                                    fontFamily =
                                        OneUISansFamily
                                )
                            }
                        )
                    },

                    confirmButton = {

                        TextButton(

                            onClick = {

                                val name =
                                    playlistName.trim()

                                if (
                                    name.isNotEmpty()
                                ) {

                                    playlistManager
                                        .createPlaylist(
                                            name
                                        )

                                    playlists =
                                        playlistManager
                                            .getPlaylists()

                                    showCreatePlaylistDialog =
                                        false
                                }
                            }
                        ) {

                            Text(
                                text =
                                    "Créer",
                                fontFamily =
                                    OneUISansFamily,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    },

                    dismissButton = {

                        TextButton(

                            onClick = {
                                showCreatePlaylistDialog =
                                    false
                            }
                        ) {

                            Text(
                                text =
                                    "Annuler",
                                fontFamily =
                                    OneUISansFamily
                            )
                        }
                    }
                )
            }
        }
    }


        @Composable
        fun SettingsScreen(
            modifier: Modifier = Modifier,
            onBack: () -> Unit,
            themeMode: AppPreferences.ThemeMode,
            onThemeModeChanged: (AppPreferences.ThemeMode) -> Unit
        ) {
            var showThemeModeMenu by remember {
                mutableStateOf(false)
            }
            val context = LocalContext.current

            val tracks = remember {
                loadMusicTracks(context)
            }

            val playlistManager = remember {
                PlaylistManager(context)
            }
            val playlists = remember {
                playlistManager.getPlaylists()
            }
            var playlistToExport by remember {
                mutableStateOf<Playlist?>(null)
            }
            var showExportPlaylistDialog by remember {
                mutableStateOf(false)
            }

            val exportLauncher =
                rememberLauncherForActivityResult(
                    contract =
                        ActivityResultContracts.CreateDocument(
                            "audio/x-mpegurl"
                        )
                ) { uri ->

                    if (uri != null && playlistToExport != null) {

                        exportPlaylist(
                            context = context,
                            uri = uri,
                            playlist = playlistToExport!!,
                            tracks = tracks
                        )

                        playlistToExport = null
                    }
                }
            val importLauncher =
                rememberLauncherForActivityResult(
                    contract =
                        ActivityResultContracts.OpenDocument()
                ) { uri ->

                    if (uri != null) {

                        importPlaylist(
                            context = context,
                            uri = uri,
                            playlistManager = playlistManager,
                            tracks = tracks
                        )
                    }
                }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 50.dp
                    )
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }

                    Text(
                        text = "Paramètres",
                        fontFamily = OneUISansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(32.dp)
                )
                Text(
                    text = "Playlists",
                    fontFamily = OneUISansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
                ListItem(
                    headlineContent = {
                        Text(
                            text = "Importer une playlist",
                            fontFamily = OneUISansFamily,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "Importer une playlist au format M3U",
                            fontFamily = OneUISansFamily
                        )
                    },
                    modifier = Modifier.clickable {
                        importLauncher.launch(
                            arrayOf(
                                "audio/x-mpegurl",
                                "audio/mpegurl",
                                "application/vnd.apple.mpegurl",
                                "text/plain"
                            )
                        )
                    }
                )
                ListItem(
                    headlineContent = {
                        Text(
                            text = "Exporter une playlist",
                            fontFamily = OneUISansFamily,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "Exporter une playlist au format M3U",
                            fontFamily = OneUISansFamily
                        )
                    },
                    modifier = Modifier.clickable {
                        showExportPlaylistDialog = true
                    }
                )
                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                Text(
                    text = "Apparence",
                    fontFamily = OneUISansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                ListItem(
                    headlineContent = {
                        Text(
                            text = "Thème de l'application",
                            fontFamily = OneUISansFamily,
                            fontWeight = FontWeight.Bold
                        )
                    },

                    supportingContent = {
                        Text(
                            text = when (themeMode) {
                                AppPreferences.ThemeMode.SYSTEM -> "Système"
                                AppPreferences.ThemeMode.LIGHT -> "Clair"
                                AppPreferences.ThemeMode.DARK -> "Sombre"
                            },
                            fontFamily = OneUISansFamily
                        )
                    },

                    modifier = Modifier.clickable {
                        showThemeModeMenu = true
                    }
                )
                if (showThemeModeMenu) {
                    AlertDialog(
                        onDismissRequest = {
                            showThemeModeMenu = false
                        },
                        title = {
                            Text(
                                text = "Thème de l'application",
                                fontFamily = OneUISansFamily,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column {

                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = "Système",
                                            fontFamily = OneUISansFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    leadingContent = {
                                        RadioButton(
                                            selected =
                                                themeMode ==
                                                        AppPreferences.ThemeMode.SYSTEM,
                                            onClick = null
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        onThemeModeChanged(
                                            AppPreferences.ThemeMode.SYSTEM
                                        )
                                        showThemeModeMenu = false
                                    }
                                )

                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = "Clair",
                                            fontFamily = OneUISansFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    leadingContent = {
                                        RadioButton(
                                            selected =
                                                themeMode ==
                                                        AppPreferences.ThemeMode.LIGHT,
                                            onClick = null
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        onThemeModeChanged(
                                            AppPreferences.ThemeMode.LIGHT
                                        )
                                        showThemeModeMenu = false
                                    }
                                )

                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = "Sombre",
                                            fontFamily = OneUISansFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    leadingContent = {
                                        RadioButton(
                                            selected =
                                                themeMode ==
                                                        AppPreferences.ThemeMode.DARK,
                                            onClick = null
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        onThemeModeChanged(
                                            AppPreferences.ThemeMode.DARK
                                        )
                                        showThemeModeMenu = false
                                    }
                                )
                            }
                        },
                        confirmButton = {}
                    )
                }
            }
            if (showExportPlaylistDialog) {

                AlertDialog(
                    onDismissRequest = {
                        showExportPlaylistDialog = false
                    },

                    title = {
                        Text(
                            text = "Exporter une playlist",
                            fontFamily = OneUISansFamily,
                            fontWeight = FontWeight.Bold
                        )
                    },

                    text = {

                        Column {

                            playlists.forEach { playlist ->

                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = playlist.name,
                                            fontFamily = OneUISansFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },

                                    supportingContent = {
                                        Text(
                                            text =
                                                "${playlist.trackIds.size} titre" +
                                                        if (playlist.trackIds.size > 1) {
                                                            "s"
                                                        } else {
                                                            ""
                                                        },
                                            fontFamily = OneUISansFamily
                                        )
                                    },

                                    modifier = Modifier.clickable {

                                        playlistToExport = playlist

                                        showExportPlaylistDialog = false

                                        exportLauncher.launch(
                                            "${playlist.name}.m3u"
                                        )
                                    }
                                )
                            }
                        }
                    },

                    confirmButton = {
                        TextButton(
                            onClick = {
                                showExportPlaylistDialog = false
                            }
                        ) {
                            Text(
                                text = "Annuler",
                                fontFamily = OneUISansFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
        }
    }