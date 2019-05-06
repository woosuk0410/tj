package com.example.android.tj.model;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.gson.Gson;

import java.util.List;
import java.util.stream.Collectors;

public class TJServiceSearchResult {
    public List<String> fileNames;
    public List<String> hashes;


    public TJServiceSearchResult(List<Pair<String, String>> pairs) {
        this.hashes = pairs.stream().map(pair -> pair.first).collect(Collectors.toList());
        this.fileNames = pairs.stream().map(pair -> pair.second).collect(Collectors.toList());
    }

    @NonNull
    public String toString() {
        return new Gson().toJson(this);
    }

    public static TJServiceStatus fromJson(String jsonStr) {
        return new Gson().fromJson(jsonStr, TJServiceStatus.class);
    }
}
