package com.eland.android.eoas.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.R
import com.eland.android.eoas.Service.ApplyService
import com.eland.android.eoas.Service.ApproveService
import com.eland.android.eoas.Util.ProgressUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.ToastUtil
import com.eland.android.eoas.Views.stepView.StepsView
import com.rey.material.widget.Button

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import cn.jpush.android.api.JPushInterface

/**
 * Created by liu.wenbin on 16/1/7.
 * 批准页面
 */
class ApproveActivity : AppCompatActivity(), ApplyService.IOnApplyListener, ApproveService.IOnApproveListener {

    @BindView(R.id.approvetoolbar)
    lateinit var approvetoolbar: Toolbar
    @BindView(R.id.txt_applyId)
    lateinit var txtApplyId: TextView
    @BindView(R.id.txt_content)
    lateinit var txtContent: TextView
    @BindView(R.id.txt_remark)
    lateinit var txtRemark: TextView
    @BindView(R.id.step)
    lateinit var step: StepsView
    @BindView(R.id.btn_approve)
    lateinit var btnApprove: Button
    @BindView(R.id.btn_refuse)
    lateinit var btnRefuse: Button
    @BindView(R.id.edit_remark)
    lateinit var editRemark: EditText

    private var mUserId: String? = null
    private var applyId: String? = null
    private var httpDialog: Dialog? = null
    private var approves: Array<String?> = arrayOf()
    private var approvePosition = 0
    private var approveType = 0
    private var approveService: ApproveService? = null
    private var applyService: ApplyService? = null

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
        setContentView(R.layout.activity_approve)
        ButterKnife.bind(this)
        approveService = ApproveService(this)
        applyService = ApplyService(this)

        initParams()
        initToolbar()
        initView()
    }

    private fun initParams() {
        mUserId = SharedReferenceHelper.getInstance(this).getValue(Constant.LOGINID)
        val intent = intent

        if (null != intent) {
            val param = intent.getStringExtra("VACATIONNO")
            if (null != param && !param.isEmpty()) {
                applyId = param
                approveType = 0
            } else {
                val bundle = intent.extras
                if (null != bundle) {
                    val applyJson = bundle.getString(JPushInterface.EXTRA_EXTRA)
                    val obj: JSONObject
                    try {
                        obj = JSONObject(applyJson)
                        applyId = obj.getString("ApplyID")
                        approveType = 1
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    private fun initToolbar() {
        approvetoolbar.setTitleTextColor(Color.parseColor("#ffffff")) //设置标题颜色
        approvetoolbar.title = "休假批准"
        setSupportActionBar(approvetoolbar)
        approvetoolbar.setNavigationIcon(R.mipmap.icon_back)
        supportActionBar!!.setHomeButtonEnabled(true) //设置返回键可用

        approvetoolbar.setNavigationOnClickListener { finish() }
    }

    private fun initView() {

        if (applyId == null || applyId!!.isEmpty()) {
            ToastUtil.showToast(this, "没有可批准的信息", Toast.LENGTH_SHORT)
            return
        }
        httpDialog = ProgressUtil().showHttpLoading(this)
        startSearchApproveInfo()
    }

    private fun startSearchApproveInfo() {
        approveService!!.searchApplyInfo(mUserId!!, applyId!!, this)
    }

    @OnClick(R.id.btn_approve)
    internal fun approve() {
        if (null == httpDialog) {
            httpDialog = ProgressUtil().showHttpLoading(this)
        }
        httpDialog!!.show()
        approveService!!.saveApprove(mUserId!!, "01", editRemark.text.toString(), applyId!!, this)
    }

    @OnClick(R.id.btn_refuse)
    internal fun refuse() {

        if (editRemark.text.toString().isEmpty()) {
            ToastUtil.showToast(this, "拒绝必须填写理由", Toast.LENGTH_LONG)
            return
        }

        if (null == httpDialog) {
            httpDialog = ProgressUtil().showHttpLoading(this)
        }
        httpDialog!!.show()
        approveService!!.saveApprove(mUserId!!, "02", editRemark.text.toString(), applyId!!, this)
    }

    override fun onSuccess(obj: JSONObject?) {

    }

    override fun onSuccess(array: JSONArray?) {
        if (null != array && array.length() > 0) {
            approves = arrayOfNulls(array.length())
            try {

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    approves[i] = obj.getString("ApproveUserName")
                    val approveState = obj.getString("ApproveStateName")
                    if (approveState == "批准") {
                        approvePosition++
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            step.setLabels(approves)
                    .setBarColor(Color.GRAY)
                    .setLabelColor(Color.GRAY)
                    .setColorIndicator(resources.getColor(R.color.md_red_500)).completedPosition = if (approvePosition == 0) 0 else approvePosition - 1
        }

        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }
    }

    override fun onSuccess(diffDays: String?) {

    }

    override fun onSuccess(type: Int, msg: String?) {

    }

    override fun onFailure(type: Int, code: Int, msg: String) {
        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }
    }

    override fun onApproveSucess(content: String, reason: String) {

        if (content == "OK") {
            if (httpDialog!!.isShowing) {
                httpDialog!!.dismiss()
            }
            //当前审批人已经审批，返回list页面
            if (approveType == 0) {
                val intent = Intent()
                intent.putExtra("result", "OK")
                this@ApproveActivity.setResult(Activity.RESULT_OK, intent)
                this@ApproveActivity.finish()
            } else {
                val intent = Intent(this@ApproveActivity, MainActivity::class.java)
                startActivity(intent)
                this@ApproveActivity.finish()
            }
        } else {
            txtApplyId.text = "【$applyId】"
            txtContent.text = content
            txtRemark.text = reason

            applyService!!.searchApprogressList(mUserId!!, applyId!!, this)
        }
    }

    override fun onApproveFailure(code: Int, msg: String) {
        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }

        ToastUtil.showToast(this, msg, Toast.LENGTH_SHORT)
    }

    override fun onDestroy() {
        super.onDestroy()
        approveService!!.cancel()
        applyService!!.cancel()
    }
}
