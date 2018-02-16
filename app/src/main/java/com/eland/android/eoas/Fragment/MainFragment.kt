package com.eland.android.eoas.Fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.eland.android.eoas.Model.ApproveListInfo
import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.R
import com.eland.android.eoas.Receiver.DeskCountChangeReceiver
import com.eland.android.eoas.Service.ApproveListService
import com.eland.android.eoas.Util.ConsoleUtil
import com.eland.android.eoas.Util.ImageLoaderOption
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Views.ScrollTextView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration

import java.util.Calendar
import java.util.TimeZone

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.eland.android.eoas.Activity.BaseActivity
import com.eland.android.eoas.Adapt.WetherAdapter
import com.eland.android.eoas.Http.dataService.IOnGetWetherListener
import com.eland.android.eoas.Http.dataService.WeatherDataService
import com.eland.android.eoas.Model.WetherData
import com.eland.android.eoas.Util.ProgressUtil
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.PermissionUtils
import permissions.dispatcher.RuntimePermissions

/**
 * Created by liu.wenbin on 15/11/27.
 */
@RuntimePermissions
class MainFragment : Fragment, ApproveListService.IOnApproveListListener, AMapLocationListener, IOnGetWetherListener {

    @BindView(R.id.txt_main_scroll)
    lateinit var txtMainScroll: ScrollTextView
    @BindView(R.id.recycle_weather)
    lateinit var recycle: RecyclerView

    private var rootView: View? = null
    private var registScheduleFragment: RegistScheduleFragment? = null
    private var contactFragment: ContactFragment? = null
    private val TAG = "EOAS"
    private var mUserId: String? = null
    private var approveListService: ApproveListService? = null

    private var options: DisplayImageOptions? = null
    private var imageLoader: ImageLoader? = null

    var mLocationClient: AMapLocationClient? = null
    var mLocationOption: AMapLocationClientOption? = null

    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()

    private var count = 0
    private lateinit var loading: Dialog

    constructor() : super() {}

    @SuppressLint("ValidFragment")
    constructor(context: Context) {
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.fragment_main, null)
        ButterKnife.bind(this, rootView!!)

        approveListService = ApproveListService(context)
        options = ImageLoaderOption.getOptionsById(R.mipmap.default_wether)
        imageLoader = ImageLoader.getInstance()
        imageLoader!!.init(ImageLoaderConfiguration.createDefault(activity))
        mUserId = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGINID)
        setApproveCount()
        setTitler()
        loading = ProgressUtil.loading(context)
        return rootView
    }

    private fun setApproveCount() {
        approveListService!!.searchApproveList(mUserId!!, this)
    }

    @Suppress("DEPRECATION")
    private fun setTitler() {
        txtMainScroll.textContent = "用户ID: " + mUserId + "   欢迎使用移动OA办公系统!" + "  今天是   " + formatWeekDay()
        txtMainScroll.textSize = 22

        txtMainScroll.textColor = if (SDK_INT >= 23) {
             context!!.resources.getColor(R.color.text_color, null)
        }
        else context!!.resources.getColor(R.color.text_color)

        txtMainScroll.backgroundColor = Color.WHITE
    }

    private fun setWeather() {
        if(!PermissionUtils.hasSelfPermissions(context,
                BaseActivity.READ_EXTERNAL_STORAGE_PERMISSION,
                BaseActivity.WRITE_EXTERNAL_STORAGE_PERMISSION,
                BaseActivity.READ_PHONE_STATE_PERMISSION,
                BaseActivity.CAMERA_PERMISSION,
                BaseActivity.ACCESS_FINE_LOCATION_PEMISSION,
                BaseActivity.ACCESS_CROSE_LOCATION_PERMISSION)) {
            showLocationWithPermissionCheck()
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated method
        onRequestPermissionsResult(requestCode, grantResults)
        showLoading()
        if(grantResults.all { it -> it == 0 }) {
            getLocation()
        }
        else {
            getWetherData("BeiJing")
        }
    }

    private fun getLocation() {
        initMap()
    }

    private fun initMap() {
        mLocationClient = AMapLocationClient(context)
        mLocationClient!!.setLocationListener(this)
        initOption()
        mLocationClient!!.setLocationOption(mLocationOption)
        mLocationClient!!.stopLocation()
        mLocationClient!!.startLocation()
    }

    override fun onLocationChanged(amapLocation: AMapLocation?) {
        if (amapLocation != null) {
            latitude = amapLocation.latitude//获取纬度
            longitude = amapLocation.longitude//获取经度
            getWetherData("${latitude}&${longitude}")
        }
    }

    private fun getWetherData(location: String) {
        mLocationClient?.stopLocation()
        mLocationClient?.onDestroy()
        WeatherDataService.GetWetherList("d8d9cfec6b3f483eae480728172812", location, 5, this, WetherData::class.java)
    }

    private fun initOption() {
        mLocationOption = AMapLocationClientOption()
        mLocationOption!!.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn
        mLocationOption!!.isOnceLocationLatest = true
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption!!.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption!!.isNeedAddress = true
        //设置是否只定位一次,默认为false
        mLocationOption!!.isOnceLocation = false
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption!!.isWifiScan = true
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption!!.isMockEnable = false
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption!!.interval = 2000
    }

    @NeedsPermission(BaseActivity.READ_EXTERNAL_STORAGE_PERMISSION,
            BaseActivity.WRITE_EXTERNAL_STORAGE_PERMISSION,
            BaseActivity.READ_PHONE_STATE_PERMISSION,
            BaseActivity.CAMERA_PERMISSION,
            BaseActivity.ACCESS_FINE_LOCATION_PEMISSION,
            BaseActivity.ACCESS_CROSE_LOCATION_PERMISSION)
    fun showLocation() {

    }

    override fun onStart() {
        super.onStart()
        setWeather()
        ConsoleUtil.i(TAG, "-------Fragment onStart---------------")
    }

    override fun onResume() {
        super.onResume()
        ConsoleUtil.i(TAG, "-------Fragment onResume---------------")
    }

    override fun onPause() {
        super.onPause()
        mLocationClient?.stopLocation()
        ConsoleUtil.i(TAG, "-------Fragment onPause---------------")
    }

    override fun onStop() {
        super.onStop()
        ConsoleUtil.i(TAG, "-------Fragment onStop---------------")
    }

    @OnClick(R.id.img_registSchedule)
    internal fun goRegistSchedule() {
        val ft = fragmentManager!!.findFragmentByTag("RegistFragment")
        val args = Bundle()
        registScheduleFragment = if (ft == null) RegistScheduleFragment.newInstance(args) else ft as RegistScheduleFragment

        goView(registScheduleFragment!!, "RegistFragment")
    }

    @OnClick(R.id.img_contact)
    internal fun goContact() {
        val ft = fragmentManager!!.findFragmentByTag("ContactFragment")
        val args = Bundle()
        contactFragment = if (ft == null) ContactFragment.newInstance(args) else ft as ContactFragment

        goView(contactFragment!!, "ContactFragment")
    }

    @OnClick(R.id.img_searchSchedule)
    internal fun goSearchSchedule() {
        val ft = fragmentManager!!.findFragmentByTag("SearchScheduleFragment")
        val args = Bundle()
        val searchScheduleFragment = if (ft == null) SearchScheduleFragment.newInstance(args) else ft as SearchScheduleFragment

        goView(searchScheduleFragment, "SearchScheduleFragment")
    }

    @OnClick(R.id.img_applyList)
    internal fun goApplyList() {
        val ft = fragmentManager!!.findFragmentByTag("ApplyListFragment")
        val args = Bundle()
        val applyListFragment = if (ft == null) ApplyListFragment.newInstance(args) else ft as ApplyListFragment

        goView(applyListFragment, "ApplyListFragment")
    }

    @OnClick(R.id.img_approveList)
    internal fun goApproveList() {
        val ft = fragmentManager!!.findFragmentByTag("ApproveListFragment")
        val args = Bundle()
        val approveListFragment = if (ft == null) ApproveListFragment.newInstance(args) else ft as ApproveListFragment

        goView(approveListFragment, "ApproveListFragment")
    }

    private fun goView(to: Fragment, tag: String) {
        val from = fragmentManager!!.findFragmentByTag("MainFragment")
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out)
        if (from.isAdded) {
            transaction.hide(from).add(R.id.main_content, to, tag).addToBackStack(null).commit()
        } else {
            transaction.hide(from).show(to).commit()

        }
    }

    private fun showLoading() {
        if(loading.isShowing) loading.hide() else loading.show()
    }

    private fun formatWeekDay(): String {

        val mYear: String
        val mMonth: String
        val mDay: String
        var mWay: String

        val c = Calendar.getInstance()
        c.timeZone = TimeZone.getTimeZone("GMT+8:00")
        mYear = c.get(Calendar.YEAR).toString() // 获取当前年份
        mMonth = (c.get(Calendar.MONTH) + 1).toString()// 获取当前月份
        mDay = c.get(Calendar.DAY_OF_MONTH).toString()// 获取当前月份的日期号码
        mWay = c.get(Calendar.DAY_OF_WEEK).toString()

        val yearMothDay = "${mYear}年${mMonth}月${mDay}日"

        return when(mWay) {
            "1" -> "$yearMothDay   星期天"
            "2" -> "$yearMothDay   星期一"
            "3" -> "$yearMothDay   星期二"
            "4" -> "$yearMothDay   星期三"
            "5" -> "$yearMothDay   星期四"
            "6" -> "$yearMothDay   星期五"
            "7" -> "$yearMothDay   星期六"
            else -> {
                "$yearMothDay   星期天"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLocationClient?.stopLocation()
        mLocationClient?.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onSuccess(list: List<ApproveListInfo>) {
        val intent = Intent(activity, DeskCountChangeReceiver::class.java)
        intent.action = "EOAS_COUNT_CHANGED"
        intent.putExtra("COUNT", (if (list.isEmpty()) 0 else list.size).toString())
        context.sendBroadcast(intent)
    }

    override fun onFailure(code: Int, msg: String) {
        showLoading()
    }

    override fun onGetWetherSuccess(result: Any) {
        showLoading()
        recycle.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
        recycle.adapter = WetherAdapter((result as WetherData).forecast.forecastday)
    }

    override fun onGetWetherFailure() {
        showLoading()
    }

    companion object {
        fun newInstance(args: Bundle): MainFragment {
            val f = MainFragment()
            f.arguments = args
            return f
        }
    }
}
