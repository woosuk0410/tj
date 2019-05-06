package com.example.android.tj

import android.app.Notification
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import com.example.android.tj.model.Metadata
import com.example.android.tj.model.MetadataList
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Semaphore


internal class Nodes(private val tjService: TJService) {

    var nodes: MutableList<Node> = Collections.synchronizedList(LinkedList())
    private val tjNotification: TJNotification = TJNotification(this, tjService)

    var semaphore = Semaphore(0)

    var hasStarted = false // if the player finished loading the 1st resource

    val last: Node
        get() = nodes[nodes.size - 1]

    val notification: Notification
        get() = tjNotification.notification

    val bitMap: Bitmap
        get() {
            var bitmap = BitmapFactory.decodeFile("$TJ_DIR_IMG/tj2.png")

            val hash = currentNode().metadata.md5Hash
            val curPos = Nodes.player.currentPosition
            val frameFile = String.format("%s-%03d.jpg", hash, curPos / 1000 / 5 + 1)
            val fullPath = "$TJ_DIR_IMG/$frameFile"
            val f = File(fullPath)
            if (f.exists()) {
                bitmap = BitmapFactory.decodeFile(fullPath)
            }
            return bitmap
        }

    internal inner class Node(var file: File) {
        lateinit var metadata: Metadata

        init {

            try {
                val md5 = String(Hex.encodeHex(DigestUtils.md5(Files
                        .readAllBytes(this.file.toPath()))))
                this.metadata = Metadata(md5, 0, this.file.name)
            } catch (e: Exception) {
                e.printStackTrace()
                System.exit(1)
            }

        }
    }

    init {

        val fileLoadingThread = Thread {
            val files = File(TJ_DIR).listFiles()

            val preloadNum = 41

            try {
                for (file in files) {
                    if (nodes.size >= preloadNum) {
                        semaphore.acquire()
                    }
                    nodes.add(0, Node(file))
                    if (nodes.size >= preloadNum) {
                        semaphore.release()
                    }
                }

                //remove duplicated nodes
                this.deDuplicate()

                //read from/write to metadata
                val metadtaFile = File(METADATA_FILE_PATH)
                if (metadtaFile.exists()) {
                    val jsonStr = String(Files.readAllBytes(Paths.get(METADATA_FILE_PATH)),
                            Charset.forName("UTF-8"))

                    val ml = MetadataList.fromJson(jsonStr)

                    //match with existing nodes
                    for (n in nodes) {
                        val metadataOp = ml.getByHash(n.metadata.md5Hash)
                        metadataOp?.let { metadata ->
                            n.metadata = metadata
                            ml.metadataList.add(metadata)
                        }
                    }
                    val fos = FileOutputStream(metadtaFile)
                    fos.write(ml.toString().toByteArray(charset("UTF-8")))
                    fos.close()

                } else {
                    val ml = MetadataList()
                    ml.metadataList = nodes.map { it.metadata }.toMutableList()
                    val jsonStr = ml.toString()
                    val fos = FileOutputStream(metadtaFile)
                    fos.write(jsonStr.toByteArray(charset("UTF-8")))
                    fos.close()
                }

                // convert back into non-sync version
                nodes = LinkedList(nodes)
            } catch (e: Exception) {
                e.printStackTrace()
                System.exit(1)
            }
        }

        fileLoadingThread.start()

        try {
            semaphore.acquire()
        } catch (e: Exception) {
            e.printStackTrace()
            System.exit(1)
        }

        this.priorityShuffle()
        semaphore.release()

        player = MediaPlayer()
    }

    private fun deDuplicate() {
        val s = HashSet<String>()
        val toRemove = LinkedList<Node>()
        for (n in nodes) {
            if (s.contains(n.metadata.md5Hash)) {
                toRemove.add(n)
            } else {
                s.add(n.metadata.md5Hash)
            }
        }
        nodes.removeAll(toRemove)
    }

    private fun forwardNode(): Node {
        val head = nodes.removeAt(0)
        nodes.add(head)
        return head
    }

    private fun backwardNode(): Node {
        val tail = nodes.removeAt(nodes.size - 1)
        nodes.add(0, tail)
        return tail
    }

    fun play() {
        player.start()
    }

    fun pause() {
        player.pause()
    }

    operator fun next() {
        this.play(0, true)
    }

    fun previous() {
        this.play(0, false)
    }

    fun playFromLocation(loc: Int) {
        play(loc, true)
    }

    fun playFromHash(hash: String) {
        val loc = nodes.indexOfFirst { it.metadata.md5Hash == hash }
        play(loc, true)
    }

    fun getNodeByHash(hash: String): Node? {
        return nodes.find { it.metadata.md5Hash == hash }
    }

    fun shuffle() {
        nodes.shuffle()
    }

    fun priorityShuffle() {
        val sortedKeys = TreeSet<Int> { i1, i2 -> i2 - i1 }
        sortedKeys.addAll(nodes.map { it.metadata.priority })
        val priorityToNodes = nodes.groupByTo(mutableMapOf()) { it.metadata.priority }

        nodes.clear()
        for (key in sortedKeys) {
            val partial = priorityToNodes[key]
            partial?.shuffle()
            nodes.addAll(partial!!)
        }
    }

    private fun currentNode(): Node {
        return last
    }

    fun currentFile(): File {
        return currentNode().file
    }

    fun UpdateMetadata(metadata: Metadata) {
        for (n in nodes) {
            if (n.metadata.md5Hash == metadata.md5Hash) {
                n.metadata = metadata
            }
        }
    }

    private fun play(startIdx: Int, forward: Boolean) {
        try {
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

            player.reset()
            val n = forwardNode()

            player.setDataSource(tjService, Uri.fromFile(n.file))
            player.prepare()
            player.start()
            player.setOnCompletionListener { finishedPlayer ->
                try {
                    finishedPlayer.reset()
                    val n2 = forwardNode()

                    player.setDataSource(tjService, Uri.fromFile(n2.file))
                    player.prepare()
                    player.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            //TODO: may have a better way
            if (!hasStarted) {
                hasStarted = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        private val EXT_DIR = Environment.getExternalStorageDirectory().absolutePath
        private val TJ_DIR = "$EXT_DIR/tj"
        val TJ_DIR_IMG = "$EXT_DIR/tj_img"
        val METADATA_FILE_PATH = "$EXT_DIR/tj.json"

        lateinit var player: MediaPlayer
    }
}
