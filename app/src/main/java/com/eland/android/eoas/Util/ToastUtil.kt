package com.eland.android.eoas.Util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.eland.android.eoas.R

/**
 * Created by liu.wenbin on 15/11/10.
 */
object ToastUtil {

    private var toast: Toast? = null
    private var textView: TextView? = null

    fun showToast(context: Context, textId: Int, showTime: Int) {
        if (toast == null) {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.toast_view, null)
            textView = view.findViewById(R.id.toast_text) as TextView
            textView!!.setText(textId)
            //textView.setTypeface(ElandApplication.systemTypeface);
            toast = Toast(context)
            toast!!.view = view
            toast!!.duration = showTime
            //   toast.setGravity(Gravity.BOTTOM,0,50);
        } else {
            textView!!.setText(textId)
        }
        toast!!.show()
    }

    fun showToast(context: Context, str: String, showTime: Int) {
        if (toast == null) {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.toast_view, null)
            textView = view.findViewById(R.id.toast_text) as TextView
            textView!!.text = str
            //textView.setTypeface(ElandApplication.systemTypeface);
            toast = Toast(context)
            toast!!.view = view
            toast!!.duration = showTime
            //  toast.setGravity(Gravity.BOTTOM,0,50);
        } else {
            textView!!.text = str
        }
        toast!!.show()
    }
}
