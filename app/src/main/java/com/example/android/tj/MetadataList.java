package com.example.android.tj;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class MetadataList {
    List<Metadata> metadataList;

    static MetadataList fromJson(String jsonStr) {
        return new Gson().fromJson(jsonStr, MetadataList.class);
    }

    @NonNull
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    Metadata getByHash(String hash) {
        return metadataList.stream().filter(metadata -> metadata.md5Hash.equals(hash)).collect
                (Collectors.toList()).get(0);
    }
}

class Metadata {
    String md5Hash;
    int priority;
    public String name;
}