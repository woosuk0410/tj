package com.example.android.tj

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.example.android.tj.Constants.INTENT_PARAM_HASH
import com.example.android.tj.Constants.INTENT_PARAM_POSITION
import com.example.android.tj.Constants.SERVICE_ANSWER
import com.example.android.tj.Constants.SERVICE_ANSWER_METADATA
import com.example.android.tj.Constants.SERVICE_CMD
import com.example.android.tj.Constants.SERVICE_PATCH_METADATA
import com.example.android.tj.Nodes.Companion.METADATA_FILE_PATH
import com.example.android.tj.model.Metadata
import com.example.android.tj.model.MetadataList
import com.example.android.tj.model.TJServiceCommand
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

class MetadataActivity : AppCompatActivity() {

    private lateinit var currentMetadata: Metadata

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val metadataStr = intent.getStringExtra(SERVICE_ANSWER_METADATA) ?: return

            val metadata = Gson().fromJson(metadataStr, Metadata::class.java)

            currentMetadata = metadata

            val tvName = findViewById<TextView>(R.id.metadata_name_value)
            tvName.text = metadata.name

            val tvHash = findViewById<TextView>(R.id.metadata_hash_value)
            tvHash.text = metadata.md5Hash

            val tvPriority = findViewById<TextView>(R.id.metadata_priority_value)
            tvPriority.text = metadata.priority.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metadata)

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                IntentFilter(SERVICE_ANSWER))

        // TODO: remove query by position
        val position = intent.getIntExtra(INTENT_PARAM_POSITION, -1)
        if (position != -1) {
            queryMetadata(position)
        } else {
            val hash = intent.getStringExtra(INTENT_PARAM_HASH)
            queryMetadataByHash(hash)
        }
    }

    // TODO: duplicated code. Tried Manifold extension class, but couldn't pass the compilition
    // should remove after making everything go through hash
    private fun queryMetadata(arg: Int) {
        val intent = Intent(this, TJService::class.java)
        intent.putExtra(SERVICE_CMD, TJServiceCommand(Constants.SERVICE_QUERY_METADATA, arg)
                .toString())
        startService(intent)
    }

    private fun queryMetadataByHash(hash: String) {
        val intent = Intent(this, TJService::class.java)
        intent.putExtra(SERVICE_CMD, TJServiceCommand(Constants.SERVICE_QUERY_METADATA_BY_HASH,
                hash).toString())
        startService(intent)
    }

    private fun patchInMemoryMetadata() {
        val intent = Intent(this, TJService::class.java)
        intent.putExtra(SERVICE_CMD, TJServiceCommand(SERVICE_PATCH_METADATA, currentMetadata
                .toString()).toString())
        startService(intent)
    }

    fun onSave() {
        try {
            val metadtaFile = File(METADATA_FILE_PATH)
            if (metadtaFile.exists()) {
                val jsonStr = String(Files.readAllBytes(Paths.get(METADATA_FILE_PATH)),
                        Charset.forName("UTF-8"))

                val ml = MetadataList.fromJson(jsonStr)

                val tvHash = findViewById<TextView>(R.id.metadata_hash_value)
                val hash = tvHash.text.toString()

                val tvPriority = findViewById<TextView>(R.id.metadata_priority_value)
                val newPriority = Integer.parseInt(tvPriority.text.toString())

                val metadata = ml.getByHash(hash)
                metadata?.priority = newPriority

                val fos = FileOutputStream(metadtaFile)
                fos.write(ml.toString().toByteArray(charset("UTF-8")))
                fos.close()

                assert(hash == currentMetadata.md5Hash)
                currentMetadata.priority = newPriority
                patchInMemoryMetadata()

                val snackbar = Snackbar.make(findViewById(R.id.metadata_save), "Done",
                        Snackbar.LENGTH_SHORT)
                snackbar.show()
            } else {
                throw Exception("Metadata file doesn't exist")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            System.exit(1)
        }

    }

}
