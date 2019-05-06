package com.example.android.tj.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

public class Metadata {
    public String md5Hash;
    public int priority;
    public String name;

    @NonNull
    public String toString() {
        return new Gson().toJson(this);
    }
}
