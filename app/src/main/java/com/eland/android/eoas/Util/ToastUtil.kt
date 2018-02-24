package com.eland.android.eoas.Util

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.eland.android.eoas.Model.Constant

import com.eland.android.eoas.R

/**
 * Created by liu.wenbin on 15/11/10.
 */
class ToastUtil {

    companion object {

        fun showToast(context: Context, textId: Int, showTime: Int) {
            val toast = Toast(context)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.toast_view, null)
            val textView = view.findViewById(R.id.toast_text) as TextView
            textView.setText(textId)
            toast.view = view
            toast.duration = showTime

            toast.show()
        }

        fun showToast(context: Context, str: String, showTime: Int) {

            val them = SharedReferenceHelper.getInstance(context).getValue(Constant.EOAS_THEME)

            val toast = Toast(context)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.toast_view, null)
            val textView = view.findViewById(R.id.toast_text) as TextView
            textView.text = str
            if(them.isEmpty() || them == "READ") {
                textView.setTextColor(Color.parseColor("#FFF44336"))
            }
            else {
                textView.setTextColor(Color.parseColor("#FF21ACE3"))
            }
            toast.view = view
            toast.duration = showTime

            toast.show()
        }
    }

}

