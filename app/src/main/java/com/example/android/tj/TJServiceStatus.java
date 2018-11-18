package com.example.android.tj;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.List;

class TJServiceStatus {
    List<String> fileNamesWithIdx;
    String nowPlaying;
    int duration;
    int currentPosition;
    boolean isPlaying;
    String md5;


    TJServiceStatus(List<String> fileNamesWithIdx, int duration, int currentPosition, String
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

    static TJServiceStatus fromJson(String jsonStr) {
        return new Gson().fromJson(jsonStr, TJServiceStatus.class);
    }
}
