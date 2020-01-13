package com.example.android.tj.service

import android.app.Notification
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.android.tj.Constants
import com.example.android.tj.Logging
import com.example.android.tj.database.History
import com.example.android.tj.database.SongMetadata
import com.example.android.tj.model.CurrentListMode
import com.example.android.tj.model.TJServiceSongsSyncData
import com.example.android.tj.model.TJServiceStatus
import com.example.android.tj.model.database.HistoryModel
import com.example.android.tj.model.database.MetadataModel
import com.example.android.tj.model.database.SongModel
import com.example.android.tj.service.Contexts.singleThreadContext
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.util.*


internal class Nodes(private val tjService: TJService) : Logging, TJServiceUtil {

    var normalList: List<SongMetadata> = emptyList()
    var selectedList: List<SongMetadata> = emptyList()
    var currentList: List<SongMetadata>
        get() {
            return if (currentListMode == CurrentListMode.Normal || selectedList.isEmpty()) normalList else selectedList
        }
        set(value) {
            if (currentListMode == CurrentListMode.Normal) {
                normalList = value
            } else {
                selectedList = value
            }
            announceBroadcast(tjService, songsSyncBroadcastIntent())
        }
    private var currentListMode: CurrentListMode = CurrentListMode.Normal
    fun switchCurrentListMode(target: CurrentListMode) {
        if (currentListMode != target) currentListMode = target
    }

    private val tjNotification: TJNotification = TJNotification(this, tjService)

    private val metadataModel: MetadataModel = MetadataModel()
    private val songModel: SongModel = SongModel()
    private val historyModel: HistoryModel = HistoryModel()

    // TODO: currently this is passed as sync data all the time, should make it on demand
    var histories: Map<String, List<History>> = emptyMap()

    var hasStarted = false // if the player finished loading the 1st resource

    val notification: Notification
        get() = tjNotification.notification

    val bitMap: Bitmap
        get() {
            var bitmap = BitmapFactory.decodeFile("$TJ_DIR_IMG/tj4.jpg")

            if (currentList.isNotEmpty()) {
                val hash = currentNode().id
                val curPos = player.currentPosition
                val frameFile = String.format("%s-%03d.jpg", hash, curPos / 1000 / 5 + 1)
                val fullPath = "$TJ_DIR_IMG/$frameFile"
                val f = File(fullPath)
                if (f.exists()) {
                    bitmap = BitmapFactory.decodeFile(fullPath)
                }
            }
            return bitmap
        }

    init {
        player = PlayerWrapper()
        GlobalScope.launch {
            metadataModel.getAll { list ->
                normalList = list
                priorityShuffle()
                start()
            }
        }
        GlobalScope.launch {
            historyModel.getAll { histories = it.groupBy { history -> history.id } }
        }
    }

    private fun forwardNode(): SongMetadata {
        val head = currentList.first()
        currentList = currentList.subList(1, currentList.size) + head
        return currentList.first()
    }

    private fun backwardNode(): SongMetadata {
        val tail = currentList.last()
        currentList = listOf(tail) + currentList.subList(0, currentList.size - 1)
        return tail
    }

    fun play() {
        player.start()
    }

    fun pause() {
        player.pause()
    }

    private fun start() {
        if (!hasStarted) {
            hasStarted = true
            this.play(0)
        }
    }

    fun playFromTop() {
        playFromLocation(0)
    }

    fun next() {
        this.play(1)
    }

    fun previous() {
        this.play(0, true)
    }

    fun playFromLocation(loc: Int) {
        play(loc)
    }

    fun playFromHash(hash: String) {
        val loc = currentList.indexOfFirst { it.id == hash }
        play(loc)
    }

    fun getNodeByHash(hash: String): SongMetadata? {
        return normalList.find { it.id == hash }
    }

    fun sortByTitle() {
        val newNodes = currentList.toList()
        currentList = newNodes.sortedBy { it.title }
    }

    fun shuffle() {
        currentList = currentList.shuffled()
    }

    fun priorityShuffle() {
        val sortedKeys = TreeSet<Int> { i1, i2 -> i2 - i1 }
        sortedKeys.addAll(currentList.map { it.priority })
        val priorityToNodes = currentList.groupBy { it.priority }

        var newNodes: List<SongMetadata> = emptyList()
        for (key in sortedKeys) {
            val partial = priorityToNodes[key]
            partial?.let {
                newNodes = newNodes + it.shuffled()
            }
        }
        currentList = newNodes
    }

    fun currentNode(): SongMetadata {
        return currentList.first()
    }

    fun updateMetadata(metadata: SongMetadata) {
        currentList.find { it.id == metadata.id }?.let {
            it.priority = metadata.priority
        }
        announceBroadcast(tjService, songsSyncBroadcastIntent())
    }

    fun addToSelectedList(metadata: SongMetadata) {
        selectedList = selectedList + metadata
        announceBroadcast(tjService, songsSyncBroadcastIntent())
    }

    @Synchronized
    private fun play(startIdx: Int, withOneBackwardStep: Boolean = false) {
        if (this.currentList.isEmpty()) { //TODO: better way to check init
            return
        }

        for (i in 0 until startIdx) {
            forwardNode()
        }

        if (withOneBackwardStep) {
            backwardNode()
        }

        GlobalScope.launch(singleThreadContext) {
            if (player.isPlaying) {
                // switching to a different song while the previous one is still playing,
                // playing time should be accumulated
                player.accumulatePlayedSoFar()
            }
            maybeRecordHistory()
            val n = currentNode()
            songModel.getById(n.id) { song ->
                song?.let {
                    PlayerSemaphore.lock.acquire()
                    player.reset()
                    player.setDataSource(
                            ByteArrayMediaDataSource(
                                    it.data()))
                    player.prepareAsync()
                    player.recordingSong = n
                    announceBroadcast(tjService, songsSyncBroadcastIntent())

                    // setOnPreparedListener and setOnPreparedListener only need to be called once
                    player.setOnPreparedListener { player ->
                        player.start()
                        PlayerSemaphore.lock.release()
                    }
                    player.setOnCompletionListener { _ ->
                        GlobalScope.launch(singleThreadContext) {
                            // this block should always be triggered by a playing state
                            // so always accumulate here
                            player.accumulatePlayedSoFar()
                            maybeRecordHistory()
                            val n2 = forwardNode()
                            songModel.getById(n2.id) { songOp ->
                                songOp?.let { it ->
                                    PlayerSemaphore.lock.acquire()
                                    player.reset()
                                    player.setDataSource(
                                            ByteArrayMediaDataSource(it.data()))
                                    player.prepareAsync()
                                    player.recordingSong = n2
                                    announceBroadcast(tjService, songsSyncBroadcastIntent())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun maybeRecordHistory() {
        val song = player.recordingSong ?: return

        val recordingThresholdSeconds = 60
        if (player.playedSoFarSeconds >= recordingThresholdSeconds) {
            log("saving history for ${player.recordingSong?.title}. played ${player.playedSoFarSeconds}s")
            val history = History(song.id, Instant.now().toString())
            historyModel.insert(history) { success ->
                run {
                    val msg = if (success) "new history saved: ${song.title}" else "history saving failed: ${song.title}"
                    if (success) {
                        updateHistory(history)
                    }
                    Handler(Looper.getMainLooper()).post {
                        val toast = Toast
                                .makeText(tjService.applicationContext, msg, Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }
            }
        } else {
            log("no history is saved for ${player.recordingSong?.title}. only played ${player.playedSoFarSeconds}s")
        }
    }

    private fun updateHistory(history: History) {
        val newHistoryList = histories.getOrElse(history.id) {
            emptyList()
        } + history
        histories = histories + mapOf(Pair(history.id, newHistoryList))
    }

    /************************* broadcast intents begin*******************************/
    fun songsSyncBroadcastIntent(): Intent {
        val dataWithMetadataNormalList = TJServiceSongsSyncData(normalList, histories)
        val dataWithMetadataSelectedList = TJServiceSongsSyncData(selectedList, histories)
        val intent = Intent(Constants.SERVICE_RESULT)
        intent.putExtra(
                Constants.SERVICE_RESULT_SONGS_DATA_WITH_METADATA_NORMAL_LIST,
                dataWithMetadataNormalList.toJsonString())
        intent.putExtra(
                Constants.SERVICE_RESULT_SONGS_DATA_WITH_METADATA_SELECTED_LIST,
                dataWithMetadataSelectedList.toJsonString())
        return intent
    }

    fun playingStatusBroadcastIntent(): Intent {
        val currentStatus: TJServiceStatus = try {
            val duration = player.duration
            val curPos = player.currentPosition
            val nowPlaying = currentNode().title
            val isPlaying = player.isPlaying
            val md5 = currentNode().id
            TJServiceStatus(duration, curPos, nowPlaying, isPlaying, md5)
        } catch (e: Exception) {
            Log.e("TJService", "Exception when generating currentStatus ${e}")
            TJServiceStatus(0, 0, "", false, "")
        }

        val intent = Intent(Constants.SERVICE_RESULT)
        intent.putExtra(Constants.SERVICE_RESULT_STATUS, currentStatus.toString())
        return intent
    }

    fun searchByTitleBroadcastIntent(query: String): Intent {
        val candidates = currentList.filter { n -> n.title.contains(query, true) }
        val result = TJServiceSongsSyncData(candidates, histories)
        val intent = Intent(Constants.SERVICE_ANSWER)
        intent.putExtra(Constants.SERVICE_ANSWER_SEARCH, result.toJsonString())
        return intent
    }

    fun searchByHashBroadcastIntent(hash: String): Intent {
        val intent = Intent(Constants.SERVICE_ANSWER)
        val metadata = getNodeByHash(hash)
        intent.putExtra(Constants.SERVICE_ANSWER_METADATA, Gson().toJson(metadata))
        return intent
    }

    /************************* broadcast intents end*******************************/

    companion object {

        private val EXT_DIR = Environment.getExternalStorageDirectory().absolutePath
        val TJ_DIR_IMG = "$EXT_DIR/tj_img"

        lateinit var player: PlayerWrapper
    }
}
