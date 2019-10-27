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

    var nodes: List<SongMetadata> = emptyList()
    private val tjNotification: TJNotification = TJNotification(this, tjService)

    private val metadataModel: MetadataModel = MetadataModel()
    private val songModel: SongModel = SongModel()


    var hasStarted = false // if the player finished loading the 1st resource

    val last: SongMetadata?
        get() = nodes.lastOrNull()

    val notification: Notification
        get() = tjNotification.notification

    val bitMap: Bitmap
        get() {
            var bitmap = BitmapFactory.decodeFile("$TJ_DIR_IMG/tj2.png")

            if (nodes.isNotEmpty()) {
                val hash = currentNode().id
                val curPos = Nodes.player.currentPosition
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
                nodes = list
                priorityShuffle()
                start() // similar to Constants.SERVICE_CMD_START in main activity. whoever gets here actually start the first song
            }
        }

    }

    private fun forwardNode(): SongMetadata {
        val head = nodes.first()
        nodes = nodes.subList(1, nodes.size) + head
        return head
    }

    private fun backwardNode(): SongMetadata {
        val tail = nodes.last()
        nodes = listOf(tail) + nodes.subList(0, nodes.size - 1)
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
            this.play(0, true)
        }
    }

    fun next() {
        this.play(0, true)
    }

    fun previous() {
        this.play(0, false)
    }

    fun playFromLocation(loc: Int) {
        play(loc, true)
    }

    fun playFromHash(hash: String) {
        val loc = nodes.indexOfFirst { it.id == hash }
        play(loc, true)
    }

    fun getNodeByHash(hash: String): SongMetadata? {
        return nodes.find { it.id == hash }
    }

    fun sortByTitle() {
        val newNodes = nodes.toList()
        nodes = newNodes.sortedBy { it.title }
    }

    fun shuffle() {
        nodes = nodes.shuffled()
    }

    fun priorityShuffle() {
        val sortedKeys = TreeSet<Int> { i1, i2 -> i2 - i1 }
        sortedKeys.addAll(nodes.map { it.priority })
        val priorityToNodes = nodes.groupBy { it.priority }

        var newNodes: List<SongMetadata> = emptyList()
        for (key in sortedKeys) {
            val partial = priorityToNodes[key]
            partial?.let {
                newNodes = newNodes + it.shuffled()
            }
        }
        nodes = newNodes
    }

    fun currentNode(): SongMetadata {
        return nodes.last()
    }

    fun updateMetadata(metadata: SongMetadata) {
        nodes.find { it.id == metadata.id }?.let {
            it.priority = metadata.priority
        }
    }

    @Synchronized
    private fun play(startIdx: Int, forward: Boolean) {
        if (this.nodes.isEmpty()) {//TODO: better way to check init
            return
        }

        for (i in 0 until startIdx) {
            if (forward)
                forwardNode()
            else
                backwardNode()
        }

        //backward needs two more steps
        if (!forward) {
            backwardNode()
            backwardNode()
        }

        GlobalScope.launch(singleThreadContext) {
            val n = forwardNode()
            songModel.getById(n.id) { song ->
                song?.let {
                    PlayerSemaphore.lock.acquire()
                    player.reset()
                    player.setDataSource(ByteArrayMediaDataSource(it.data()))
                    player.prepareAsync()
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
                                    player.setOnPreparedListener { player ->
                                        player.start()
                                        PlayerSemaphore.lock.release()
                                    }
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
