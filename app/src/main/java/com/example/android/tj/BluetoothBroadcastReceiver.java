package com.example.android.tj;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    private Nodes nodes;

    BluetoothBroadcastReceiver(Nodes nodes) {
        this.nodes = nodes;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        switch (Objects.requireNonNull(action)) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                nodes.play();
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                nodes.pause();
                break;
            default:
        }

    }
}
