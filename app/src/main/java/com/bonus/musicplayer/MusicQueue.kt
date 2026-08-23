package com.bonus.musicplayer

enum class RepeatMode {
    OFF,
    ONE,
    ALL
}

class MusicQueue {

    private val queue = mutableListOf<MusicTrack>()
    private val originalQueue = mutableListOf<MusicTrack>()

    var currentIndex: Int = -1
        private set

    var repeatMode: RepeatMode = RepeatMode.OFF
    var isShuffleEnabled: Boolean = false
        private set

    val currentTrack: MusicTrack?
        get() = queue.getOrNull(currentIndex)

    val tracks: List<MusicTrack>
        get() = queue

    fun setQueue(
        tracks: List<MusicTrack>,
        startIndex: Int
    ) {
        queue.clear()
        queue.addAll(tracks)

        originalQueue.clear()
        originalQueue.addAll(tracks)

        currentIndex =
            startIndex.coerceIn(
                0,
                queue.lastIndex
            )

        isShuffleEnabled = false
    }
    fun toggleShuffle() {

        if (queue.isEmpty()) {
            return
        }

        val current = currentTrack

        if (!isShuffleEnabled) {

            queue.shuffle()

            currentIndex =
                queue.indexOfFirst {
                    it.id == current?.id
                }

            isShuffleEnabled = true

        } else {

            queue.clear()
            queue.addAll(originalQueue)

            currentIndex =
                queue.indexOfFirst {
                    it.id == current?.id
                }

            isShuffleEnabled = false
        }
    }
    fun playAt(index: Int): MusicTrack? {

        if (index !in queue.indices) {
            return null
        }

        currentIndex = index

        return currentTrack
    }

    fun next(): MusicTrack? {

        if (queue.isEmpty()) {
            return null
        }

        // 🔂 Répéter le morceau actuel
        if (repeatMode == RepeatMode.ONE) {
            return currentTrack
        }

        // 🎵 Morceau suivant normal
        if (currentIndex < queue.lastIndex) {
            currentIndex++
            return currentTrack
        }

        // 🔁 Revenir au début
        if (repeatMode == RepeatMode.ALL) {
            currentIndex = 0
            return currentTrack
        }

        return null
    }

    fun previous(): MusicTrack? {

        if (queue.isEmpty()) {
            return null
        }

        // 🔂 En mode "répéter le morceau",
        // on reste sur le même morceau.
        if (repeatMode == RepeatMode.ONE) {
            return currentTrack
        }

        if (currentIndex > 0) {
            currentIndex--
            return currentTrack
        }

        // 🔁 En répétition de toute la file,
        // revenir au dernier morceau.
        if (repeatMode == RepeatMode.ALL) {
            currentIndex = queue.lastIndex
            return currentTrack
        }

        return null
    }
    fun toggleRepeatMode() {

        repeatMode =
            when (repeatMode) {

                RepeatMode.OFF ->
                    RepeatMode.ALL

                RepeatMode.ALL ->
                    RepeatMode.ONE

                RepeatMode.ONE ->
                    RepeatMode.OFF
            }
    }
}