package com.example.android.tj.activity.ui.playing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.tj.Constants
import com.example.android.tj.R
import com.example.android.tj.activity.FrameActivity
import com.example.android.tj.activity.TJServiceBroadcastReceiver
import com.example.android.tj.activity.TJServiceClientUtil
import com.example.android.tj.model.TJServiceCommand
import com.example.android.tj.model.TJServiceStatus

class NowPlayingFragment : Fragment(), TJServiceClientUtil, TJServiceBroadcastReceiver {

    private lateinit var homeViewModel: NowPlayingViewModel

    private lateinit var switch: Switch
    lateinit var seekBar: SeekBar
    lateinit var nowPlaying: TextView
    val handler: Handler = Handler()


    private val uiUpdateCallback = object : Runnable {
        override fun run() {
            val syncCmd = TJServiceCommand(Constants.SERVICE_CMD_SYNC)
            sendCmdToTJService(activity, syncCmd)
            handler.postDelayed(this, 1000)
        }
    }

    override val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getStringExtra(Constants.SERVICE_RESULT_STATUS) ?: return
            updateUI(TJServiceStatus.fromJson(status))
        }
    }
    override val intentActions: List<String> = listOf(Constants.SERVICE_RESULT)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(NowPlayingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_playing, container, false)

        switch = root.findViewById(R.id.switch_v2)
        seekBar = root.findViewById(R.id.seekBar_v2)
        nowPlaying = root.findViewById(R.id.now_playing_v2)

        registerBroadCastReceiver(activity)

        initUI()
        initPollingThread()
        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        this.handler.removeCallbacksAndMessages(null)
    }

    private fun initUI() {
        //switch
        switch.isChecked = true

        //now playing
        nowPlaying.setOnLongClickListener { _ ->
            val intent = Intent(activity!!.applicationContext, FrameActivity::class.java)
            startActivity(intent)
            true
        }

        //seek bar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val cmd = TJServiceCommand(Constants.SERVICE_CMD_SEEK, progress * 1000)
                    sendCmdToTJService(activity, cmd)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                handler.removeCallbacksAndMessages(null)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                handler.postDelayed(uiUpdateCallback, 1000)
            }
        })
    }

    private fun updateUI(status: TJServiceStatus) {
        //switch
        switch.isChecked = status.isPlaying

        //now playing
        nowPlaying.text = status.nowPlaying

        //seek bar
        seekBar.max = status.duration / 1000
        seekBar.progress = status.currentPosition / 1000
    }

    private fun initPollingThread() {
        activity?.runOnUiThread(uiUpdateCallback)
    }

}