package com.eland.android.eoas.Adapt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.eland.android.eoas.Model.ScheduleInfo
import com.eland.android.eoas.R

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by liu.wenbin on 15/12/25.
 */
class ScheduleAdapt : BaseAdapter {

    private var context: Context? = null
    private var list: List<ScheduleInfo>? = null
    private var layoutInflater: LayoutInflater? = null

    constructor(context: Context) {
        this.context = context
        this.layoutInflater = LayoutInflater.from(context)
    }

    constructor(context: Context, list: List<ScheduleInfo>) {
        this.context = context
        this.list = list
        this.layoutInflater = LayoutInflater.from(context)
    }

    fun setList(list: List<ScheduleInfo>) {
        this.list = list
    }

    override fun getCount(): Int {
        return if (list!!.isEmpty()) 0 else list!!.size
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
            view = layoutInflater!!.inflate(R.layout.schedule_item_layout, null)
            viewHolder = ViewHolder(view)
            view!!.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val scheduleInfo = list!![i]

        if (null != scheduleInfo) {
            viewHolder.txtDate.text = scheduleInfo.date
            viewHolder.txtWork.text = scheduleInfo.work
            viewHolder.txtWorkdes!!.text = scheduleInfo.workdes

            if (scheduleInfo.workdes!!.isEmpty()) {

            } else if (scheduleInfo.workdes == "迟到" || scheduleInfo.workdes == "旷工") {
                viewHolder.txtWorkdes.setTextColor(context!!.resources.getColor(R.color.md_red_500))
                //viewHolder.txtWorkdes.setBackgroundColor(context.getResources().getColor(R.color.md_red_500));
            } else {
                viewHolder.txtWorkdes.setTextColor(context!!.resources.getColor(R.color.green))
            }

            viewHolder.txtOffwork.text = scheduleInfo.offwork
            viewHolder.txtOffworkdes.text = scheduleInfo.offworkdes

            if (scheduleInfo.offworkdes!!.isEmpty()) {

            } else if (scheduleInfo.offworkdes == "早退" || scheduleInfo.offworkdes == "旷工") {
                viewHolder.txtOffworkdes.setTextColor(context!!.resources.getColor(R.color.md_red_500))
            } else {
                viewHolder.txtOffworkdes.setTextColor(context!!.resources.getColor(R.color.green))
            }
        }

        return view
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'schedule_item_layout.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    internal class ViewHolder(view: View) {
        @BindView(R.id.txt_date)
        lateinit var txtDate: TextView
        @BindView(R.id.txt_work)
        lateinit var txtWork: TextView
        @BindView(R.id.txt_offwork)
        lateinit var txtOffwork: TextView
        @BindView(R.id.txt_workdes)
        lateinit var txtWorkdes: TextView
        @BindView(R.id.txt_offworkdes)
        lateinit var txtOffworkdes: TextView

        init {
            ButterKnife.bind(this, view)
        }
    }
}
