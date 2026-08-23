package com.bonus.musicplayer

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class PlaylistManager(
    private val context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            "BONUS_Music",
            Context.MODE_PRIVATE
        )

    private val playlistsKey =
        "playlists"

    fun getPlaylists(): List<Playlist> {

        val jsonString =
            preferences.getString(
                playlistsKey,
                null
            ) ?: return emptyList()

        return try {

            val jsonArray =
                JSONArray(jsonString)

            List(jsonArray.length()) { index ->

                val jsonObject =
                    jsonArray.getJSONObject(index)

                val trackIdsArray =
                    jsonObject.optJSONArray(
                        "trackIds"
                    )

                val trackIds =
                    if (trackIdsArray != null) {
                        List(trackIdsArray.length()) {
                            trackIdsArray.getLong(it)
                        }
                    } else {
                        emptyList()
                    }

                Playlist(
                    id = jsonObject.getLong("id"),
                    name = jsonObject.getString("name"),
                    trackIds = trackIds
                )
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    fun createPlaylist(
        name: String
    ): Playlist {

        val playlist =
            Playlist(
                id = System.currentTimeMillis(),
                name = name
            )

        savePlaylists(
            getPlaylists() + playlist
        )

        return playlist
    }

    fun deletePlaylist(
        playlistId: Long
    ) {

        val updatedPlaylists =
            getPlaylists().filter {
                it.id != playlistId
            }

        savePlaylists(
            updatedPlaylists
        )
    }

    fun addTrackToPlaylist(
        playlistId: Long,
        trackId: Long
    ) {

        val updatedPlaylists =
            getPlaylists().map { playlist ->

                if (playlist.id == playlistId) {

                    if (trackId !in playlist.trackIds) {

                        playlist.copy(
                            trackIds =
                                playlist.trackIds +
                                        trackId
                        )

                    } else {
                        playlist
                    }

                } else {
                    playlist
                }
            }

        savePlaylists(
            updatedPlaylists
        )
    }

    fun removeTrackFromPlaylist(
        playlistId: Long,
        trackId: Long
    ) {

        val updatedPlaylists =
            getPlaylists().map { playlist ->

                if (playlist.id == playlistId) {

                    playlist.copy(
                        trackIds =
                            playlist.trackIds.filter {
                                it != trackId
                            }
                    )

                } else {
                    playlist
                }
            }

        savePlaylists(
            updatedPlaylists
        )
    }

    private fun savePlaylists(
        playlists: List<Playlist>
    ) {

        val jsonArray =
            JSONArray()

        playlists.forEach { playlist ->

            val jsonObject =
                JSONObject()

            jsonObject.put(
                "id",
                playlist.id
            )

            jsonObject.put(
                "name",
                playlist.name
            )

            val trackIdsArray =
                JSONArray()

            playlist.trackIds.forEach { trackId ->
                trackIdsArray.put(trackId)
            }

            jsonObject.put(
                "trackIds",
                trackIdsArray
            )

            jsonArray.put(
                jsonObject
            )
        }

        preferences
            .edit()
            .putString(
                playlistsKey,
                jsonArray.toString()
            )
            .apply()
    }
}