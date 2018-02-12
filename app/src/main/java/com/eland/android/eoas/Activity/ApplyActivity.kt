package com.eland.android.eoas.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.Model.NameValueInfo
import com.eland.android.eoas.R
import com.eland.android.eoas.Service.ApplyService
import com.eland.android.eoas.Util.ProgressUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.ToastUtil
import com.eland.android.eoas.Views.SegmentView
import com.rey.material.app.DatePickerDialog
import com.rey.material.app.Dialog
import com.rey.material.app.DialogFragment
import com.rey.material.widget.Button
import com.rey.material.widget.Spinner

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

import butterknife.ButterKnife
import butterknife.OnClick

import android.app.Activity
import butterknife.BindView


/**
 * Created by liu.wenbin on 15/12/29.
 */
class ApplyActivity : AppCompatActivity(), ApplyService.IOnApplyListener {

    @BindView(R.id.applytoolbar)
    lateinit var applytoolbar: Toolbar
    @BindView(R.id.spinner_vacationtype)
    lateinit var spinnerVacationtype: Spinner
    @BindView(R.id.txt_startdate)
    lateinit var txtStartdate: TextView
    @BindView(R.id.seg_starttime)
    lateinit var segStarttime: SegmentView
    @BindView(R.id.txt_enddate)
    lateinit var txtEnddate: TextView
    @BindView(R.id.seg_endtime)
    lateinit var segEndtime: SegmentView
    @BindView(R.id.btn_apply)
    lateinit var btnApply: Button
    @BindView(R.id.txt_diffDays)
    lateinit var txtDiffDays: TextView
    @BindView(R.id.edit_remark)
    lateinit var editRemark: EditText
    @BindView(R.id.txt_all_vacation)
    lateinit var txtAllVacation: TextView
    @BindView(R.id.txt_year_vacation)
    lateinit var txtYearVacation: TextView
    @BindView(R.id.txt_company_vacation)
    lateinit var txtCompanyVacation: TextView
    @BindView(R.id.txt_adjust_vacation)
    lateinit var txtAdjustVacation: TextView

    private var mUserId: String? = null
    private var startTime: String? = null
    private var endTime: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var diffDays: String? = null
    private var vacationTypeCode = "01"
    private var yearVacation: String? = null
    private var adjustVacation: String? = null
    private var companyVacation = "0.0"
    private var mList: MutableList<NameValueInfo>? = null
    private var applyService: ApplyService? = null

    private var httpDialog: android.app.Dialog? = null

    private var maxTime: Long = 0
    private var minTime: Long = 0

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

        setContentView(R.layout.activity_apply)
        ButterKnife.bind(this)

        applyService = ApplyService(this)
        //初始化页面loading
        httpDialog = ProgressUtil().showHttpLoading(this)

        initView()
        initToolbar()
        initSegment()
        initVacationDays()
        //initSpinner();
    }

    private fun initView() {
        mUserId = SharedReferenceHelper.getInstance(this).getValue(Constant.LOGINID)

        segStarttime.setSegmentText("08:00", 0)
        segStarttime.setSegmentText("13:00", 1)

        segEndtime.setSegmentText("13:00", 0)
        segEndtime.setSegmentText("17:00", 1)

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(System.currentTimeMillis())
        val nowDate = simpleDateFormat.format(date)

        setTime("08", "13")
        setDate(nowDate, nowDate)
        setDiffDays("0.5")

        val thisYearStart = nowDate.substring(0, 4) + "-01-01"
        val thisYearEnd = nowDate.substring(0, 4) + "-12-31"
        try {
            maxTime = simpleDateFormat.parse(thisYearEnd).time
            minTime = simpleDateFormat.parse(thisYearStart).time
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun initToolbar() {
        applytoolbar.setTitleTextColor(Color.parseColor("#ffffff")) //设置标题颜色
        applytoolbar.title = "新申请"
        setSupportActionBar(applytoolbar)
        applytoolbar.setNavigationIcon(R.mipmap.icon_back)
        supportActionBar!!.setHomeButtonEnabled(true) //设置返回键可用

        applytoolbar.setNavigationOnClickListener { finish() }
    }

    private fun initSegment() {
        segStarttime.setOnSegmentViewClickListener { v, position ->
            if (position == 0) {
                //8点
                //startTime = "08";
                setTime("08", endTime)
            } else {
                //13点
                setTime("13", endTime)
            }
            getDiffDate()
        }

        segEndtime.setOnSegmentViewClickListener { v, position ->
            if (position == 0) {
                //13点
                setTime(startTime, "13")
            } else {
                //15点
                setTime(startTime, "17")
            }
            getDiffDate()
        }

    }

    private fun initVacationDays() {
        applyService!!.searchVacationDays(mUserId!!, this)
    }

    private fun initSpinner() {
        applyService!!.searchVacationType(mUserId!!, this)
    }

    private fun getDiffDate() {
        if (httpDialog != null && !httpDialog!!.isShowing) {
            httpDialog!!.show()
        }
        setVacationType()
        applyService!!.searchDiffDays(mUserId!!, startDate!!, endDate!!, startTime!!, endTime!!, vacationTypeCode, this)
    }

    private fun setVacationType() {
        val select = spinnerVacationtype.selectedItem.toString()
        //find vacation code
        if (null != mList && mList!!.size > 0) {
            for (result in mList!!) {
                if (result.name == select) {
                    vacationTypeCode = result.code!!
                }
            }
        }
    }

    private fun setDate(start: String?, end: String?) {
        startDate = start
        endDate = end

        //        Calendar ca = Calendar.getInstance();
        //        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //        String thisYear = ca.YEAR + "-12-31";
        //
        //        try {
        //            if (dateFormat.parse(start).getTime() > dateFormat.parse(thisYear).getTime()) {
        //                txtStartdate.setText(thisYear);
        //            }
        //            else {
        //                txtStartdate.setText(start);
        //            }
        //
        //            if (dateFormat.parse(end).getTime() > dateFormat.parse(thisYear).getTime()) {
        //                txtEnddate.setText(thisYear);
        //            }
        //            else {
        //                txtEnddate.setText(end);
        //            }
        //        } catch (ParseException e) {
        //            e.printStackTrace();
        //        }

        txtStartdate.text = startDate
        txtEnddate.text = endDate
    }

    private fun setTime(start: String?, end: String?) {
        startTime = start
        endTime = end
    }

    private fun setDiffDays(diff: String) {
        diffDays = diff
        txtDiffDays.text = "${diffDays}天"
    }

    @OnClick(R.id.txt_startdate)
    internal fun setStartDate() {


        val builder = object : DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker) {
            //            @Override
            //            public DatePickerDialog.Builder dateRange(long min, long max) {
            //                min = minTime;
            //                max = maxTime;
            //                return super.dateRange(min, max);
            //            }

            override fun onPositiveActionClicked(fragment: DialogFragment) {
                val dialog = fragment.dialog as DatePickerDialog

                val format = SimpleDateFormat("yyyy-MM-dd")
                setDate(dialog.getFormattedDate(format), endDate)
                getDiffDate()
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

        fragment.show(supportFragmentManager, null)
    }

    @OnClick(R.id.txt_enddate)
    internal fun setEndDate() {
        val builder = object : DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker) {
            override fun dateRange(min: Long, max: Long): DatePickerDialog.Builder {
                var min = min
                var max = max
                min = minTime
                max = maxTime
                return super.dateRange(min, max)
            }

            override fun onPositiveActionClicked(fragment: DialogFragment) {
                val dialog = fragment.dialog as DatePickerDialog

                val format = SimpleDateFormat("yyyy-MM-dd")
                setDate(startDate, dialog.getFormattedDate(format))
                getDiffDate()
                super.onPositiveActionClicked(fragment)
            }

            override fun onNegativeActionClicked(fragment: DialogFragment) {
                //Toast.makeText(fragment.getDialog().getContext(), "Cancelled" , Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment)
            }
        }.dateRange(minTime, maxTime)

        builder.positiveAction("确定")
                .negativeAction("取消")

        val fragment = DialogFragment.newInstance(builder)
        fragment.show(supportFragmentManager, null)
    }

    @OnClick(R.id.btn_apply) internal fun apply() {
//        val isGo = validateInput()
        if (validateInput()) {
            startApply()
        }
    }

    private fun validateInput(): Boolean {
        setVacationType()
        val all = 0.0

        //06 - adjust 07 - year 11 - company
        if (vacationTypeCode == "06") {
            val adjust = java.lang.Double.valueOf(txtAdjustVacation.text.toString())!!
            if (all >= adjust || java.lang.Double.valueOf(diffDays) > adjust) {
                ToastUtil.showToast(this, "调休假不足，请选择其他休假类型", Toast.LENGTH_LONG)
                return false
            }
        } else if (vacationTypeCode == "07") {
            val years = java.lang.Double.valueOf(txtYearVacation.text.toString())!!
            if (all >= years || java.lang.Double.valueOf(diffDays) > years) {
                ToastUtil.showToast(this, "年休假不足，请选择其他休假类型", Toast.LENGTH_LONG)
                return false
            }
        } else if (vacationTypeCode == "11") {
            val company = java.lang.Double.valueOf(txtCompanyVacation.text.toString())!!
            if (all >= company || java.lang.Double.valueOf(diffDays) > company) {
                ToastUtil.showToast(this, "企业年休假不足，请选择其他休假类型", Toast.LENGTH_LONG)
                return false
            }
        }

        if (editRemark.text.toString().isEmpty()) {
            ToastUtil.showToast(this, "请填写休假理由", Toast.LENGTH_LONG)
            return false
        }

        if (diffDays == "0.0") {
            ToastUtil.showToast(this, "选择正确的休假日期", Toast.LENGTH_LONG)
            return false
        }

        return true
    }

    private fun startApply() {

        if (null == httpDialog) {
            httpDialog = ProgressUtil().showHttpLoading(this)
        } else {
            httpDialog!!.show()
        }

        applyService!!.saveApply(mUserId!!, startDate!!, endDate!!, startTime!!, endTime!!, vacationTypeCode, diffDays!!,
                editRemark.text.toString(), this)
    }

    override fun onSuccess(obj: JSONObject?) {
        if (null != obj) {
            try {
                yearVacation = obj.getString("YearVacation")
                adjustVacation = obj.getString("AdjustVacation")
                companyVacation = obj.getString("CompanyYearVacation")

                val all = java.lang.Float.valueOf(yearVacation)!! +
                        java.lang.Float.valueOf(adjustVacation)!! +
                        java.lang.Float.valueOf(companyVacation)!!

                txtYearVacation.text = yearVacation
                txtCompanyVacation.text = companyVacation
                txtAdjustVacation.text = adjustVacation
                txtAllVacation.text = all.toString()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        initSpinner()
        //        if (httpDialog.isShowing()) {
        //            httpDialog.dismiss();
        //        }
    }

    override fun onSuccess(array: JSONArray?) {
        mList = ArrayList()
        var dto: NameValueInfo? = null

        val vacationTypes = arrayOfNulls<String>(array!!.length())
        try {
            for (i in 0 until array.length()) {
                var obj: JSONObject? = null
                obj = array.getJSONObject(i)
                dto = NameValueInfo()
                dto.name = obj!!.getString("name")
                dto.code = obj.getString("code")
                mList!!.add(dto)
                vacationTypes[i] = obj.getString("name")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }


        val adapter = ArrayAdapter<String>(this@ApplyActivity, R.layout.vacation_spinner_layout, vacationTypes)
        adapter.setDropDownViewResource(R.layout.vacation_spinner_dropdown)
        spinnerVacationtype.adapter = adapter

        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }
    }

    override fun onSuccess(diffDays: String?) {
        if (!diffDays!!.isEmpty()) {
            setDiffDays(diffDays.replace("\"".toRegex(), ""))
        }
        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }
    }

    override fun onSuccess(type: Int, msg: String?) {
        val message = msg?.replace("\"".toRegex(), "")
        var alertMessage = ""
        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }

        if (message!!.isEmpty()) {
            alertMessage = "申请失败"
            ToastUtil.showToast(this, alertMessage, Toast.LENGTH_LONG)
        } else if (message == "HUM008") {
            alertMessage = "此期间内已申请过相同的假期"
            ToastUtil.showToast(this, alertMessage, Toast.LENGTH_LONG)
        } else if (message == "HUM009") {
            //alertMessage = "请先设置代理负责人";
            val intent = Intent(this@ApplyActivity, SetProxyUserActivity::class.java)
            intent.putExtra("StartDate", startDate)
            intent.putExtra("EndDate", endDate)

            startActivityForResult(intent, 1)
        } else {
            alertMessage = "申请成功"
            ToastUtil.showToast(this, alertMessage, Toast.LENGTH_LONG)
            val intent = Intent()
            //把返回数据存入Intent
            intent.putExtra("result", "refresh")
            //设置返回数据
            this@ApplyActivity.setResult(Activity.RESULT_OK, intent)
            this@ApplyActivity.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (null != intent) {
            val result = intent.extras!!.getString("result")//得到新Activity 关闭后返回的数据
            if (result == "OK") {
                startApply()
            }
        }

    }

    override fun onFailure(type: Int, code: Int, msg: String) {
        ToastUtil.showToast(this, msg, Toast.LENGTH_LONG)
        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        applyService!!.cancel()
    }

}
