package com.eland.android.eoas.Activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast

import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.R
import com.eland.android.eoas.Service.ApplyService
import com.eland.android.eoas.Util.ConsoleUtil
import com.eland.android.eoas.Util.ProgressUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.ToastUtil
import com.eland.android.eoas.Views.stepView.StepsView

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by liu.wenbin on 15/12/29.
 */
class ApplyStateActivity : AppCompatActivity(), ApplyService.IOnApplyListener {

    @BindView(R.id.applytoolbar)
    lateinit var applytoolbar: Toolbar
    @BindView(R.id.step)
    lateinit var step: StepsView

    private val TAG = "EOAS"

    private var applyId: String? = null
    private var httpDialog: Dialog? = null
    private var approves: Array<String?> = arrayOf()// = {"审批人1", "审批人2", "审批人3"};
    private var approvePosition = 0
    private var mUserId: String? = null

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

        setContentView(R.layout.activity_apply_state)
        ButterKnife.bind(this)

        applyService = ApplyService(this)

        mUserId = SharedReferenceHelper.getInstance(this).getValue(Constant.LOGINID)
        initToolbar()
        initParames()

        initApproveState()
    }

    private fun initToolbar() {
        applytoolbar.setTitleTextColor(Color.parseColor("#ffffff")) //设置标题颜色
        applytoolbar.title = "批准流程"
        setSupportActionBar(applytoolbar)
        applytoolbar.setNavigationIcon(R.mipmap.icon_back)
        supportActionBar!!.setHomeButtonEnabled(true) //设置返回键可用
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        applytoolbar.setNavigationOnClickListener { finish() }
    }

    private fun initParames() {
        val intent = intent

        if (null != intent) {
            applyId = intent.getStringExtra("VACATIONNO")
            ConsoleUtil.i(TAG, applyId!!)
        }
    }

    private fun initApproveState() {
        httpDialog = ProgressUtil().showHttpLoading(this)

        applyService!!.searchApprogressList(mUserId!!, applyId!!, this)
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
                    .setColorIndicator(resources.getColor(R.color.md_red_500))
                    .setCompletedPosition(if (approvePosition == 0) 0 else approvePosition - 1)
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
        ToastUtil.showToast(this, msg, Toast.LENGTH_LONG)
        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }
    }
}
