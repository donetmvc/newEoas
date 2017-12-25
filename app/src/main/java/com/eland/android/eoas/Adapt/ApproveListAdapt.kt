package com.eland.android.eoas.Adapt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.eland.android.eoas.Model.ApproveListInfo
import com.eland.android.eoas.R

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by liu.wenbin on 16/1/7.
 */
class ApproveListAdapt(private val context: Context, private val list: List<ApproveListInfo>) : BaseAdapter() {
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
            view = layoutInflater.inflate(R.layout.approvelist_item_layout, null)
            viewHolder = ViewHolder(view)
            view!!.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val dto = list[i]

        viewHolder.txtApplicant!!.text = dto.applyUserName
        viewHolder.txtRemark!!.text = dto.reason
        viewHolder.txtVacationdays!!.text = dto.vacationDays + "天"

        if (dto.vacationType!!.isEmpty() || dto.vacationType == "null") {
            viewHolder.txtVacationName!!.text = dto.vacationTypeName
        } else {
            viewHolder.txtVacationName!!.text = dto.vacationType
        }

        viewHolder.txtVacationperiod!!.text = dto.applyPeriod
        viewHolder.txtApplyId!!.text = dto.applyId

        return view
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'approvelist_item_layout.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    internal class ViewHolder(view: View) {
        @BindView(R.id.txt_applicant)
        var txtApplicant: TextView? = null
        @BindView(R.id.txt_vacationName)
        var txtVacationName: TextView? = null
        @BindView(R.id.txt_vacationdays)
        var txtVacationdays: TextView? = null
        @BindView(R.id.txt_vacationperiod)
        var txtVacationperiod: TextView? = null
        @BindView(R.id.txt_remark)
        var txtRemark: TextView? = null
        @BindView(R.id.txt_applyId)
        var txtApplyId: TextView? = null

        init {
            ButterKnife.bind(this, view)
        }
    }
}
