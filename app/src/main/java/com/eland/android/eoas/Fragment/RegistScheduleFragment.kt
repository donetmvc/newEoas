package com.eland.android.eoas.Fragment

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.support.v4.app.Fragment
import android.telephony.TelephonyManager
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
import com.eland.android.eoas.Service.*
import com.eland.android.eoas.Service.RegWorkInfoService.Companion.distance
import pl.droidsonroids.gif.AnimationListener
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

/**
 * Created by liu.wenbin on 15/11/30.
 */
class RegistScheduleFragment : Fragment, AnimationListener, ScheduleService.IScheduleListener {

    @BindView(R.id.img_gif)
    var imgGif: GifImageView? = null
    @BindView(R.id.txt_distance)
    var txtDistance: TextView? = null
    private val TAG = "EOAS"
    private var rootView: View? = null
    private var gifDrawable: GifDrawable? = null
    private val autoRegist = false
    private var scheduleService: ScheduleService? = null
    internal var telephonyManager: TelephonyManager? = null
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
            regWorkInfoService = (service as RegWorkInfoService.myBinder).service
            regWorkInfoService!!.startService(imei!!, isAM!!, "MANUAL")

            regWorkInfoService!!.onAction = fun(distance: Float) {
                val msg = Message()
                if (distance < 300.00f) {
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

    var handler: Handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (null != context && null != txtDistance) {
                if (msg.what == CANREGIST) {
                    context!!.unbindService(conn!!)
                    scheduleService!!.regScheduleAM(imei!!, isAM!!)
                    txtDistance!!.visibility = View.GONE
                } else {
                    val distance = msg.obj as Float
                    txtDistance!!.visibility = View.VISIBLE
                    txtDistance!!.text = "距离考勤点还有" + distance.toString() + "米"
                }
            }
        }
    }

//    var handler: Handler = Handler().handleMessage({
//
//    })

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

        if (autoRegist) {
            setActiveImg()
        }

        return rootView
    }

    private fun initFragment() {
        scheduleService = ScheduleService(context)
        scheduleService!!.setOnScheduleListener(this)
        telephonyManager = context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        imei = telephonyManager?.deviceId
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
    internal fun regist() {
        if (gifDrawable != null) {
            setDisableImg()
        } else {
            setActiveImg()
        }
    }

    private fun setActiveImg() {
        try {
            gifDrawable = GifDrawable(getContext().resources, R.drawable.schedule_active)
            imgGif!!.setImageDrawable(gifDrawable)
            gifDrawable!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        startRegist()
    }

    private fun startRegist() {
        if (isAM == "AM") {
            val intent = Intent(context, RegWorkInfoService::class.java)
            context!!.bindService(intent, conn!!, Context.BIND_AUTO_CREATE)
        } else {
            scheduleService!!.regSchedulePM(imei!!, isAM!!)
        }
    }

    private fun setDisableImg() {
        if (null != gifDrawable && gifDrawable!!.isPlaying) {
            //gifDrawable.stop();
            gifDrawable!!.recycle()
            val theme = SharedReferenceHelper.getInstance(getContext()).getValue(Constant.EOAS_THEME)
            if (!theme.isEmpty() && theme == "RED") {
                imgGif!!.setImageDrawable(context!!.resources.getDrawable(R.drawable.schedule_nomor))
            } else {
                imgGif!!.setImageDrawable(context!!.resources.getDrawable(R.drawable.schedule_nomor_blue))
            }
            gifDrawable = null
        }
        stopRegist()
    }

    private fun stopRegist() {
        if (SystemMethodUtil.isWorked(getContext(), "RegWorkInfoService")) {
            ConsoleUtil.i(TAG, "---------------Service is running,you can stop it.--------------")
            if (null != conn) {
                context!!.unbindService(conn!!)
                ConsoleUtil.i(TAG, "---------------stop success.--------------")
            }
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scheduleService!!.cancel()
//        ButterKnife.unbind(this)
    }

    override fun onAnimationCompleted(loopNumber: Int) {
        val view = view
        if (view != null) {
            //ToastUtil.showToast(context, "完事了啊", Toast.LENGTH_LONG);
        }
    }

    override fun onScheduleSuccess() {
        if (context != null && imgGif != null) {
            ToastUtil.showToast(context, "打卡成功.", Toast.LENGTH_LONG)
            setDisableImg()
        }
    }

    override fun onScheduleFailure(code: Int, msg: String?) {
        if (context != null && imgGif != null) {
            ToastUtil.showToast(context, msg!!, Toast.LENGTH_LONG)
            setDisableImg()
        }
    }

    companion object {

        fun newInstance(args: Bundle): RegistScheduleFragment {
            val f = RegistScheduleFragment()
            f.arguments = args
            return f
        }
    }
}
