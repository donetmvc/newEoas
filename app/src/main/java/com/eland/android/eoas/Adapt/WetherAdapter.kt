package com.eland.android.eoas.Adapt

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eland.android.eoas.Model.ForeCastDay
import com.eland.android.eoas.R
import kotlinx.android.synthetic.main.wether_item_layout.view.*

/**
 * Created by liuwenbin on 2018/1/3.
 * 虽然青春不在，但不能自我放逐.
 */
class WetherAdapter(val items: List<ForeCastDay>) : RecyclerView.Adapter<WetherAdapter.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.wether_item_layout, parent, false)
        return ViewHolder(view)
    }


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(wether: ForeCastDay) {
            view.wether_date.text = wether.date.toString()
            view.wether_week.text = "星期一"
            view.wether_tips.text = wether.day.condition.text
            view.wether_temp.text = "${wether.day.maxtemp_c}/${wether.day.mintemp_c}C°"
            view.wether_wind.text = "${wether.day.maxwind_mph}/${wether.day.maxwind_mph}m/s"
        }
    }
}

