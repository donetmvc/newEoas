package com.eland.android.eoas.Util

import android.util.Log

/**
 * Created by liu.wenbin on 15/11/10.
 */
object ConsoleUtil {

    private val DEBUG = true

    //verbose
    fun v(tag: String, msg: String) {
        if (DEBUG) {
            Log.v(tag, msg)
        }

    }

    //debug
    fun d(tag: String, msg: String) {
        if (DEBUG) {
            Log.d(tag, msg)
        }

    }

    //info
    fun i(tag: String, msg: String) {
        if (DEBUG) {
            Log.i(tag, msg)
        }

    }

    //warn
    fun w(tag: String, msg: String) {
        if (DEBUG) {
            Log.w(tag, msg)
        }

    }
}
