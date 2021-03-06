package com.eland.android.eoas.Fragment

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.R
import com.eland.android.eoas.Util.ConsoleUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.SystemMethodUtil
import com.eland.android.eoas.Util.ToastUtil

import java.io.IOException
import java.util.GregorianCalendar

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.eland.android.eoas.Activity.MainActivity
import com.eland.android.eoas.DeviceInfoFactory.GetDeviceInfo
import com.eland.android.eoas.Service.*
import pl.droidsonroids.gif.AnimationListener
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.lang.ref.WeakReference

/**
 * Created by liu.wenbin on 15/11/30.
 */
class RegisterScheduleFragment : Fragment, AnimationListener, ScheduleService.IScheduleListener {

    @BindView(R.id.img_gif)
    lateinit var imgGif: GifImageView
    @BindView(R.id.txt_distance)
    lateinit var txtDistance: TextView

    private val TAG = "EOAS"
    private var rootView: View? = null
    private var gifDrawable: GifDrawable? = null
    private val autoRegister = false
    private var scheduleService: ScheduleService? = null
    private var imei: String? = null
    private var am_pm: Int = 0
    private var isAM: String? = null
    private var regWorkInfoService: RegWorkInfoService? = null
    var CANREGIST = 0
    var NOTREGIST = 1

    internal var conn: ServiceConnection? = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {

        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            //返回一个MsgService对象
            regWorkInfoService = (service as RegWorkInfoService.Companion.myBinder).service
            regWorkInfoService!!.startService(imei!!, isAM!!, "MANUAL")

            regWorkInfoService!!.onAction = fun(distance: Float) {
                val msg = Message()
                if (distance < 300.00f) {
                    regWorkInfoService!!.stopService()
                    msg.what = CANREGIST
                    handler.sendMessage(msg)
                } else {
                    msg.what = NOTREGIST
                    msg.obj = distance
                    handler.sendMessage(msg)
                }
            }
        }
    }

    var handler: Handler = MyHandler(WeakReference(this))

    constructor() : super() {}

    @SuppressLint("ValidFragment")
    constructor(context: Context) {
        //this.context = context;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.registschedule_fragment, null)
        ButterKnife.bind(this, rootView!!)

        initFragment()

        if (autoRegister) {
            setActiveImg()
        }

        return rootView
    }

    private fun initFragment() {
        scheduleService = ScheduleService(context)
        scheduleService!!.setOnScheduleListener(this)
//        telephonyManager = context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        imei = GetDeviceInfo(context).getDeviceId() //telephonyManager?.deviceId
        val gregorianCalendar = GregorianCalendar()
        am_pm = gregorianCalendar.get(GregorianCalendar.AM_PM)
        if (am_pm == 0) {
            isAM = "AM"
            ConsoleUtil.i(TAG, "AM")
        } else {
            isAM = "PM"
            ConsoleUtil.i(TAG, "PM")
        }
    }

    @OnClick(R.id.img_gif)
    internal fun register() {
        if (gifDrawable != null) {
            setDisableImg()
        } else {
            setActiveImg()
        }
    }

    private fun setActiveImg() {
        try {
            gifDrawable = GifDrawable(context.resources, R.drawable.schedule_active)
            imgGif.setImageDrawable(gifDrawable)
            gifDrawable!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        startRegister()
    }

    private fun startRegister() {
        if (isAM == "PM") {
            val intent = Intent(context, RegWorkInfoService::class.java)
            context.bindService(intent, conn, Context.BIND_AUTO_CREATE)
        } else {
            scheduleService!!.regSchedulePM(imei!!, isAM!!)
        }
    }

    private fun setDisableImg() {
        if (null != gifDrawable && gifDrawable!!.isPlaying) {
            //gifDrawable.stop();
            gifDrawable!!.recycle()
            val theme = SharedReferenceHelper.getInstance(activity).getValue(Constant.EOAS_THEME)
            if (theme.isNotEmpty() && theme == "RED") {
                imgGif.setImageDrawable(context.resources.getDrawable(R.drawable.schedule_nomor))
            } else {
                imgGif.setImageDrawable(context.resources.getDrawable(R.drawable.schedule_nomor_blue))
            }
            gifDrawable = null
        }
        stopRegister()
    }

    private fun stopRegister() {
        if (SystemMethodUtil.isWorked(context, "RegWorkInfoService")) {
            ConsoleUtil.i(TAG, "---------------Service is running,you can stop it.--------------")
            if (null != conn) {
                context!!.unbindService(conn)
                ConsoleUtil.i(TAG, "---------------stop success.--------------")
            }
            if(null != regWorkInfoService) {
                regWorkInfoService!!.stopService()
            }
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scheduleService!!.cancel()
        stopRegister()
//        ButterKnife.unbind(this)
    }

    override fun onAnimationCompleted(loopNumber: Int) {
        val view = view
        if (view != null) {
            //ToastUtil.showToast(context, "完事了啊", Toast.LENGTH_LONG);
        }
    }

    override fun onScheduleSuccess() {
        if (context != null) {
            ToastUtil.showToast(context, "打卡成功.", Toast.LENGTH_LONG)
            setDisableImg()
        }
    }

    override fun onScheduleFailure(code: Int, msg: String?) {
        if (context != null) {
            ToastUtil.showToast(context, msg!!, Toast.LENGTH_LONG)
            setDisableImg()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun updateDistance(message: Message) {
        @Synchronized when(message.what) {
             CANREGIST -> {
                if(freeConn) {
                    context!!.unbindService(conn!!)
                    freeConn = false
                }
                scheduleService!!.regScheduleAM(imei!!, isAM!!)
                txtDistance.visibility = View.GONE
            }
            NOTREGIST -> {
                val distance = message.obj as Float
                txtDistance.visibility = View.VISIBLE
                txtDistance.text = "距离考勤点还有${distance.toString()}米"
            }
            else -> {
            }
        }
    }

    companion object {

        @kotlin.jvm.Volatile
        var freeConn  = true

        fun newInstance(args: Bundle): RegisterScheduleFragment {
            val f = RegisterScheduleFragment()
            f.arguments = args
            return f
        }

        private class MyHandler(private val fragment: WeakReference<RegisterScheduleFragment>): Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                fragment.get()?.updateDistance(msg)
            }
        }

    }
}
