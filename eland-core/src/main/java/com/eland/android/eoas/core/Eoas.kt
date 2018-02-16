package com.eland.android.eoas.core

import com.joanzapata.iconify.IconFontDescriptor
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule

/**
 * Created by liuwenbin on 18/2/16.
 */
object Eoas {

    private var map = HashMap<String, Any>()

    fun initIcon() {
        Iconify.with(FontAwesomeModule())
    }
}