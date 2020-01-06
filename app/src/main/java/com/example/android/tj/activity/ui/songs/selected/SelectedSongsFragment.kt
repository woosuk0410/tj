package com.example.android.tj.activity.ui.songs.selected


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.tj.Constants
import com.example.android.tj.R
import com.example.android.tj.activity.TJServiceBroadcastReceiver
import com.example.android.tj.activity.TJServiceUtil
import com.example.android.tj.activity.ui.songs.SongsListAdapter
import com.example.android.tj.activity.ui.songs.SongsViewModel
import com.example.android.tj.model.CurrentListMode
import com.example.android.tj.model.TJServiceCommand
import com.example.android.tj.model.TJServiceSongsSyncData
import com.google.android.material.bottomnavigation.BottomNavigationView

//TODO: de-duplicate with SongsFragment
class SelectedSongsFragment : Fragment(), TJServiceUtil, TJServiceBroadcastReceiver {

    private lateinit var model: SongsViewModel
    private lateinit var recyclerView: RecyclerView

    override val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val syncDataWithSelectedListStr = intent
                    .getStringExtra(Constants.SERVICE_RESULT_SONGS_DATA_WITH_METADATA_SELECTED_LIST)
            syncDataWithSelectedListStr?.let {
                val syncData = TJServiceSongsSyncData.fromJson(it)
                val adapter = recyclerView.adapter
                if (adapter == null) {
                    model.songsSyncData.value = syncData
                } else {
                    val currentData = model.songsSyncData.value
                    if (currentData != syncData) {
                        model.songsSyncData.value = syncData
                    }
                }
            }
        }
    }
    override val intentActions: List<String> = listOf(Constants.SERVICE_RESULT)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_songs, container, false)
        val viewManager = LinearLayoutManager(activity)
        recyclerView = root.findViewById<RecyclerView>(R.id.recycler_view_v2).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }
        model = ViewModelProviders.of(this).get(SongsViewModel::class.java)
        val metadataListObserver = Observer<TJServiceSongsSyncData> {
            recyclerView.swapAdapter(
                    SongsListAdapter(this, viewManager, it, CurrentListMode.Selected), false)
        }
        model.songsSyncData.observe(this, metadataListObserver)

        registerBroadCastReceiver(activity)

        val cmd = TJServiceCommand(Constants.SERVICE_CMD_SYNC_SONGS_DATA)
        sendCmdToTJService(activity, cmd)

        val navBar = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        navBar?.visibility = View.GONE

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // this is needed for invoking onCreateOptionsMenu properly
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_selected_songs_fragment, menu)

        val play = menu.findItem(R.id.selected_songs_menu_play)
        play.setOnMenuItemClickListener {
            run {
                sendCmdToTJService(
                        activity, TJServiceCommand(
                        Constants.SERVICE_CMD_SWITCH_TARGET_LIST, CurrentListMode.Selected.value))
                sendCmdToTJService(activity, TJServiceCommand(Constants.SERVICE_CMD_PLAY_FROM_TOP))
                true
            }
        }

        val clear = menu.findItem(R.id.selected_songs_menu_clear)
        clear.setOnMenuItemClickListener {
            run {
                sendCmdToTJService(
                        activity, TJServiceCommand(Constants.SERVICE_CLEAR_SELECTED_LIST))
                sendCmdToTJService(
                        activity, TJServiceCommand(
                        Constants.SERVICE_CMD_SWITCH_TARGET_LIST, CurrentListMode.Normal.value))
                true
            }
        }
    }

}
