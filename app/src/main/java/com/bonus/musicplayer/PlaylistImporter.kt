package com.bonus.musicplayer

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun importPlaylist(
    context: Context,
    uri: Uri,
    playlistManager: PlaylistManager,
    tracks: List<MusicTrack>
): Boolean {

    val lines =
        context.contentResolver
            .openInputStream(uri)
            ?.bufferedReader()
            ?.readLines()
            ?: return false

    val paths =
        lines
            .filter {
                it.isNotBlank() &&
                        !it.startsWith("#")
            }

    if (paths.isEmpty()) {
        return false
    }

    val playlistName =
        context.contentResolver.query(
            uri,
            arrayOf(
                OpenableColumns.DISPLAY_NAME
            ),
            null,
            null,
            null
        )?.use { cursor ->

            if (cursor.moveToFirst()) {

                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        OpenableColumns.DISPLAY_NAME
                    )
                )

            } else {
                null
            }

        }?.substringBeforeLast(".")
            ?: "Playlist importée"

    playlistManager.createPlaylist(
        playlistName
    )

    val importedPlaylist =
        playlistManager
            .getPlaylists()
            .lastOrNull()
            ?: return false

    paths.forEach { path ->

        val normalizedPath =
            path
                .replace("\\", "/")
                .removePrefix("../../../")
                .removePrefix("../")
                .removePrefix("./")

        val track =
            tracks.firstOrNull { track ->

                val trackPath =
                    track.path
                        .replace("\\", "/")

                trackPath.endsWith(
                    normalizedPath
                )
            }

        if (track != null) {

            playlistManager.addTrackToPlaylist(
                playlistId =
                    importedPlaylist.id,

                trackId =
                    track.id
            )
        }
    }

    return true
}