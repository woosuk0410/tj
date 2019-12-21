package com.example.android.tj.model

import com.google.gson.Gson

class TJServiceCommand {
    var cmdCode: Int = 0
    var arg1: Int = 0

    var data: String = ""

    constructor(cmdCode: Int) {
        this.cmdCode = cmdCode
    }

    //TODO: more general representation for arg1
    // polymorphism?
    constructor(cmdCode: Int, arg1: Int) {
        this.cmdCode = cmdCode
        this.arg1 = arg1
    }

    constructor(cmdCode: Int, data: String) {
        this.cmdCode = cmdCode
        this.data = data
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    companion object {

        fun fromJson(jsonStr: String): TJServiceCommand {
            return Gson().fromJson(jsonStr, TJServiceCommand::class.java)
        }
    }
}
