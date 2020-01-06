package com.example.android.tj.service

import android.media.MediaDataSource

class ByteArrayMediaDataSource(private val data: ByteArray) : MediaDataSource() {
    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        if (position >= data.size) {
            return -1
        }
        if (position + size > data.size) {
            val newSize = data.size - position.toInt()
            data.copyInto(buffer, offset, position.toInt(), data.size)
            return newSize
        } else {
            data.copyInto(buffer, offset, position.toInt(), (position + size).toInt())
            return size
        }
    }

    override fun getSize(): Long {
        return data.size.toLong()
    }

    override fun close() {
    }
}