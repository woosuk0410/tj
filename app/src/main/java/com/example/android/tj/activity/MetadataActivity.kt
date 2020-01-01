package com.example.android.tj.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android.tj.Constants
import com.example.android.tj.Constants.INTENT_PARAM_HASH
import com.example.android.tj.Constants.INTENT_PARAM_POSITION
import com.example.android.tj.Constants.SERVICE_ANSWER
import com.example.android.tj.Constants.SERVICE_ANSWER_METADATA
import com.example.android.tj.Constants.SERVICE_CMD
import com.example.android.tj.Constants.SERVICE_PATCH_METADATA
import com.example.android.tj.R
import com.example.android.tj.TJService
import com.example.android.tj.database.SongMetadata
import com.example.android.tj.model.MetadataModel
import com.example.android.tj.model.TJServiceCommand
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Deprecated("to be removed")
class MetadataActivity : AppCompatActivity() {

    private lateinit var currentMetadata: SongMetadata
    private val metadataModel: MetadataModel = MetadataModel()


    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val metadataStr = intent.getStringExtra(SERVICE_ANSWER_METADATA) ?: return

            val metadata = Gson().fromJson(metadataStr, SongMetadata::class.java)

            currentMetadata = metadata

            val tvName = findViewById<TextView>(R.id.metadata_name_value)
            tvName.text = metadata.title

            val tvHash = findViewById<TextView>(R.id.metadata_hash_value)
            tvHash.text = metadata.id

            val tvPriority = findViewById<TextView>(R.id.metadata_priority_value)
            tvPriority.text = metadata.priority.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metadata)

        LocalBroadcastManager.getInstance(this).registerReceiver(
                messageReceiver,
                IntentFilter(SERVICE_ANSWER))

        // TODO: remove query by position
        val position = intent.getIntExtra(INTENT_PARAM_POSITION, -1)
        if (position != -1) {
            queryMetadata(position)
        } else {
            val hash = intent.getStringExtra(INTENT_PARAM_HASH)
            hash?.let { queryMetadataByHash(it) }
        }
    }

    // TODO: duplicated code. Tried Manifold extension class, but couldn't pass the compilition
    // should remove after making everything go through hash
    private fun queryMetadata(arg: Int) {
        val intent = Intent(this, TJService::class.java)
        intent.putExtra(
                SERVICE_CMD, TJServiceCommand(Constants.SERVICE_QUERY_METADATA, arg)
                .toString())
        startService(intent)
    }

    private fun queryMetadataByHash(hash: String) {
        val intent = Intent(this, TJService::class.java)
        intent.putExtra(
                SERVICE_CMD, TJServiceCommand(
                Constants.SERVICE_QUERY_METADATA_BY_HASH,
                hash).toString())
        startService(intent)
    }

    private fun patchInMemoryMetadata(metadata: SongMetadata) {
        val intent = Intent(this, TJService::class.java)
        intent.putExtra(
                SERVICE_CMD, TJServiceCommand(
                SERVICE_PATCH_METADATA, metadata
                .toString()).toString())
        startService(intent)
    }

    fun onSave(view: View) {

        val priorityTextView = findViewById<TextView>(R.id.metadata_priority_value)
        val newMetadata = SongMetadata(
                currentMetadata.id, currentMetadata.title,
                Integer.parseInt(priorityTextView.text.toString()))
        GlobalScope.launch {
            metadataModel.insert(newMetadata) { success ->
                val msg = if (success) "Done" else "Failed"
                if (success) {
                    patchInMemoryMetadata(newMetadata)
                }
                val snackBar = Snackbar.make(
                        findViewById(R.id.metadata_save), msg,
                        Snackbar.LENGTH_SHORT)
                snackBar.show()
            }
        }
    }

}
