package com.eland.android.eoas.Adapt

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.eland.android.eoas.Model.LoginInfo
import com.eland.android.eoas.R
import com.eland.android.eoas.Util.ImageLoaderOption
import com.eland.android.eoas.Util.ToastUtil
import com.eland.android.eoas.Views.CircleImageView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by liu.wenbin on 15/12/15.
 */
class ContactAdapt : BaseAdapter {

    private val TAG = "EOAS"
    private var context: Context? = null
    private var imageLoader: ImageLoader? = null
    private var options: DisplayImageOptions? = null
    private var mInflater: LayoutInflater? = null
    private var lists: List<LoginInfo>? = null

    constructor(context: Context) {
        this.context = context
        this.mInflater = LayoutInflater.from(context)
    }

    constructor(context: Context, list: List<LoginInfo>, imageLoader: ImageLoader) {
        this.context = context
        this.lists = list
        this.imageLoader = imageLoader
        this.mInflater = LayoutInflater.from(context)
        this.options = ImageLoaderOption.getOptionsById(R.mipmap.eland_log)
    }

    fun setList(list: List<LoginInfo>) {
        this.lists = list
    }

    override fun getCount(): Int {
        return if (lists!!.isEmpty()) 0 else lists!!.size
    }

    override fun getItem(i: Int): Any {
        return i
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view

        val viewHolder: ViewHolder

        if (null == view) {
            view = mInflater!!.inflate(R.layout.contact_item_layout, null)
            viewHolder = ViewHolder(view)
            view!!.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val dto = lists!![i]
        viewHolder.txtName.text = dto.userName
        viewHolder.txtEmail.text = dto.email
        viewHolder.txtTel.text = dto.cellNo
        imageLoader?.displayImage(dto.url, viewHolder.imgPhoto, options)

        viewHolder.itemActionCall.setOnClickListener(View.OnClickListener {
            //ToastUtil.showToast(context, "打电话咯", Toast.LENGTH_LONG);
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + dto.cellNo))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@OnClickListener
            }
            context!!.startActivity(intent)
        })

        viewHolder.itemActionEmail.setOnClickListener {
            //ToastUtil.showToast(context, "发邮件咯", Toast.LENGTH_LONG);
            sendEmail(dto.email!!)
        }

        viewHolder.itemActionText.setOnClickListener {
            //ToastUtil.showToast(context, "发短信咯", Toast.LENGTH_LONG);
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto://" + dto.cellNo))
            context!!.startActivity(intent)
        }

        return view
    }

    /**
     * 发送邮件
     */
    private fun sendEmail(email: String) {
        val uri = Uri.parse("mailto:" + email)
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        val pm = context!!.packageManager
        val infos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (infos == null || infos.size <= 0) {
            ToastUtil.showToast(context!!, "请先安装邮件应用并设置账户", Toast.LENGTH_SHORT)
            return
        }
        context!!.startActivity(intent)
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'contact_item_layout.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    internal class ViewHolder(view: View) {
        @BindView(R.id.img_photo)
        lateinit var imgPhoto: CircleImageView
        @BindView(R.id.txt_name)
        lateinit var txtName: TextView
        @BindView(R.id.txt_email)
        lateinit var txtEmail: TextView
        @BindView(R.id.txt_tel)
        lateinit var txtTel: TextView
        @BindView(R.id.item_txt_call)
        lateinit var itemTxtCall: TextView
        @BindView(R.id.item_action_call)
        lateinit var itemActionCall: LinearLayout
        @BindView(R.id.item_txt_text)
        lateinit var itemTxtText: TextView
        @BindView(R.id.item_action_text)
        lateinit var itemActionText: LinearLayout
        @BindView(R.id.item_txt_email)
        lateinit var itemTxtEmail: TextView
        @BindView(R.id.item_action_email)
        lateinit var itemActionEmail: LinearLayout

        init {
            ButterKnife.bind(this, view)
        }
    }
}
