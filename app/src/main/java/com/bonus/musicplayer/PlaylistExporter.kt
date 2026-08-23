package com.bonus.musicplayer

import android.content.Context
import android.net.Uri
import java.io.OutputStreamWriter

fun exportPlaylist(
    context: Context,
    uri: Uri,
    playlist: Playlist,
    tracks: List<MusicTrack>
) {
    val playlistTracks = playlist.trackIds.mapNotNull { id ->
        tracks.firstOrNull { it.id == id }
    }

    context.contentResolver.openOutputStream(uri)?.use { outputStream ->

        OutputStreamWriter(outputStream).use { writer ->

            writer.write("#EXTM3U\n")

            playlistTracks.forEach { track ->

                writer.write(
                    "#EXTINF:${track.duration / 1000},${track.title}\n"
                )

                writer.write("${track.path}\n")
            }
        }
    }
}