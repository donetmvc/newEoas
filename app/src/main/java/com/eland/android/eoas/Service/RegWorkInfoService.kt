package com.eland.android.eoas.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.IBinder
import android.support.annotation.RequiresApi

import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.eland.android.eoas.R
import com.eland.android.eoas.Util.ConsoleUtil

/**
 * Created by liu.wenbin on 15/12/1.
 */
class RegWorkInfoService : Service(), AMapLocationListener, ScheduleService.IScheduleListener {

    //执行回调的好东东，不用去定义那么麻烦的接口咯
    var onAction = { _: Float -> Unit }

    private var scheduleService: ScheduleService? = null
    //声明AMapLocationClient类对象
    private var mLocationClient: AMapLocationClient? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (null != intent) {
            initParams(intent)
        }

        scheduleService = ScheduleService(applicationContext)
        scheduleService!!.setOnScheduleListener(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        icon = BitmapFactory.decodeResource(resources,
                R.mipmap.ic_launcher)

        return super.onStartCommand(intent, flags, startId)
    }

    fun startService(imei: String, isAm: String, type: String) {
        Companion.imei = imei
        Companion.isAm = isAm
        Companion.type = type
        initMap()
    }

    private fun initParams(intent: Intent) {
        imei = intent.getStringExtra("IMEI")
        isAm = intent.getStringExtra("ISAM")
        type = intent.getStringExtra("TYPE")

        if (type.isNotEmpty() && type == "AUTO") {
            startService(imei, isAm, "AUTO")
        } else {
            startService(imei, isAm, "NOAUTO")
        }
    }

    private fun initMap() {
        mLocationClient = AMapLocationClient(applicationContext)
        mLocationClient!!.setLocationListener(this)
        initOption()
        mLocationClient!!.setLocationOption(mLocationOption)

        //执行在子线程的
        startLocation()
    }

    private fun initOption() {
        mLocationOption = AMapLocationClientOption()
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption!!.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption!!.isNeedAddress = true
        //设置是否只定位一次,默认为false
        mLocationOption!!.isOnceLocation = false
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption!!.isWifiActiveScan = true
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption!!.isMockEnable = false
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption!!.interval = 2000
    }

    private fun startLocation() {
        ConsoleUtil.i(TAG, "----------RegWorkInfoService:" + "Location Start")
        mLocationClient!!.startLocation()
    }


    fun setOnLocationLinstener(iLocationListener: ILocationListener) {
        onLocationListener = iLocationListener
    }

    interface ILocationListener {
        fun onLocationSuccess(distance: Float)
    }

    override fun onLocationChanged(amapLocation: AMapLocation?) {
        @Synchronized if (amapLocation != null) {
            if (amapLocation.errorCode == 0) {
                val latitude = amapLocation.latitude//获取纬度
                val longitude = amapLocation.longitude//获取经度

                val startLatlng = LatLng(amapLocation.latitude, amapLocation.longitude)
                val endLatlng = LatLng(39.98023200, 116.49513900)
                val endLatlng1 = LatLng(39.97986400, 116.49460800)
                val distance1 = AMapUtils.calculateLineDistance(startLatlng, endLatlng)
                val distance2 = AMapUtils.calculateLineDistance(startLatlng, endLatlng1)

                //取最小距离，防止位置偏差
                distance = if (distance1 < distance2) distance1 else distance2

                ConsoleUtil.i(TAG, "----------RegWorkInfoService:" + distance)

                if (type == "AUTO") {
                    if (distance < 300.0) {
                        ConsoleUtil.i(TAG, "----------RegWorkInfoService:" + "Location end")
                        mLocationClient!!.stopLocation()
                        startRegService()
                    }
                } else {
                    //回调
                    onAction(distance)
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                ConsoleUtil.i(TAG, "location Error, ErrCode:"
                        + amapLocation.errorCode + ", errInfo:"
                        + amapLocation.errorInfo)

            }
        }
    }

    private fun startRegService() {
        ConsoleUtil.i(TAG, "----------RegWorkInfoService:" + "Regist start" + isAm)
        scheduleService!!.regScheduleAM(imei, isAm)
    }

    override fun onScheduleSuccess() {
        showNotification("S")
        stopSelf()
    }

    private fun showNotification(regType: String) {

        val message = if (regType == "S") "你已经成功出勤，Happy Day." else "已尝试10次，都失败，请联系管理人员."

//        if(SDK_INT >= 26) {
//            val id = "my_eoas_push"
//            // 用户可以看到的通知渠道的名字.
//            val name = "Eoas Push"
//            // 用户可以看到的通知渠道的描述
//            val description = "Eoas考勤通知"
//            val importance = NotificationManager.IMPORTANCE_HIGH;
//            val mChannel = if (SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel(id, name, importance)
//            }
//            else {
//                null
//            }
//            // 配置通知渠道的属性
//            mChannel?.description = description
//            // 设置通知出现时的闪灯（如果 android 设备支持的话）
//            mChannel?.enableLights(true)
//            mChannel?.lightColor = Color.RED
//            // 设置通知出现时的震动（如果 android 设备支持的话）
//            mChannel?.enableVibration(true);
//            mChannel?.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
//            //最后在notificationmanager中创建该通知渠道
//            notificationManager?.createNotificationChannel(mChannel);
//
//
//            notification = Notification.Builder(this, id)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setTicker("TickerText:" + "出勤通知")
//                    .setContentTitle("出勤通知")
//                    .setContentText(message)
//                    .setNumber(1)
//                    .build()
//        }
//        else {
//            notification = Notification.Builder(this)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setTicker("TickerText:" + "出勤通知")
//                    .setContentTitle("出勤通知")
//                    .setContentText(message)
//                    .setNumber(1)
//                    .build()
//        }

        val notification = android.support.v4.app.NotificationCompat.Builder(applicationContext, "0")
                .setLargeIcon(icon)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("出勤通知").setContentInfo("移动OA")
                .setContentTitle("出勤通知").setContentText(message)
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .build()

        notificationManager!!.notify(0, notification)
    }

    override fun onScheduleFailure(code: Int, msg: String?) {
        showNotification("F")
        stopSelf()
    }

    override fun onDestroy() {
        stopService()
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopService()
        return super.onUnbind(intent)
    }

    override fun onBind(intent: Intent): IBinder? {
        return myBinder()
    }

    fun stopService() {
        if (null != scheduleService) {
            scheduleService!!.cancel()
        }
        mLocationClient!!.stopLocation()
        mLocationClient!!.onDestroy()
        stopSelf()
    }

    companion object {
        var mLocationOption: AMapLocationClientOption? = null
        var TAG = "EOAS"
        var imei = ""
        var isAm = ""
        var type = ""
        var distance = 0.00f
        var onLocationListener: ILocationListener? = null
        private var notificationManager: NotificationManager? = null
        private var icon: Bitmap? = null

        open class myBinder : Binder() {
            val service: RegWorkInfoService
                get() = RegWorkInfoService()
        }
    }

}


