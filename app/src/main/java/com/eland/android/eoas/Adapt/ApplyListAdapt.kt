package com.eland.android.eoas.Adapt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.eland.android.eoas.Model.ApplyListInfo
import com.eland.android.eoas.R

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by elandmac on 15/12/28.
 */
class ApplyListAdapt : BaseAdapter {

    private var context: Context? = null
    private var list: List<ApplyListInfo>? = null
    private var layoutInflater: LayoutInflater? = null

    constructor(context: Context) {
        this.context = context
        this.layoutInflater = LayoutInflater.from(context)
    }

    constructor(context: Context, list: List<ApplyListInfo>) {
        this.context = context
        this.list = list
        this.layoutInflater = LayoutInflater.from(context)
    }

    fun setList(list: List<ApplyListInfo>) {
        this.list = list
    }

    override fun getCount(): Int {
        return if (list!!.size == 0) 0 else list!!.size
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
            view = layoutInflater!!.inflate(R.layout.applylist_item_layout, null)
            viewHolder = ViewHolder(view)
            view!!.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val dto = list!![i]

        if (dto.processStateCode == "01") {
            //审批开始
            viewHolder.imgVacationstate.setImageResource(R.mipmap.apply_wait_icon)
        } else if (dto.processStateCode == "02") {
            //审批进行中
            viewHolder.imgVacationstate.setImageResource(R.mipmap.apply_wait_icon)
        } else {
            //审批结束
            viewHolder.imgVacationstate.setImageResource(R.mipmap.apply_success_icon)
        }

        viewHolder.txtVacationtype.text = dto.vacationTypeName
        viewHolder.txtVacationdays.text = dto.vacationDays + "天"
        viewHolder.txtVacationtperiod.text = dto.vacationPeriod
        viewHolder.txtVacationtremark.text = dto.remarks
        viewHolder.txtVacationno.text = dto.vacationNo

        return view
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'applylist_item_layout.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    internal class ViewHolder(view: View) {
        @BindView(R.id.img_vacationstate)
        lateinit var imgVacationstate: ImageView
        @BindView(R.id.txt_vacationtype)
        lateinit var txtVacationtype: TextView
        @BindView(R.id.txt_vacationdays)
        lateinit var txtVacationdays: TextView
        @BindView(R.id.txt_vacationtperiod)
        lateinit var txtVacationtperiod: TextView
        @BindView(R.id.txt_vacationtremark)
        lateinit var txtVacationtremark: TextView
        @BindView(R.id.txt_vacationno)
        lateinit var txtVacationno: TextView

        init {
            ButterKnife.bind(this, view)
        }
    }
}
