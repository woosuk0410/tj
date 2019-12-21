package com.example.android.tj

import android.app.Notification
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.example.android.tj.Contexts.singleThreadContext
import com.example.android.tj.database.SongMetadata
import com.example.android.tj.model.MetadataModel
import com.example.android.tj.model.SongModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


internal class Nodes(tjService: TJService) {

    var normalList: List<SongMetadata> = emptyList()
    var selectedList: List<SongMetadata> = emptyList()
    var currentList: List<SongMetadata>
        get() {
            return if (currentListMode == CurrentListMode.Normal) normalList else selectedList
        }
        set(value) {
            if (currentListMode == CurrentListMode.Normal) {
                normalList = value
            } else {
                selectedList = value
            }
        }
    private var currentListMode: CurrentListMode = CurrentListMode.Normal
    fun switchCurrentListMode(target: CurrentListMode) {
        if (currentListMode != target) currentListMode = target
    }

    private val tjNotification: TJNotification = TJNotification(this, tjService)

    private val metadataModel: MetadataModel = MetadataModel()
    private val songModel: SongModel = SongModel()


    var hasStarted = false // if the player finished loading the 1st resource

    val last: SongMetadata?
        get() = currentList.lastOrNull()

    val notification: Notification
        get() = tjNotification.notification

    val bitMap: Bitmap
        get() {
            var bitmap = BitmapFactory.decodeFile("$TJ_DIR_IMG/tj2.png")

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
                currentList = list
                priorityShuffle()
                start() // similar to Constants.SERVICE_CMD_START in main activity. whoever gets here actually start the first song
            }
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
        return currentList.find { it.id == hash }
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
    }

    fun addToSelectedList(metadata: SongMetadata) {
        selectedList = selectedList + metadata
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
            val n = currentNode()
            songModel.getById(n.id) { song ->
                song?.let {
                    PlayerSemaphore.lock.acquire()
                    player.reset()
                    player.setDataSource(ByteArrayMediaDataSource(it.data()))
                    player.prepareAsync()

                    // setOnPreparedListener and setOnPreparedListener only need to be called once
                    player.setOnPreparedListener { player ->
                        player.start()
                        PlayerSemaphore.lock.release()
                    }
                    player.setOnCompletionListener { _ ->
                        GlobalScope.launch(singleThreadContext) {
                            val n2 = forwardNode()
                            songModel.getById(n2.id) { songOp ->
                                songOp?.let { it ->
                                    PlayerSemaphore.lock.acquire()
                                    player.reset()
                                    player.setDataSource(ByteArrayMediaDataSource(it.data()))
                                    player.prepareAsync()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {

        private val EXT_DIR = Environment.getExternalStorageDirectory().absolutePath
        val TJ_DIR_IMG = "$EXT_DIR/tj_img"

        lateinit var player: PlayerWrapper
    }
}
