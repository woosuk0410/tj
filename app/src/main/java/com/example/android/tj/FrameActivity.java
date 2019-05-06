package com.example.android.tj;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.android.tj.model.TJServiceStatus;

import java.io.File;

import static com.example.android.tj.Constants.SERVICE_RESULT;
import static com.example.android.tj.Constants.SERVICE_RESULT_STATUS;
import static com.example.android.tj.Nodes.TJ_DIR_IMG;

public class FrameActivity extends AppCompatActivity {

    ImageView frameView;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String rawStatus = intent.getStringExtra(SERVICE_RESULT_STATUS);
            TJServiceStatus status = TJServiceStatus.fromJson(rawStatus);
//            Log.w("FrameActivity", "here:" + status.currentPosition);

            Bitmap bitmap = BitmapFactory.decodeFile(TJ_DIR_IMG + "/tj2.png");
            @SuppressLint("DefaultLocale")
            String frameFile = String.format("%s-%03d.jpg", status.md5, status.currentPosition /
                    1000 / 5 + 1);
            String fullPath = TJ_DIR_IMG + "/" + frameFile;
            File f = new File(fullPath);
            if (f.exists()) {
                bitmap = BitmapFactory.decodeFile(fullPath);
            }
            frameView.setImageBitmap(bitmap);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
        frameView = findViewById(R.id.imageView);

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter(SERVICE_RESULT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

}
