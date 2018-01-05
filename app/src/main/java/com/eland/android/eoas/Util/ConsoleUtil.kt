package com.eland.android.eoas.Util

import com.eland.android.eoas.BuildConfig
import timber.log.Timber

/**
 * Created by liu.wenbin on 15/11/10.
 */
object ConsoleUtil {

    private val DEBUG = BuildConfig.DEBUG

    //verbose
    fun v(tag: String, msg: String) {
        if (DEBUG) {
            Timber.v(msg)
        }

    }

    //debug
    fun d(tag: String, msg: String) {
        if (DEBUG) {
            Timber.d(msg)
        }

    }

    //info
    fun i(tag: String, msg: String) {
        if (DEBUG) {
            Timber.i(msg)
        }

    }

    //warn
    fun w(tag: String, msg: String) {
        if (DEBUG) {
            Timber.w(msg)
        }

    }
}
