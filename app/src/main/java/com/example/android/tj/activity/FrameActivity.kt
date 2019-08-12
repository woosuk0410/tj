package com.example.android.tj.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android.tj.Constants.SERVICE_RESULT
import com.example.android.tj.Constants.SERVICE_RESULT_STATUS
import com.example.android.tj.Nodes.Companion.TJ_DIR_IMG
import com.example.android.tj.R
import com.example.android.tj.model.TJServiceStatus
import java.io.File

class FrameActivity : AppCompatActivity() {

    internal lateinit var frameView: ImageView

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val rawStatus = intent.getStringExtra(SERVICE_RESULT_STATUS)
            val status = TJServiceStatus.fromJson(rawStatus)
            //            Log.w("FrameActivity", "here:" + status.currentPosition);

            var bitmap = BitmapFactory.decodeFile("$TJ_DIR_IMG/tj3.jpg")
            @SuppressLint("DefaultLocale")
            val frameFile = String.format("%s-%03d.jpg", status.md5, status
                    .currentPosition /
                    1000 / 5 + 1)
            val fullPath = "$TJ_DIR_IMG/$frameFile"
            val f = File(fullPath)
            if (f.exists()) {
                bitmap = BitmapFactory.decodeFile(fullPath)
            }
            frameView.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)
        frameView = findViewById(R.id.imageView)

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                IntentFilter(SERVICE_RESULT))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }

}
