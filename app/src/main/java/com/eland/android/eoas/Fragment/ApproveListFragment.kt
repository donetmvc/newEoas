package com.eland.android.eoas.Fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import com.eland.android.eoas.Activity.ApproveActivity
import com.eland.android.eoas.Adapt.ApproveListAdapt
import com.eland.android.eoas.Model.ApproveListInfo
import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.R
import com.eland.android.eoas.Receiver.DeskCountChangeReceiver
import com.eland.android.eoas.Service.ApproveListService
import com.eland.android.eoas.Util.ProgressUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.ToastUtil
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshLayout
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshListener
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by liu.wenbin on 16/1/7.
 */
@SuppressLint("ValidFragment")
class ApproveListFragment : Fragment, ApproveListService.IOnApproveListListener {

    @BindView(R.id.listView)
    var listView: ListView? = null
    @BindView(R.id.refresh)
    var refresh: MaterialRefreshLayout? = null
    private var rootView: View? = null
    private var mUserId: String? = null

    private var mAdapter: ApproveListAdapt? = null
    private var mList: List<ApproveListInfo>? = null
    private var httpDialog: Dialog? = null
    private var approveListService: ApproveListService? = null

    constructor() : super() {}

    constructor(context: Context) {
        //this.context = context;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.contact_fragment, null)
        ButterKnife.bind(this, rootView!!)

        approveListService = ApproveListService(context)
        initView()
        initAdapt()
        initListener()

        return rootView
    }

    private fun initView() {
        mUserId = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGINID)
    }

    private fun initAdapt() {
        initData()
    }

    private fun initListener() {
        refresh!!.setMaterialRefreshListener(object : MaterialRefreshListener() {

            override fun onRefresh(materialRefreshLayout: MaterialRefreshLayout) {
                refresh!!.finishRefreshLoadMore()
                getData()
            }

            override fun onRefreshLoadMore(materialRefreshLayout: MaterialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout)
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
            val txtNo = view.findViewById(R.id.txt_applyId) as TextView
            val vacationNo = txtNo.text.toString()

            if (!vacationNo.isEmpty()) {
                val intent = Intent(activity, ApproveActivity::class.java)
                intent.putExtra("VACATIONNO", vacationNo)
                startActivityForResult(intent, 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (null != intent) {
            val result = intent.getStringExtra("result")
            if (result == "OK") {
                if (null != httpDialog && !httpDialog!!.isShowing) {
                    httpDialog!!.show()
                }
                getData()
            }
        }
    }

    private fun initData() {
        httpDialog = ProgressUtil().showHttpLoading(getContext())
        getData()
    }

    private fun getData() {
        approveListService!!.searchApproveList(mUserId!!, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        approveListService!!.cancel()
//        ButterKnife.unbind(this)
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

    override fun onSuccess(list: List<ApproveListInfo>) {
        clearLoading()

        when(list!!.isNotEmpty()) {
            true -> mList = list
            false -> mList = arrayListOf()
        }

        mAdapter = ApproveListAdapt(context!!, mList!!)
        val animAdapter = ScaleInAnimationAdapter(mAdapter)
        animAdapter.absListView = listView!!
        animAdapter.setInitialDelayMillis(300)
        listView!!.adapter = animAdapter


        val intent = Intent(activity, DeskCountChangeReceiver::class.java)
        intent.action = "EOAS_COUNT_CHANGED"
        intent.putExtra("COUNT", (if (list!!.isEmpty()) 0 else list.size).toString())
        context!!.sendBroadcast(intent)
    }

    override fun onFailure(code: Int, msg: String) {
        clearLoading()
        if (null != listView) {
            ToastUtil.showToast(context, msg, Toast.LENGTH_LONG)
        }
    }

    companion object {

        fun newInstance(args: Bundle): ApproveListFragment {
            val f = ApproveListFragment()
            f.arguments = args
            return f
        }
    }
}
