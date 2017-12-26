package com.eland.android.eoas.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast

import com.eland.android.eoas.Adapt.ScheduleAdapt
import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.Model.ScheduleInfo
import com.eland.android.eoas.R
import com.eland.android.eoas.Service.SearchScheduleService
import com.eland.android.eoas.Util.ProgressUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.ToastUtil
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshLayout
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshListener
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter
import com.rey.material.app.DatePickerDialog
import com.rey.material.app.Dialog
import com.rey.material.app.DialogFragment

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

/**
 * Created by liu.wenbin on 15/12/24.
 */
class SearchScheduleFragment : Fragment, SearchScheduleService.IOnSearchScheduleListener {

    @BindView(R.id.edit_startdate)
    lateinit var editStartdate: EditText
    @BindView(R.id.edit_enddate)
    lateinit var editEnddate: EditText
    @BindView(R.id.img_search)
    lateinit var imgSearch: ImageView
    @BindView(R.id.listView)
    lateinit var listView: ListView
    @BindView(R.id.refresh)
    lateinit var refresh: MaterialRefreshLayout

    private var rootView: View? = null
    private var refreshType = REFRESHTYEP.NONE
    private var startDate: String? = null
    private var endDate: String? = null
    private var mAdapt: ScheduleAdapt? = null
    private var mList: List<ScheduleInfo>? = null
    private var mUserId: String? = null
    private var httpDialog: android.app.Dialog? = null
    private var searchScheduleService: SearchScheduleService? = null

    enum class REFRESHTYEP {
        REFRESH, LOAD_MORE, NONE
    }

    constructor() : super() {}

    @SuppressLint("ValidFragment")
    constructor(context: Context) {
        //this.context = context;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.searchschedule_fragment, null)
        ButterKnife.bind(this, rootView!!)

        searchScheduleService = SearchScheduleService(context!!)

        initView()
        initListener()
        initAdapter()

        return rootView
    }

    private fun initView() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(System.currentTimeMillis())
        val nowDate = simpleDateFormat.format(date)

        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 30)

        startDate = simpleDateFormat.format(cal.time)
        endDate = nowDate
        editStartdate!!.setText(startDate)
        editEnddate!!.setText(endDate)

        mUserId = SharedReferenceHelper.getInstance(context!!).getValue(Constant.LOGINID)
    }

    private fun initAdapter() {
        mList = ArrayList()

        mAdapt = ScheduleAdapt(context, mList!!)
        val animAdapter = ScaleInAnimationAdapter(mAdapt)
        animAdapter.absListView = listView
        animAdapter.setInitialDelayMillis(300)
        listView!!.adapter = animAdapter
    }

    private fun initListener() {
        refresh!!.setMaterialRefreshListener(object : MaterialRefreshListener() {
            override fun onfinish() {
                super.onfinish()
            }

            override fun onRefresh(materialRefreshLayout: MaterialRefreshLayout) {
                refresh!!.finishRefreshLoadMore()
                refreshType = REFRESHTYEP.REFRESH

                mAdapt!!.notifyDataSetChanged()
                getData()
            }

            override fun onRefreshLoadMore(materialRefreshLayout: MaterialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout)

                refresh!!.finishRefresh()
                refreshType = REFRESHTYEP.REFRESH

                Handler().postDelayed({
                    if (null != refresh) {
                        refresh!!.finishRefreshLoadMore()
                    }
                }, 3000)

                getData()
            }
        })
    }

    @OnClick(R.id.edit_startdate) internal fun setStartDate() {
        val builder = object : DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker) {
            override fun onPositiveActionClicked(fragment: DialogFragment) {
                val dialog = fragment.dialog as DatePickerDialog

                val format = SimpleDateFormat("yyyy-MM-dd")
                startDate = dialog.getFormattedDate(format)
                editStartdate!!.setText(startDate)
                super.onPositiveActionClicked(fragment)
            }

            override fun onNegativeActionClicked(fragment: DialogFragment) {
                //Toast.makeText(fragment.getDialog().getContext(), "Cancelled" , Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment)
            }
        }

        builder.positiveAction("确定")
                .negativeAction("取消")
        val fragment = DialogFragment.newInstance(builder)
        fragment.show(fragmentManager, null)
    }

    @OnClick(R.id.edit_enddate) internal fun setEndDate() {
        val builder = object : DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker) {
            override fun onPositiveActionClicked(fragment: DialogFragment) {
                val dialog = fragment.dialog as DatePickerDialog

                val format = SimpleDateFormat("yyyy-MM-dd")
                endDate = dialog.getFormattedDate(format)
                editEnddate!!.setText(endDate)
                super.onPositiveActionClicked(fragment)
            }

            override fun onNegativeActionClicked(fragment: DialogFragment) {
                //Toast.makeText(fragment.getDialog().getContext(), "Cancelled" , Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment)
            }
        }

        builder.positiveAction("确定")
                .negativeAction("取消")
        val fragment = DialogFragment.newInstance(builder)
        fragment.show(fragmentManager, null)
    }

    @OnClick(R.id.img_search) internal fun searchSchedule() {
        httpDialog = ProgressUtil().showHttpLoading(getContext())
        getData()
    }

    private fun getData() {
        searchScheduleService!!.searchSchedule(mUserId!!, startDate!!, endDate!!, this)
    }

    private fun clearLoading() {
        if (null != refresh) {
            refresh!!.finishRefresh()
            refresh!!.finishRefreshLoadMore()
        }
        if (httpDialog != null && httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }
    }

    override fun onSuccess(list: List<ScheduleInfo>) {
        clearLoading()

        if (null != list && list.size > 0) {
            mList = list
            mAdapt!!.setList(mList!!)
            mAdapt!!.notifyDataSetChanged()
        } else {
            ToastUtil.showToast(context!!, "没有可查询的数据", Toast.LENGTH_SHORT)
        }
    }

    override fun onFailure(code: Int, msg: String) {
        clearLoading()
        ToastUtil.showToast(context!!, msg, Toast.LENGTH_SHORT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchScheduleService!!.cancel()
//        ButterKnife.unbind(this)
    }

    companion object {

        fun newInstance(args: Bundle): SearchScheduleFragment {
            val f = SearchScheduleFragment()
            f.arguments = args
            return f
        }
    }
}
