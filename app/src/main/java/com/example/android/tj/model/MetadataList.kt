package com.example.android.tj.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder

class MetadataList {
    var metadataList: MutableList<Metadata> = mutableListOf()

    override fun toString(): String {
        return GsonBuilder().setPrettyPrinting().create().toJson(this)
    }

    fun getByHash(hash: String): Metadata? {
        return metadataList.find { it.md5Hash == hash }
    }

    companion object {

        fun fromJson(jsonStr: String): MetadataList {
            return Gson().fromJson(jsonStr, MetadataList::class.java)
        }
    }
}

