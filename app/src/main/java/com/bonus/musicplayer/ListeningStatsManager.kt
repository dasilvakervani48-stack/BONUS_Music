package com.bonus.musicplayer

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ListeningStatsManager(
    private val context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            "BONUS_Music",
            Context.MODE_PRIVATE
        )

    private val listensKey =
        "listening_stats"

    fun registerListen(
        trackId: Long
    ) {

        val date =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(Date())

        val listens =
            getListens().toMutableList()

        listens.add(
            Listen(
                trackId = trackId,
                date = date
            )
        )

        saveListens(listens)
    }

    fun getWeeklyTopTracks(
        tracks: List<MusicTrack>
    ): List<MusicTrack> {

        val calendar =
            Calendar.getInstance()

        calendar.firstDayOfWeek =
            Calendar.MONDAY

        calendar.set(
            Calendar.DAY_OF_WEEK,
            Calendar.MONDAY
        )

        val weekStart =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(calendar.time)

        val currentWeekListens =
            getListens().filter {
                it.date >= weekStart
            }

        val counts =
            currentWeekListens
                .groupingBy {
                    it.trackId
                }
                .eachCount()

        return tracks
            .filter {
                counts.containsKey(it.id)
            }
            .sortedByDescending {
                counts[it.id] ?: 0
            }
            .take(50)
    }

    fun getYearlyTopTracks(
        tracks: List<MusicTrack>
    ): List<MusicTrack> {

        val currentYear =
            Calendar
                .getInstance()
                .get(Calendar.YEAR)
                .toString()

        val currentYearListens =
            getListens().filter {
                it.date.startsWith(
                    "$currentYear-"
                )
            }

        val counts =
            currentYearListens
                .groupingBy {
                    it.trackId
                }
                .eachCount()

        return tracks
            .filter {
                counts.containsKey(it.id)
            }
            .sortedByDescending {
                counts[it.id] ?: 0
            }
            .take(50)
    }

    private fun getListens(): List<Listen> {

        val jsonString =
            preferences.getString(
                listensKey,
                null
            )
                ?: return emptyList()

        return try {

            val jsonArray =
                JSONArray(jsonString)

            List(
                jsonArray.length()
            ) { index ->

                val jsonObject =
                    jsonArray.getJSONObject(
                        index
                    )

                Listen(
                    trackId =
                        jsonObject.getLong(
                            "trackId"
                        ),
                    date =
                        jsonObject.getString(
                            "date"
                        )
                )
            }

        } catch (
            e: Exception
        ) {

            emptyList()
        }
    }

    private fun saveListens(
        listens: List<Listen>
    ) {

        val jsonArray =
            JSONArray()

        listens.forEach { listen ->

            val jsonObject =
                JSONObject()

            jsonObject.put(
                "trackId",
                listen.trackId
            )

            jsonObject.put(
                "date",
                listen.date
            )

            jsonArray.put(
                jsonObject
            )
        }

        preferences
            .edit()
            .putString(
                listensKey,
                jsonArray.toString()
            )
            .apply()
    }
}

data class Listen(
    val trackId: Long,
    val date: String
)