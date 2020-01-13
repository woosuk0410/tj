package com.example.android.tj.activity.ui.songs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.tj.Constants
import com.example.android.tj.R
import com.example.android.tj.activity.TJServiceBroadcastReceiver
import com.example.android.tj.activity.TJServiceClientUtil
import com.example.android.tj.model.TJServiceSongsSyncData
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main_v2.*

class SongsFragment : Fragment(), TJServiceClientUtil, TJServiceBroadcastReceiver,
                      SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private lateinit var model: SongsViewModel
    private lateinit var recyclerView: RecyclerView
    private var viewMode: ViewMode = ViewMode.NORMAL

    override val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (viewMode) {
                ViewMode.NORMAL -> {
                    val status = intent
                            .getStringExtra(
                                    Constants.SERVICE_RESULT_SONGS_DATA_WITH_METADATA_NORMAL_LIST)
                    status?.let {
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

                    val syncDataWithSelectedListStr = intent
                            .getStringExtra(
                                    Constants.SERVICE_RESULT_SONGS_DATA_WITH_METADATA_SELECTED_LIST)
                    syncDataWithSelectedListStr?.let {
                        val syncData = TJServiceSongsSyncData.fromJson(it)
                        if (syncData.list.isNotEmpty()) {
                            selectedCountButton.text = "${syncData.list.size}"
                        }
                    }
                }
                ViewMode.QUERY  -> {
                    val searchResultStr = intent.getStringExtra(Constants.SERVICE_ANSWER_SEARCH)
                    searchResultStr?.let {
                        model.songsSyncData.value = TJServiceSongsSyncData.fromJson(it)
                    }
                }
            }
        }
    }
    override val intentActions: List<String> = listOf(
            Constants.SERVICE_RESULT, Constants.SERVICE_ANSWER)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_songs, container, false)
        val viewManager = LinearLayoutManager(activity)
        recyclerView = root.findViewById<RecyclerView>(R.id.recycler_view_v2).apply {
            // use this setting to improve performance if changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager
        }

        // subscribe to live data
        model =
                ViewModelProviders.of(this).get(SongsViewModel::class.java)
        val metadataListObserver = Observer<TJServiceSongsSyncData> {
            recyclerView.swapAdapter(SongsListAdapter(this, viewManager, it), false)
        }
        model.songsSyncData.observe(this, metadataListObserver)

        registerBroadCastReceiver(activity)
        syncedSongsDataCmd(activity)


        val navBar = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        navBar?.visibility = View.VISIBLE

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // this is needed for invoking onCreateOptionsMenu properly
    }

    private lateinit var selectedCountButton: Button
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_songs_fragment, menu)

        val selectedList = menu.findItem(R.id.selected_list).actionView
        val notificationView = selectedList
                .findViewById<Button>(R.id.selected_songs_count_notification)
        selectedCountButton = notificationView
        notificationView.text = ""
        selectedCountButton.setOnClickListener {
            run {
                val action = SongsFragmentDirections.actionNavigationSongsToSelectedSongs()
                NavHostFragment.findNavController(nav_host_fragment).navigate(action)
            }
        }

        val searchItem: MenuItem = menu.findItem(R.id.search_v2)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.setOnCloseListener(this)

        when (viewMode) {
            ViewMode.QUERY -> {
                lastQueryText?.let {
                    searchView.setQuery(lastQueryText, false)
                }
            }
            else           -> {
            }
        }
    }

    private var lastQueryText: String? = null
    override fun onQueryTextChange(newText: String?): Boolean {
        viewMode = ViewMode.QUERY
        lastQueryText = newText
        newText?.let { sendSearchQueryCmd(activity, it) }

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onClose(): Boolean {
        viewMode = ViewMode.NORMAL
        return true
    }
}