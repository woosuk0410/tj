package com.example.android.tj.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

public class TJServiceCommand {
    public int cmdCode;
    public int arg1;

    public String data;

    public TJServiceCommand(int cmdCode) {
        this.cmdCode = cmdCode;
    }

    public TJServiceCommand(int cmdCode, int arg1) {
        this.cmdCode = cmdCode;
        this.arg1 = arg1;
    }

    public TJServiceCommand(int cmdCode, String data) {
        this.cmdCode = cmdCode;
        this.data = data;
    }

    @NonNull
    public String toString() {
        return new Gson().toJson(this);
    }

    public static TJServiceCommand fromJson(String jsonStr) {
        return new Gson().fromJson(jsonStr, TJServiceCommand.class);
    }
}
