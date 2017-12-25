package com.eland.android.eoas.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import com.eland.android.eoas.Adapt.ProxyAdapt
import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.Model.ProxyInfo
import com.eland.android.eoas.Model.SaveProxyInfo
import com.eland.android.eoas.R
import com.eland.android.eoas.Service.ProxyService
import com.eland.android.eoas.Util.ProgressUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.ToastUtil
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshLayout
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshListener
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter
import com.rey.material.widget.Spinner

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by liu.wenbin on 16/1/6.
 * 设置代理负责人
 */
class SetProxyUserActivity : AppCompatActivity(), ProxyService.IOnSearchProxyInfoListener {

    @BindView(R.id.setProxytoolbar)
     var setProxytoolbar: Toolbar? = null
    @BindView(R.id.listView)
     var listView: ListView? = null
    @BindView(R.id.refresh)
     var refresh: MaterialRefreshLayout? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var mUserId: String? = null
    private var httpDialog: Dialog? = null

    private var mList: List<ProxyInfo>? = null
    private var mAdapt: ProxyAdapt? = null
    private var proxyService: ProxyService? = null

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

        setContentView(R.layout.activity_setproxy)
        ButterKnife.bind(this)

        proxyService = ProxyService(this)
        mUserId = SharedReferenceHelper.getInstance(this).getValue(Constant.LOGINID)
        initToolBar()
        initParams()
        initProxyList()
    }

    private fun initToolBar() {
        setProxytoolbar!!.setTitleTextColor(Color.parseColor("#ffffff")) //设置标题颜色
        setProxytoolbar!!.title = "设置审批人"
        setSupportActionBar(setProxytoolbar)
        setProxytoolbar!!.setNavigationIcon(R.mipmap.icon_back)
        supportActionBar!!.setHomeButtonEnabled(true) //设置返回键可用
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setProxytoolbar!!.setNavigationOnClickListener { finish() }

        refresh!!.setMaterialRefreshListener(object : MaterialRefreshListener() {
            override fun onRefresh(materialRefreshLayout: MaterialRefreshLayout) {
                refresh!!.finishRefresh()
                refresh!!.finishRefreshLoadMore()
            }

            override fun onRefreshLoadMore(materialRefreshLayout: MaterialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout)
                refresh!!.finishRefresh()
                refresh!!.finishRefreshLoadMore()
            }
        })
    }

    private fun initParams() {
        val intent = intent

        if (null != intent) {
            startDate = intent.getStringExtra("StartDate")
            endDate = intent.getStringExtra("EndDate")
        }
    }

    private fun initProxyList() {
        httpDialog = ProgressUtil().showHttpLoading(this)
        proxyService!!.searchProxyInfo(mUserId!!, startDate!!, endDate!!, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_proxy, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.ab_saveproxy) {
            saveProxy()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveProxy() {
        if (null != httpDialog && !httpDialog!!.isShowing) {
            httpDialog!!.show()
        } else {
            httpDialog = ProgressUtil().showHttpLoading(this)
        }

        val list = ArrayList<SaveProxyInfo>()
        var saveProxyInfo: SaveProxyInfo

        for (i in 0 until listView!!.count) {
            saveProxyInfo = SaveProxyInfo()
            val view = listView!!.getChildAt(i)
            val orgView = view.findViewById(R.id.txt_orgCode) as TextView
            val empSpinner = view.findViewById(R.id.spinner_proxy) as Spinner

            saveProxyInfo.orgId = orgView.text.toString()
            saveProxyInfo.empId = findEmpId(empSpinner.selectedItem.toString(), i)
            saveProxyInfo.startDate = startDate
            saveProxyInfo.endDate = endDate
            saveProxyInfo.userId = mUserId

            list.add(saveProxyInfo)
        }

        proxyService!!.saveProxy(list, this)
    }

    private fun findEmpId(userName: String, position: Int): String {
        var empId = ""

        if (null != mList && mList!!.size > 0) {
            if (mList!![position] != null && mList!![position].proxyInfoList != null) {
                for (j in mList!![position].proxyInfoList!!.indices) {
                    if (mList!![position].proxyInfoList!![j].UserName == userName) {
                        empId = mList!![position].proxyInfoList!![j].EmpId!!
                        break
                    }
                }
            }
        }
        return empId
    }

    override fun onSuccess(list: List<ProxyInfo>) {
        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }

        if (list != null && list.size > 0) {
            mList = list
        } else {
            mList = ArrayList()
        }

        mAdapt = ProxyAdapt(this, mList!!, startDate!!, endDate!!)
        val animAdapter = ScaleInAnimationAdapter(mAdapt)
        animAdapter.absListView = listView
        animAdapter.setInitialDelayMillis(300)
        listView!!.adapter = animAdapter
    }

    override fun onFailure(code: Int, msg: String) {
        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }
        if (code == 1000) {
            //go back to apply activity
            val intent = Intent()
            intent.putExtra("result", "OK")
            this@SetProxyUserActivity.setResult(Activity.RESULT_OK, intent)
            this@SetProxyUserActivity.finish()
        } else {
            ToastUtil.showToast(this, msg, Toast.LENGTH_SHORT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        proxyService!!.cancel()
    }
}
