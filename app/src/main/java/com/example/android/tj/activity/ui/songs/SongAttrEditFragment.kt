package com.example.android.tj.activity.ui.songs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.android.tj.Constants
import com.example.android.tj.R
import com.example.android.tj.activity.TJServiceBroadcastReceiver
import com.example.android.tj.activity.TJServiceUtil
import com.example.android.tj.database.SongMetadata
import com.example.android.tj.model.MetadataModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SongAttrEditFragment : Fragment(), TJServiceUtil, TJServiceBroadcastReceiver {

    private val args: SongAttrEditFragmentArgs by navArgs()

    private lateinit var currentMetadata: SongMetadata
    private val metadataModel: MetadataModel = MetadataModel()

    private lateinit var rootView: View

    override val intentActions: List<String> = listOf(Constants.SERVICE_ANSWER)
    override val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val metadataStr = intent.getStringExtra(Constants.SERVICE_ANSWER_METADATA) ?: return

            val metadata = Gson().fromJson(metadataStr, SongMetadata::class.java)

            currentMetadata = metadata

            val tvName = rootView.findViewById<TextView>(R.id.metadata_name_value_v2)
            tvName.text = metadata.title

            val tvHash = rootView.findViewById<TextView>(R.id.metadata_hash_value_v2)
            tvHash.text = metadata.id

            val tvPriority = rootView.findViewById<TextView>(R.id.metadata_priority_value_v2)
            tvPriority.text = metadata.priority.toString()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        registerBroadCastReceiver(activity)

        queryMetadataByHash(activity, args.hash)

        val navBar = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        navBar?.visibility = View.GONE

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_song_attr_edit, container, false)
        return rootView
    }

    fun onSave(view: View) {
        val root = view.rootView
        val priorityTextView = root.findViewById<TextView>(R.id.metadata_priority_value_v2)
        val newMetadata = SongMetadata(
                currentMetadata.id, currentMetadata.title,
                Integer.parseInt(priorityTextView.text.toString()))
        GlobalScope.launch {
            metadataModel.insert(newMetadata) { success ->
                val msg = if (success) "Done" else "Failed"
                if (success) {
                    patchInMemoryMetadata(activity, newMetadata)
                }
                val snackBar = Snackbar.make(
                        root.findViewById(R.id.metadata_save_v2), msg,
                        Snackbar.LENGTH_SHORT)
                snackBar.show()
            }
        }
    }
}
