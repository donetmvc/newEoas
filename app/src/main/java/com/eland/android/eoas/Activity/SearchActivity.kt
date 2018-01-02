package com.eland.android.eoas.Activity

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import com.eland.android.eoas.Adapt.ContactAdapt
import com.eland.android.eoas.Application.EOASApplication
import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.Model.LoginInfo
import com.eland.android.eoas.R
import com.eland.android.eoas.Service.ContactService
import com.eland.android.eoas.Util.CacheInfoUtil
import com.eland.android.eoas.Util.ProgressUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.ToastUtil
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshLayout
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshListener
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.rey.material.widget.EditText

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by liu.wenbin on 15/12/16.
 */
class SearchActivity : AppCompatActivity(), ContactService.IOnSearchContactListener {

    @BindView(R.id.searchtoolbar)
    lateinit var searchtoolbar: Toolbar
    @BindView(R.id.edit_searchKey)
    lateinit var editText: EditText
    @BindView(R.id.txt_tips)
    lateinit var txtTips: TextView
    @BindView(R.id.listView)
    lateinit var listView: ListView
    @BindView(R.id.refresh)
    lateinit var refresh: MaterialRefreshLayout

    private var imageLoader: ImageLoader? = null

    internal var mAdapt: ContactAdapt? = null
    internal var contactService: ContactService? = null
    internal var mList: List<LoginInfo>? = null

    private var searchKey: String? = null
    private var httpDialog: Dialog? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = SharedReferenceHelper.getInstance(this).getValue(Constant.EOAS_THEME)
        if (!theme.isEmpty()) {
            if (theme == "RED") {
                setTheme(R.style.MainThenmeRed)
            } else {
                setTheme(R.style.MainThenmeBlue)
            }
        } else {
            setTheme(R.style.MainThenmeRed)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        EOASApplication.instance!!.addActivity(this)
        ButterKnife.bind(this)

        contactService = ContactService(this)
        imageLoader = ImageLoader.getInstance()
        imageLoader!!.init(ImageLoaderConfiguration.createDefault(this))

        initToolbar()
        editText = searchtoolbar.findViewById(R.id.edit_searchKey) as EditText
        userId = SharedReferenceHelper.getInstance(this).getValue(Constant.LOGINID)

        initRefreshListener()
    }

    private fun initRefreshListener() {
        refresh.setMaterialRefreshListener(object : MaterialRefreshListener() {
            override fun onRefresh(materialRefreshLayout: MaterialRefreshLayout) {
                refresh.finishRefresh()
            }
        })
    }

    private fun initToolbar() {
        searchtoolbar.setTitleTextColor(Color.parseColor("#ffffff")) //设置标题颜色
        searchtoolbar.title = ""
        setSupportActionBar(searchtoolbar)
        searchtoolbar.setNavigationIcon(R.mipmap.icon_back)
        supportActionBar!!.setHomeButtonEnabled(true) //设置返回键可用
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchtoolbar.setNavigationOnClickListener { finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.ab_search) {
            startSearch()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startSearch() {
        searchKey = editText?.text.toString()

        if (searchKey!!.isEmpty()) {
            ToastUtil.showToast(this, "请输入您想查询的人员信息", Toast.LENGTH_SHORT)
            txtTips.visibility = TextView.VISIBLE
            return
        }

        //hide input keyboard
        val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (im.isActive) {
            im.hideSoftInputFromWindow(currentFocus!!.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        httpDialog = ProgressUtil().showHttpLoading(this)

        txtTips.visibility = TextView.GONE

        mList = CacheInfoUtil.loadContact(this)

        if (null != mList && mList!!.size > 0) {
            startFindLikeData(mList!!)
        } else {
            getData()
        }
    }

    private fun getData() {
        contactService?.searchContact(userId!!, this)
    }

    private fun clearLoading() {
        refresh.finishRefresh()
        refresh.finishRefreshLoadMore()
        if (httpDialog != null && httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }
    }

    override fun onSearchSuccess(list: ArrayList<LoginInfo>) {
        if (list.size > 0) {
            startFindLikeData(list)
        } else {
            clearLoading()
            ToastUtil.showToast(this, "没有搜索到匹配的人员信息", Toast.LENGTH_SHORT)
        }
    }

    override fun onSearchFailure(code: Int, msg: String) {
        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }
        ToastUtil.showToast(this, "没有搜索到匹配的人员信息", Toast.LENGTH_SHORT)
    }

    private fun startFindLikeData(list: List<LoginInfo>) {
        val returnList = ArrayList<LoginInfo>()

        for (i in list.indices) {
            if (list[i].userName!!.contains(searchKey!!)
                    || list[i].userId!!.contains(searchKey!!)
                    || list[i].cellNo!!.contains(searchKey!!)) {
                returnList.add(list[i])
                continue
            }
        }

        initAdapt(returnList)
    }

    private fun initAdapt(list: List<LoginInfo>?) {
        if (null != list && list.isNotEmpty()) {
            mAdapt = ContactAdapt(this, list, imageLoader!!)
            val animAdapter = ScaleInAnimationAdapter(mAdapt)
            animAdapter.absListView = listView
            animAdapter.setInitialDelayMillis(300)
            listView.adapter = animAdapter
        } else {
            ToastUtil.showToast(this, "没有搜索到匹配的人员信息", Toast.LENGTH_SHORT)
        }
        clearLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        contactService?.cancel()
    }
}
