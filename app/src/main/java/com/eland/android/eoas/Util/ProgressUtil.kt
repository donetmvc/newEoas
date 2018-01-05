package com.eland.android.eoas.Util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View

import com.eland.android.eoas.R
import com.eland.android.eoas.Service.UpdatePhoneNmService
import com.rey.material.widget.ProgressView
import com.victor.loading.rotate.RotateLoading
import com.wang.avi.AVLoadingIndicatorView

import me.drakeet.materialdialog.MaterialDialog

/**
 * Created by liu.wenbin on 15/11/12.
 * 加载框
 */
class ProgressUtil {
    fun setOnDialogConfirmListener(lister: IOnDialogConfirmListener) {
        iOnDialogConfirmListener = lister
    }

    fun setOnMainUpdateListener(lister: IOnMainUpdateListener) {
        iOnMainUpdateListener = lister
    }

    interface IOnDialogConfirmListener {
        fun OnDialogConfirmListener()
    }

    interface IOnMainUpdateListener {
        fun onUpdateSuccess()
    }

    fun showProgress(context: Context): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.progress_view, null)
        progressView = view.findViewById(R.id.progress) as ProgressView
        progressView!!.start()
        val dialog = Dialog(context, R.style.CustomDialog)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()

        return dialog
    }

    fun showHttpLoading(context: Context): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.http_loading, null)

        rotateLoading = view.findViewById(R.id.rotateloading) as RotateLoading
        rotateLoading!!.start()
        val dialog = Dialog(context, R.style.CustomDialog)
        dialog.setContentView(view)

        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()

        return dialog
    }

    fun showDialogUpdate(message: String, title: String, context: Context): MaterialDialog {
        val dialog = MaterialDialog(context)
        dialog.setTitle(title)
        dialog.setMessage(message)

        dialog.setPositiveButton(context.resources.getString(R.string.update)) {
            if (null != iOnDialogConfirmListener) {
                iOnDialogConfirmListener!!.OnDialogConfirmListener()
            }
        }.setNegativeButton(context.resources.getString(R.string.refuse)) { dialog.dismiss() }

        return dialog
    }

    fun showDialogUpdateForMain(message: String, title: String, context: Context): MaterialDialog {
        val dialog = MaterialDialog(context)
        dialog.setTitle(title)
        dialog.setMessage(message)

        dialog.setPositiveButton(context.resources.getString(R.string.update)) {
            if (null != iOnMainUpdateListener) {
                iOnMainUpdateListener!!.onUpdateSuccess()
            }
        }.setNegativeButton(context.resources.getString(R.string.refuse)) { dialog.dismiss() }

        return dialog
    }

    companion object {
        private var progressView: ProgressView? = null
        private var rotateLoading: RotateLoading? = null

        var iOnDialogConfirmListener: IOnDialogConfirmListener? = null
        var iOnMainUpdateListener: IOnMainUpdateListener? = null
        private lateinit var dialog: Dialog //= Dialog(contexts, R.style.CustomDialog)

        fun loading(context: Context, message: String? = null, title: String? = null, type: String? = null): Dialog {
            val res: Int = if(type != null) {
                when(type) {
                    "progress" -> R.layout.avloading_view
                    "loading" -> R.layout.http_loading
                    else -> {
                        R.layout.avloading_view
                    }
                }
            }
            else {
                R.layout.avloading_view
            }
            val inflater = LayoutInflater.from(context)
            val subView = inflater.inflate(res, null)
            val avi = subView.findViewById(R.id.avi) as AVLoadingIndicatorView
            avi.smoothToShow()
            val dialog = Dialog(context, R.style.CustomDialog)
            dialog.setContentView(avi)

            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)

            return dialog
        }
    }
}
