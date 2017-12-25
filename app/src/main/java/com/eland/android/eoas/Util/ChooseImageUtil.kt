package com.eland.android.eoas.Util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

import com.eland.android.eoas.R
import com.rey.material.app.Dialog

/**
 * Created by elandmac on 15/12/7.
 */
class ChooseImageUtil {

    private var iOnAlbumListener: IOnAlbumListener? = null
    private var iOnCarmerListener: IOnCarmerListener? = null

    fun showChooseImageDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.chooseimage_layout, null)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setGravity(Gravity.CENTER)

        dialog.setContentView(view)

        val txt_pic_album = view.findViewById(R.id.album_pic) as TextView
        val txt_pic_carmer = view.findViewById(R.id.camera_pic) as TextView

        txt_pic_album.setOnClickListener {
            if (null != iOnAlbumListener) {
                iOnAlbumListener!!.onAlbumClick()
            }
        }

        txt_pic_carmer.setOnClickListener {
            if (null != iOnCarmerListener) {
                iOnCarmerListener!!.onCarmerClick()
            }
        }

        dialog.show()

        return dialog
    }

    fun setOnAlbumListener(iOnAlbumListener: IOnAlbumListener) {
        this.iOnAlbumListener = iOnAlbumListener
    }

    interface IOnAlbumListener {
        fun onAlbumClick()
    }

    fun setOnCarmerListener(iOnCarmerListener: IOnCarmerListener) {
        this.iOnCarmerListener = iOnCarmerListener
    }

    interface IOnCarmerListener {
        fun onCarmerClick()
    }
}
