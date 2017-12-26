package com.eland.android.eoas.Adapt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView

import com.eland.android.eoas.Model.ProxyInfo
import com.eland.android.eoas.Model.ProxyUserInfo
import com.eland.android.eoas.R
import com.rey.material.widget.Spinner

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by liu.wenbin on 16/1/6.
 */
class ProxyAdapt(private val context: Context, private val list: List<ProxyInfo>, private val startDate: String, private val endDate: String) : BaseAdapter() {
    private val layoutInflater: LayoutInflater

    init {
        this.layoutInflater = LayoutInflater.from(context)
    }


    override fun getCount(): Int {
        return if (list.size == 0) 0 else list.size
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
            view = layoutInflater.inflate(R.layout.setproxy_item_layout, null)
            viewHolder = ViewHolder(view)
            view!!.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val proxyInfo = list[i]

        viewHolder.txtOrgName!!.text = proxyInfo.OrgName
        viewHolder.txtOrgCode!!.text = proxyInfo.OrgCode
        viewHolder.txtStart!!.text = startDate
        viewHolder.txtEnd!!.text = endDate

        val proxyUser = arrayOfNulls<String>(proxyInfo.proxyInfoList!!.size)
        var dto: ProxyUserInfo
        for (j in proxyUser.indices) {
            dto = proxyInfo!!.proxyInfoList!![j]
            proxyUser[j] = dto.UserName
        }

        val adapter = ArrayAdapter<String>(context,
                R.layout.vacation_spinner_layout, proxyUser)
        adapter.setDropDownViewResource(R.layout.vacation_spinner_dropdown)
        viewHolder.spinnerProxy!!.adapter = adapter

        return view
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'setproxy_item_layout.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    internal class ViewHolder(view: View) {
        @BindView(R.id.txt_orgName)
        lateinit var txtOrgName: TextView
        @BindView(R.id.txt_orgCode)
        lateinit var txtOrgCode: TextView
        @BindView(R.id.spinner_proxy)
        lateinit var spinnerProxy: Spinner
        @BindView(R.id.txt_start)
        lateinit var txtStart: TextView
        @BindView(R.id.txt_end)
        lateinit var txtEnd: TextView

        init {
            ButterKnife.bind(this, view)
        }
    }
}
