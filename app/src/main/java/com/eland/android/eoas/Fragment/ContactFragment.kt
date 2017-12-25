package com.eland.android.eoas.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast

import com.eland.android.eoas.Activity.SearchActivity
import com.eland.android.eoas.Adapt.ContactAdapt
import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.Model.LoginInfo
import com.eland.android.eoas.R
import com.eland.android.eoas.Service.ContactService
import com.eland.android.eoas.Util.CacheInfoUtil
import com.eland.android.eoas.Util.ConsoleUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.ToastUtil
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshLayout
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshListener
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by liu.wenbin on 15/12/14.
 */
class ContactFragment : Fragment, ContactService.IOnSearchContactListener {

    @BindView(R.id.listView)
    var listView: ListView? = null
    @BindView(R.id.refresh)
    var refresh: MaterialRefreshLayout? = null
    private var rootView: View? = null
    internal var mLsit: MutableList<LoginInfo>? = null
    private var mAdapt: ContactAdapt? = null
    private var imageLoader: ImageLoader? = null
    internal var userId: String? = null
    internal var toolbar: Toolbar? = null
    private val TAG = "EOAS"

    private var contactService: ContactService? = null
    private var refreshType = REFRESH_TYPE.RERESH

    enum class REFRESH_TYPE {
        RERESH, LOAD
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

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        ConsoleUtil.i(TAG, "=====I am back=========")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        ConsoleUtil.i(TAG, "=====I am saved=========")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.contact_fragment, null)
        ButterKnife.bind(this, rootView!!)

        imageLoader = ImageLoader.getInstance()
        imageLoader!!.init(ImageLoaderConfiguration.createDefault(activity))
        contactService = ContactService(context!!)
        userId = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGINID)
        toolbar = activity.findViewById(R.id.toolbar) as Toolbar

        initListener()

        initAdapter()

        return rootView
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
    }

    private fun initAdapter() {
        mLsit = ArrayList()

        if (CacheInfoUtil.loadContact(context).size > 0) {
            mLsit = CacheInfoUtil.loadContact(context)
        } else {
            getData()
        }

        mAdapt = ContactAdapt(context!!, mLsit!!, imageLoader!!)
        val animAdapter = ScaleInAnimationAdapter(mAdapt)
        animAdapter.absListView = listView
        animAdapter.setInitialDelayMillis(300)
        listView!!.adapter = animAdapter
    }

    private fun getData() {
        contactService!!.searchContact(userId!!, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        contactService!!.cancel()
//        ButterKnife.unbind(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId

        if (id == R.id.ab_search) {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clearLoading() {
        if (null != refresh) {
            refresh!!.finishRefresh()
            refresh!!.finishRefreshLoadMore()
        }
    }

    override fun onSearchSuccess(list: ArrayList<LoginInfo>) {
        clearLoading()
        if (null != listView) {
            //把数据加入缓存
            CacheInfoUtil.saveContact(context, list)

            mLsit!!.clear()
            mLsit!!.addAll(list)
            mAdapt!!.setList(mLsit!!)
        }
    }

    override fun onSearchFailure(code: Int, msg: String) {
        clearLoading()
        ToastUtil.showToast(context, msg, Toast.LENGTH_LONG)
    }

    companion object {

        fun newInstance(args: Bundle): ContactFragment {
            val f = ContactFragment()
            f.arguments = args
            return f
        }
    }
}
