package com.eland.android.eoas.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.eland.android.eoas.Util.ConsoleUtil

import me.leolin.shortcutbadger.ShortcutBadger

/**
 * Created by liu.wenbin on 16/1/8.
 */
class DeskCountChangeReceiver : BroadcastReceiver() {

    private val TAG = "EOAS"

    override fun onReceive(context: Context, intent: Intent?) {

        if (null != intent) {
            val action = intent.action
            if (null != action && action == "EOAS_COUNT_CHANGED") {
                ConsoleUtil.i(TAG, "========received count change============")
                val count = Integer.valueOf(intent.getStringExtra("COUNT"))!!
                ConsoleUtil.i(TAG, "========received count change============" + count)
                if (count > 0) {
                    ShortcutBadger.with(context.applicationContext).count(count) //for 1.1.3
                } else {
                    ShortcutBadger.with(context.applicationContext).remove()  //for 1.1.3
                }
            }
        }
    }
}
