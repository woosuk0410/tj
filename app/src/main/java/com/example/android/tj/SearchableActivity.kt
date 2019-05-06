package com.example.android.tj

import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android.tj.Constants.INTENT_PARAM_HASH
import com.example.android.tj.Constants.SERVICE_ANSWER
import com.example.android.tj.Constants.SERVICE_ANSWER_SEARCH
import com.example.android.tj.Constants.SERVICE_CMD
import com.example.android.tj.Constants.SERVICE_CMD_PLAY_FROM_HASH
import com.example.android.tj.Constants.SERVICE_QUERY_SEARCH
import com.example.android.tj.model.TJServiceCommand
import com.example.android.tj.model.TJServiceSearchResult
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.util.*

class SearchableActivity : AppCompatActivity() {

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val searchResultStr = intent.getStringExtra(SERVICE_ANSWER_SEARCH) ?: return

            currentResult = Gson().fromJson(searchResultStr, TJServiceSearchResult::class.java)
            adapter.clear()
            adapter.addAll(currentResult.fileNames)
            adapter.notifyDataSetChanged()

            val snackbar = Snackbar.make(findViewById<View>(R.id.list_searchable_files),
                    currentResult.hashes.size.toString() + " found.", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }
    }

    internal lateinit var adapter: ArrayAdapter<String>
    internal lateinit var currentResult: TJServiceSearchResult

    private fun playFromHash(hash: String) {
        val intent = Intent(this, TJService::class.java)
        intent.putExtra(SERVICE_CMD, TJServiceCommand(SERVICE_CMD_PLAY_FROM_HASH, hash)
                .toString())
        startService(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                IntentFilter(SERVICE_ANSWER))

        // list view
        val lv = findViewById<ListView>(R.id.list_searchable_files)
        adapter = ArrayAdapter(this, R.layout.activity_listview, LinkedList())
        lv.adapter = adapter
        lv.setOnItemClickListener { parent, view, position, id ->
            val hash = currentResult.hashes[position]
            playFromHash(hash)
            finish()
        }

        lv.setOnItemLongClickListener { parent, view, position, id ->
            val intent = Intent(applicationContext, MetadataActivity::class.java)
            val hash = currentResult.hashes[position]
            intent.putExtra(INTENT_PARAM_HASH, hash)
            startActivity(intent)
            true
        }

        // search
        val intent = intent
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            sendSearchQuery(query)
        }
    }


    override fun onNewIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            sendSearchQuery(query)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // https://developer.android.com/guide/topics/search/search-dialog#java
        // https://developer.android.com/training/search/setup#java

        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false) // Do not iconify the widget; expand it by default

        return true
    }

    private fun sendSearchQuery(query: String) {
        val intent = Intent(this, TJService::class.java)
        intent.putExtra(SERVICE_CMD, TJServiceCommand(SERVICE_QUERY_SEARCH, query).toString())
        startService(intent)
    }
}
