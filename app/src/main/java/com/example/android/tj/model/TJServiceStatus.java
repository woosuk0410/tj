package com.example.android.tj.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.List;

public class TJServiceStatus {
    public List<String> fileNamesWithIdx;
    public String nowPlaying;
    public int duration;
    public int currentPosition;
    public boolean isPlaying;
    public String md5;


    public TJServiceStatus(List<String> fileNamesWithIdx, int duration, int currentPosition, String
            nowPlaying, boolean isPlaying, String md5) {
        this.fileNamesWithIdx = fileNamesWithIdx;
        this.duration = duration;
        this.currentPosition = currentPosition;
        this.nowPlaying = nowPlaying;
        this.isPlaying = isPlaying;
        this.md5 = md5;
    }

    @NonNull
    public String toString() {
        return new Gson().toJson(this);
    }

    public static TJServiceStatus fromJson(String jsonStr) {
        return new Gson().fromJson(jsonStr, TJServiceStatus.class);
    }
}
