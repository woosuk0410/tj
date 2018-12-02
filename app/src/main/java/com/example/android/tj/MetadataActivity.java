package com.example.android.tj;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static com.example.android.tj.Constants.INTENT_PARAM_POSITION;
import static com.example.android.tj.Constants.SERVICE_ANSWER;
import static com.example.android.tj.Constants.SERVICE_ANSWER_METADATA;
import static com.example.android.tj.Constants.SERVICE_CMD;
import static com.example.android.tj.Constants.SERVICE_PATCH_METADATA;
import static com.example.android.tj.Nodes.METADATA_FILE_PATH;

public class MetadataActivity extends AppCompatActivity {

    private Metadata currentMetadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metadata);

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter(SERVICE_ANSWER));

        int position = getIntent().getIntExtra(INTENT_PARAM_POSITION, 0);
        sendTJServiceCmd(Constants.SERVICE_QUERY_METADATA, position);
    }

    // TODO: duplicated code. Tried Manifold extension class, but couldn't pass the compilition
    private void sendTJServiceCmd(int cmd, int arg1) {
        Intent intent = new Intent(this, TJService.class);
        intent.putExtra(SERVICE_CMD, new TJServiceCommand(cmd, arg1).toString());
        startService(intent);
    }

    private void patchInMemoryMetadata() {
        Intent intent = new Intent(this, TJService.class);
        intent.putExtra(SERVICE_CMD, new TJServiceCommand(SERVICE_PATCH_METADATA, currentMetadata
                .toString()).toString());
        startService(intent);
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String metadataStr = intent.getStringExtra(SERVICE_ANSWER_METADATA);
            Log.w("BroadcastReceiver", "here " + metadataStr);
            Metadata metadata = new Gson().fromJson(metadataStr, Metadata.class);

            currentMetadata = metadata;

            TextView tvName = findViewById(R.id.metadata_name_value);
            tvName.setText(metadata.name);

            TextView tvHash = findViewById(R.id.metadata_hash_value);
            tvHash.setText(metadata.md5Hash);

            TextView tvPriority = findViewById(R.id.metadata_priority_value);
            tvPriority.setText(String.valueOf(metadata.priority));
        }
    };

    @SuppressLint("Assert")
    public void onSave(View view) {
        try {
            File metadtaFile = new File(METADATA_FILE_PATH);
            if (metadtaFile.exists()) {
                String jsonStr = new String(Files.readAllBytes(Paths.get(METADATA_FILE_PATH)),
                        "UTF-8");

                MetadataList ml = MetadataList.fromJson(jsonStr);

                TextView tvHash = findViewById(R.id.metadata_hash_value);
                String hash = tvHash.getText().toString();

                TextView tvPriority = findViewById(R.id.metadata_priority_value);
                int newPriority = Integer.parseInt(tvPriority.getText().toString());

                Optional<Metadata> metadataOp = ml.getByHash(hash);
                metadataOp.ifPresent(metadata -> metadata.priority = newPriority);

                FileOutputStream fos = new FileOutputStream(metadtaFile);
                fos.write(ml.toString().getBytes("UTF-8"));
                fos.close();

                assert (hash.equals(currentMetadata.md5Hash));
                currentMetadata.priority = newPriority;
                patchInMemoryMetadata();

                Snackbar snackbar = Snackbar.make(findViewById(R.id.metadata_save), "Done",
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                throw new Exception("Metadata file doesn't exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
