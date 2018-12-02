package com.example.android.tj;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

class TJServiceCommand {
    int cmdCode;
    int arg1;

    String data;

    TJServiceCommand(int cmdCode) {
        this.cmdCode = cmdCode;
    }

    TJServiceCommand(int cmdCode, int arg1) {
        this.cmdCode = cmdCode;
        this.arg1 = arg1;
    }

    TJServiceCommand(int cmdCode, String data) {
        this.cmdCode = cmdCode;
        this.data = data;
    }

    @NonNull
    public String toString() {
        return new Gson().toJson(this);
    }

    static TJServiceCommand fromJson(String jsonStr) {
        return new Gson().fromJson(jsonStr, TJServiceCommand.class);
    }
}
