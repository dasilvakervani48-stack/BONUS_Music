package com.bonus.musicplayer

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AppUpdate(
    val version: String,
    val url: String
)

suspend fun checkForUpdate(
    context: Context
): AppUpdate? = withContext(Dispatchers.IO) {

    try {
        val url = URL(
            "https://api.github.com/repos/" +
                    "dasilvakervani48-stack/BONUS_Music/releases/latest"
        )

        val connection =
            url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"

        connection.setRequestProperty(
            "Accept",
            "application/vnd.github+json"
        )

        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        if (
            connection.responseCode !=
            HttpURLConnection.HTTP_OK
        ) {
            connection.disconnect()
            return@withContext null
        }

        val response =
            connection.inputStream
                .bufferedReader()
                .use { it.readText() }

        connection.disconnect()

        val json =
            JSONObject(response)

        val remoteVersion =
            json
                .getString("tag_name")
                .removePrefix("v")

        val releaseUrl =
            json.getString("html_url")

        val currentVersion =
            context
                .packageManager
                .getPackageInfo(
                    context.packageName,
                    0
                )
                .versionName
                ?: return@withContext null

        if (
            isNewerVersion(
                remoteVersion,
                currentVersion
            )
        ) {
            AppUpdate(
                version = remoteVersion,
                url = releaseUrl
            )
        } else {
            null
        }

    } catch (
        e: Exception
    ) {
        null
    }
}

private fun isNewerVersion(
    remoteVersion: String,
    currentVersion: String
): Boolean {

    val remoteParts =
        remoteVersion
            .split(".")
            .map {
                it.toIntOrNull() ?: 0
            }

    val currentParts =
        currentVersion
            .split(".")
            .map {
                it.toIntOrNull() ?: 0
            }

    val maxParts =
        maxOf(
            remoteParts.size,
            currentParts.size
        )

    for (i in 0 until maxParts) {

        val remotePart =
            remoteParts.getOrElse(i) {
                0
            }

        val currentPart =
            currentParts.getOrElse(i) {
                0
            }

        if (
            remotePart >
            currentPart
        ) {
            return true
        }

        if (
            remotePart <
            currentPart
        ) {
            return false
        }
    }

    return false
}