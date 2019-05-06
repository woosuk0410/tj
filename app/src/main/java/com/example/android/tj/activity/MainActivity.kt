package com.example.android.tj.activity

import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android.tj.Constants
import com.example.android.tj.Constants.INTENT_PARAM_POSITION
import com.example.android.tj.Constants.SERVICE_CMD
import com.example.android.tj.Constants.SERVICE_RESULT
import com.example.android.tj.Constants.SERVICE_RESULT_STATUS
import com.example.android.tj.R
import com.example.android.tj.TJService
import com.example.android.tj.model.TJServiceCommand
import com.example.android.tj.model.TJServiceStatus
import java.util.*

class MainActivity : AppCompatActivity() {


    lateinit var switch_: Switch
    lateinit var seekBar: SeekBar
    lateinit var nowPlaying: TextView
    private var handler: Handler? = null
    internal lateinit var adapter: ArrayAdapter<String>


    private val uiUpdateCallback = object : Runnable {
        override fun run() {
            sendTJServiceCmd(Constants.SERVICE_CMD_SYNC)
            handler!!.postDelayed(this, 1000)
        }
    }

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getStringExtra(SERVICE_RESULT_STATUS) ?: return
            updateUI(TJServiceStatus.fromJson(status))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        switch_ = findViewById(R.id.switch1)
        seekBar = findViewById(R.id.seekBar)
        nowPlaying = findViewById(R.id.now_playing)

        sendTJServiceCmd(Constants.SERVICE_CMD_START)


        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                IntentFilter(SERVICE_RESULT))


        initUI()
        initPollingThread()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        this.handler!!.removeCallbacksAndMessages(null)
    }

    //TODO: more general representation for arg1
    private fun sendTJServiceCmd(cmd: Int, arg1: Int) {
        val intent = Intent(this, TJService::class.java)
        intent.putExtra(SERVICE_CMD, TJServiceCommand(cmd, arg1).toString())
        startService(intent)
    }

    private fun sendTJServiceCmd(cmd: Int) {
        val intent = Intent(this, TJService::class.java)
        intent.putExtra(SERVICE_CMD, TJServiceCommand(cmd).toString())
        startService(intent)
    }

    private fun initUI() {
        //switch
        switch_.isChecked = true

        //list view
        val lv = findViewById<ListView>(R.id.list_files)
        adapter = ArrayAdapter(this, R.layout.activity_listview, LinkedList())
        lv.adapter = adapter

        lv.setOnItemClickListener { parent, view, position, id ->
            sendTJServiceCmd(Constants.SERVICE_CMD_PLAY_FROM, position)
            lv.smoothScrollToPosition(0)
        }

        lv.setOnItemLongClickListener { parent, view, position, id ->
            val intent = Intent(applicationContext, MetadataActivity::class.java)
            intent.putExtra(INTENT_PARAM_POSITION, position)
            startActivity(intent)
            true
        }

        //now playing
        nowPlaying.setOnLongClickListener { v ->
            val intent = Intent(applicationContext, FrameActivity::class.java)
            startActivity(intent)
            true
        }

        //seek bar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    sendTJServiceCmd(Constants.SERVICE_CMD_SEEK, progress * 1000)

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                handler!!.removeCallbacksAndMessages(null)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                handler!!.postDelayed(uiUpdateCallback, 1000)
            }
        })
    }

    private fun updateUI(status: TJServiceStatus) {
        //switch
        switch_.isChecked = status.isPlaying

        //list view
        adapter.clear()
        adapter.addAll(status.fileNamesWithIdx)
        adapter.notifyDataSetChanged()

        //now playing
        nowPlaying.text = status.nowPlaying

        //seek bar
        seekBar.max = status.duration / 1000
        seekBar.progress = status.currentPosition / 1000
    }

    private fun initPollingThread() {
        handler = Handler()
        this.runOnUiThread(uiUpdateCallback)
    }


    fun onPriorityShuffle(view: View) {
        sendTJServiceCmd(Constants.SERVICE_CMD_PRIORITY_SHUFFLE)
    }

    fun onShuffle(view: View) {
        sendTJServiceCmd(Constants.SERVICE_CMD_SHUFFLE)
    }

    fun onNowPlayingClick(view: View) {
        val lv = findViewById<ListView>(R.id.list_files)
        lv.setSelection(0)
    }

    fun onSwitch(view: View) {
        if (switch_.isChecked) {
            sendTJServiceCmd(Constants.SERVICE_CMD_PLAY)
        } else {
            sendTJServiceCmd(Constants.SERVICE_CMD_PAUSE)
        }
    }

    fun onSort(view: View) {
        sendTJServiceCmd(Constants.SERVICE_CMD_SORT)
    }

    fun onSearch(item: MenuItem) {
        val intent = Intent(applicationContext, SearchableActivity::class.java)
        intent.action = Intent.ACTION_SEARCH
        intent.putExtra(SearchManager.QUERY, "")
        startActivity(intent)
    }
}
