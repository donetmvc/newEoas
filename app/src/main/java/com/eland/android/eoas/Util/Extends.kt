package com.eland.android.eoas.Util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by liuwenbin on 2018/2/23.
 * 虽然青春不在，但不能自我放逐.
 */
fun Long.formatDate(format: String? = "yyyy-MM-dd"): String {
    return SimpleDateFormat(format, Locale.CHINA).format(System.currentTimeMillis())
}


fun String.parseTime(format: String? = "yyyy-MM-dd"): Long {
    return SimpleDateFormat(format, Locale.CHINA).parse(this).time
}

