package com.eland.android.eoas.Receiver

import android.content.Context
import cn.jpush.android.api.JPushMessage
import cn.jpush.android.service.JPushMessageReceiver

/**
 * Created by liuwenbin on 2017/12/27.
 * 虽然青春不在，但不能自我放逐.
 */
class AliasOrTagReceiver: JPushMessageReceiver() {
    override fun onCheckTagOperatorResult(p0: Context?, p1: JPushMessage?) {
        super.onCheckTagOperatorResult(p0, p1)
    }

    override fun onTagOperatorResult(p0: Context?, p1: JPushMessage?) {
        super.onTagOperatorResult(p0, p1)
    }

    override fun onAliasOperatorResult(p0: Context?, p1: JPushMessage?) {
        super.onAliasOperatorResult(p0, p1)
    }


}