package com.eland.android.eoas.Fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import com.eland.android.eoas.Activity.ApplyActivity
import com.eland.android.eoas.Activity.ApplyStateActivity
import com.eland.android.eoas.Adapt.ApplyListAdapt
import com.eland.android.eoas.Model.ApplyListInfo
import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.R
import com.eland.android.eoas.Service.ApplyListService
import com.eland.android.eoas.Util.ProgressUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.ToastUtil
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshLayout
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshListener
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by liu.wenbin on 15/12/28.
 */
class ApplyListFragment : Fragment, ApplyListService.IOnSearchApplyListListener {

    @BindView(R.id.listView)
    var listView: ListView? = null
    @BindView(R.id.refresh)
    var refresh: MaterialRefreshLayout? = null
    private var rootView: View? = null

    private var mList: List<ApplyListInfo>? = null
    private var mAdapt: ApplyListAdapt? = null

    internal var httpDialog: Dialog? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var mUserId: String? = null

    private var refreshType = REFRESH_TYPE.RERESH

    private var applyListService: ApplyListService? = null

    enum class REFRESH_TYPE {
        RERESH, LOAD
    }

    constructor() : super() {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.contact_fragment, null)
        ButterKnife.bind(this, rootView!!)

        applyListService = ApplyListService(context)

        initView()
        initAdapt()
        initListener()

        return rootView
    }

    private fun initView() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 30 * 6)

        val cal1 = Calendar.getInstance()
        cal1.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 30 * 6)

        startDate = simpleDateFormat.format(cal.time)
        endDate = simpleDateFormat.format(cal1.time)

        mUserId = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGINID)
    }

    private fun initAdapt() {
        mList = ArrayList()
        initGetData()
        mAdapt = ApplyListAdapt(context!!, mList!!)
        val animAdapter = ScaleInAnimationAdapter(mAdapt)
        animAdapter.absListView = listView
        animAdapter.setInitialDelayMillis(300)
        listView!!.adapter = animAdapter
    }

    private fun initListener() {
        refresh!!.setMaterialRefreshListener(object : MaterialRefreshListener() {

            override fun onRefresh(materialRefreshLayout: MaterialRefreshLayout) {
                refreshType = REFRESH_TYPE.RERESH
                refresh!!.finishRefreshLoadMore()

                getData()
            }

            override fun onRefreshLoadMore(materialRefreshLayout: MaterialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout)
                refreshType = REFRESH_TYPE.LOAD
                refresh!!.finishRefresh()

                Handler().postDelayed({
                    if (null != refresh) {
                        refresh!!.finishRefreshLoadMore()
                    }
                }, 3000)
            }

            override fun onfinish() {
                super.onfinish()
            }

        })

        listView!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val txtNo = view.findViewById(R.id.txt_vacationno) as TextView
            val vacationNo = txtNo.text.toString()

            if (!vacationNo.isEmpty()) {
                val intent = Intent(activity, ApplyStateActivity::class.java)
                intent.putExtra("VACATIONNO", vacationNo)
                startActivity(intent)
            }
        }
    }

    private fun initGetData() {
        httpDialog = ProgressUtil().showHttpLoading(context)
        getData()
    }

    private fun getData() {
        applyListService!!.searchApplyList(mUserId!!, startDate!!, endDate!!, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.toolbar_menu_applylist, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId

        if (id == R.id.ab_apply) {
            val intent = Intent(activity, ApplyActivity::class.java)
            startActivityForResult(intent, 1)

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (null != intent) {
            val result = intent.extras!!.getString("result")//得到新Activity 关闭后返回的数据
            if (result == "refresh") {
                initGetData()
            }
        }
    }

    override fun onSearchSuccess(list: List<ApplyListInfo>) {
        if (list.isNotEmpty() && listView != null) {
            mList = list
            mAdapt!!.setList(mList!!)
            mAdapt!!.notifyDataSetChanged()
        }
        clearLoading()
    }

    override fun onSearchFailure(code: Int, msg: String) {
        if (listView != null) {
            ToastUtil.showToast(context, msg, Toast.LENGTH_LONG)
            clearLoading()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        applyListService!!.cancel()
//        ButterKnife.(this)
    }

    companion object {

        fun newInstance(args: Bundle): ApplyListFragment {
            val f = ApplyListFragment()
            f.arguments = args
            return f
        }
    }
}
