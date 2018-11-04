package com.example.android.tj;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.util.Objects;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    private Nodes nodes;
    private Handler handler;

    BluetoothBroadcastReceiver(Nodes nodes) {
        this.nodes = nodes;
        this.handler = new Handler();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        switch (Objects.requireNonNull(action)) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                this.handler.postDelayed(() -> this.nodes.play(), 12000);
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                nodes.pause();
                break;
            default:
        }
    }
}
