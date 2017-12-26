package com.eland.android.eoas.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.eland.android.eoas.Activity.LoginActivity
import com.eland.android.eoas.Activity.MainActivity
import com.eland.android.eoas.Application.EOASApplication
import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.Model.RegAutoInfo
import com.eland.android.eoas.R
import com.eland.android.eoas.Receiver.AutoReceiver
import com.eland.android.eoas.Service.LogOutService
import com.eland.android.eoas.Service.RegAutoService
import com.eland.android.eoas.Service.UpdatePhoneNmService
import com.eland.android.eoas.Service.UploadFileService
import com.eland.android.eoas.Util.CacheInfoUtil
import com.eland.android.eoas.Util.ChooseImageUtil
import com.eland.android.eoas.Util.ConsoleUtil
import com.eland.android.eoas.Util.FileUtil
import com.eland.android.eoas.Util.ProgressUtil
import com.eland.android.eoas.Util.SharedReferenceHelper
import com.eland.android.eoas.Util.SystemMethodUtil
import com.eland.android.eoas.Util.ToastUtil
import com.eland.android.eoas.Views.CircleImageView
import com.eland.android.eoas.Views.PhoneNumberView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.pgyersdk.javabean.AppBean
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import com.rey.material.widget.Switch

import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.Date

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import cn.jpush.android.api.JPushInterface
import me.drakeet.materialdialog.MaterialDialog


/**
 * Created by liu.wenbin on 15/12/3.
 */
@SuppressLint("ValidFragment")
class DrawerFragment : Fragment, ChooseImageUtil.IOnCarmerListener, ChooseImageUtil.IOnAlbumListener, UploadFileService.IOnUploadListener, PhoneNumberView.OnActionSheetSelected, UpdatePhoneNmService.IOnUpdateListener, ProgressUtil.IOnDialogConfirmListener {

    @BindView(R.id.img_photo)
    lateinit var imgPhoto: CircleImageView
    @BindView(R.id.txt_name)
    lateinit var txtName: TextView
    @BindView(R.id.txt_tel)
    lateinit var txtTel: TextView
    @BindView(R.id.img_more)
    lateinit var imgMore: ImageView
    @BindView(R.id.txt_email)
    lateinit var txtEmail: TextView
    @BindView(R.id.user_info_layout)
    lateinit var userInfoLayout: RelativeLayout
    @BindView(R.id.txt_logout)
    lateinit var txtLogout: TextView
    @BindView(R.id.lin_logout)
    lateinit var linLogout: LinearLayout
    @BindView(R.id.txt_versionname)
    lateinit var txtVersionname: TextView
    @BindView(R.id.lin_update)
    lateinit var linUpdate: LinearLayout
    @BindView(R.id.txt_theme_red)
    lateinit var txtThemeRed: TextView
    @BindView(R.id.txt_theme_blue)
    lateinit var txtThemeBlue: TextView

    private val TAG = "EOAS"
    private var mainActivity: MainActivity? = null
    private var rootView: View? = null
    private var aSwitch: Switch? = null
    private var bSwitch: Switch? = null
    private var loginId: String? = null
    private var imei: String? = null
    private var userName: String? = null
    private var cellNo: String? = null
    private var email: String? = null
    private var chooseImageUtil: ChooseImageUtil? = null
    private var dateTime: String? = null
    private var imgDialog: com.rey.material.app.Dialog? = null
    private var httpDialog: Dialog? = null
    private var updateNumDialog: Dialog? = null

    private var imgUri = ""//"http://182.92.65.253:30001/Eland.EOAS/images/chengcuicui.jpg";
    private var imageLocalPath = ""
    private var fileName = ""
    internal var bitmap: Bitmap? = null
    private var uploadFileService: UploadFileService? = null
    private var animation: Animation? = null
    private var updateService: UpdatePhoneNmService? = null

    //clock
    private var pi: PendingIntent? = null
    private var alar: AlarmManager? = null
    private var running: Boolean? = false

    //check update
    private var updateManagerListener: UpdateManagerListener? = null
    private var downUri: String? = null
    private var dialogUtil: ProgressUtil? = null
    private var updateDialog: MaterialDialog? = null

    //get app version info
    internal var packageManager: PackageManager? = null
    internal var packageInfo: PackageInfo? = null

    internal var options = DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.mipmap.eland_log)
            .showImageOnFail(R.mipmap.eland_log)
            .resetViewBeforeLoading(true)
            .cacheOnDisc(true)
            .cacheInMemory(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            .displayer(FadeInBitmapDisplayer(300))
            .build()

    var handler: Handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> setBitmap(msg.obj as Bitmap)
                1 -> {
                }
                2 -> txtTel!!.text = msg.obj as String
                else -> {
                }
            }
        }
    }

    constructor() : super() {}

    constructor(mainActivity: MainActivity) {
        this.mainActivity = mainActivity
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.fragment_drawer, container, false)
        ButterKnife.bind(this, rootView!!)

        aSwitch = rootView!!.findViewById(R.id.sw_auto) as Switch
        bSwitch = rootView!!.findViewById(R.id.sw_receive) as Switch
        chooseImageUtil = ChooseImageUtil()
        uploadFileService = UploadFileService(context)
        updateService = UpdatePhoneNmService(context)
        chooseImageUtil!!.setOnAlbumListener(this)
        chooseImageUtil!!.setOnCarmerListener(this)
        uploadFileService!!.setUploadListener(this)
        dialogUtil = ProgressUtil()
        dialogUtil!!.setOnDialogConfirmListener(this)

        packageManager = activity.packageManager

        try {
            packageInfo = packageManager?.getPackageInfo(activity.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        initView()
        return rootView
    }

    private fun initView() {
        loginId = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGINID)
        cellNo = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGINID_CELLNO)
        email = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGINID_EMAIL)
        userName = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGINID_USERNAME)
        animation = AnimationUtils.loadAnimation(context, R.anim.image_circel)

        initClock()

        //设置用户名
        if (!loginId!!.isEmpty()) {
            fileName = loginId!!.replace(".", "")
            imgUri = EOASApplication.instance.photoUri + fileName + ".jpg"
        } else {
            txtName!!.text = "无名氏"
            fileName = "sysadmin"
        }
        //设置用户
        txtName!!.text = userName
        //设置邮箱地址
        txtEmail!!.text = email
        //设置电话号码
        txtTel!!.text = cellNo

        //设置自动出勤与否
        if (CacheInfoUtil.loadIsRegAuto(context, loginId!!)!!) {
            aSwitch!!.isChecked = true
            startRegAutoService()
        } else {
            aSwitch!!.isChecked = false
            stopRegAutoService()
        }

        //设置用户是否接收push
        if (CacheInfoUtil.loadIsReceive(context, loginId!!)!!) {
            bSwitch!!.isChecked = true
            JPushInterface.resumePush(context.applicationContext)
        } else {
            bSwitch!!.isChecked = false
            JPushInterface.stopPush(context.applicationContext)
        }


        //设置头像
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(activity))
        ImageLoader.getInstance().displayImage(imgUri, imgPhoto!!, options)

        //设置版本信息
        txtVersionname!!.text = "当前版本(" + packageInfo?.versionName + ")"

        aSwitch!!.setOnCheckedChangeListener { view, checked ->
            ConsoleUtil.i(TAG, checked.toString())
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            imei = telephonyManager.deviceId

            val list = ArrayList<RegAutoInfo>()
            val reg = RegAutoInfo()
            reg.userId = loginId
            reg.imei = imei

            if (checked) {
                reg.isRegAuto = "TRUE"
                list.add(reg)
                CacheInfoUtil.saveIsRegAuto(context, list)

                startRegAutoService()
            } else {
                reg.isRegAuto = "FALSE"
                list.add(reg)
                CacheInfoUtil.saveIsRegAuto(context, list)

                stopRegAutoService()
            }
        }

        bSwitch!!.setOnCheckedChangeListener { view, checked ->
            val list = ArrayList<RegAutoInfo>()
            val reg = RegAutoInfo()
            reg.userId = loginId

            if (checked) {
                reg.isReceive = "TRUE"
                list.add(reg)
                CacheInfoUtil.saveIsReceive(context, list)

                JPushInterface.resumePush(context.applicationContext)
            } else {
                reg.isReceive = "FALSE"
                list.add(reg)
                CacheInfoUtil.saveIsReceive(context, list)

                JPushInterface.stopPush(context.applicationContext)
            }
        }
    }

    private fun initClock() {
        val intent = Intent(activity, AutoReceiver::class.java)
        intent.action = "REG_AUTO"
        alar = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pi = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun startRegAutoService() {
        //设置闹钟服务
        alar!!.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (10 * 1000).toLong(), pi)
    }

    private fun stopRegAutoService() {
        //取消闹钟
        alar!!.cancel(pi)
        //check service is running
        running = checkRegServiceIsRunning()

        if (running!!) {
            //stop service
            val intent = Intent(activity, RegAutoService::class.java)
            context.stopService(intent)
        }
    }

    private fun checkRegServiceIsRunning(): Boolean? {
        running = SystemMethodUtil.isWorked(context, "RegAutoService")
        return running
    }

    @OnClick(R.id.img_photo)
    internal fun showChooseImage() {
        imgDialog = chooseImageUtil!!.showChooseImageDialog(context)
    }

    @OnClick(R.id.img_more)
    internal fun showMoreAction() {
        updateNumDialog = PhoneNumberView.showNumActionSheete(mainActivity, this)
        //dialog.se
    }

    @OnClick(R.id.lin_update)
    internal fun checkUpdate() {
        updateManagerListener = object : UpdateManagerListener() {
            override fun onNoUpdateAvailable() {
                ToastUtil.showToast(context, "已经是最新版本啦", Toast.LENGTH_SHORT)
            }

            override fun onUpdateAvailable(result: String) {
                val appBean = UpdateManagerListener.getAppBeanFromString(result)
                val message = appBean.releaseNote
                downUri = appBean.downloadURL

                updateDialog = dialogUtil!!.showDialogUpdate(message, resources.getString(R.string.hasupdate), mainActivity!!)
                updateDialog!!.show()
            }
        }

        //检测更新
        PgyUpdateManager.register(activity, updateManagerListener)
    }

    @OnClick(R.id.lin_logout)
    internal fun logOut() {
        clearCach()
        //关闭push服务
        JPushInterface.stopPush(context.applicationContext)
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)

        val serviceIntent = Intent(activity, LogOutService::class.java)
        context.startService(serviceIntent)

        activity.finish()
    }

    @OnClick(R.id.txt_theme_red) internal fun theme_red() {
        SharedReferenceHelper.getInstance(context).setValue(Constant.EOAS_THEME, "RED")
        SharedReferenceHelper.getInstance(context).setValue(Constant.EOAS_ISCHANGINGTHEME, "TURE")
        activity.recreate()
        //ToastUtil.showToast(getContext(), "red", Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.txt_theme_blue) internal fun theme_blue() {
        SharedReferenceHelper.getInstance(context).setValue(Constant.EOAS_THEME, "BLUE")
        SharedReferenceHelper.getInstance(context).setValue(Constant.EOAS_ISCHANGINGTHEME, "TURE")
        activity.recreate()
        //ToastUtil.showToast(getContext(), "blue", Toast.LENGTH_SHORT);
    }

    private fun clearCach() {
        SharedReferenceHelper.getInstance(context).clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateService!!.cancel()
        uploadFileService!!.cancel()
        if (null != bitmap) {
            bitmap!!.recycle()
        }
//        ButterKnife.unbind(this)
    }

    override fun onAlbumClick() {
        ConsoleUtil.i(TAG, "Album")
        val date = Date(System.currentTimeMillis())
        dateTime = date.time.toString() + ""
        getPicFromAlbum()
        imgDialog!!.dismiss()
    }

    override fun onCarmerClick() {
        ConsoleUtil.i(TAG, "Carmer")
        val date = Date(System.currentTimeMillis())
        dateTime = date.time.toString() + ""
        getPicFromCamere()
        imgDialog!!.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_RESULT -> {
                }
                REQUEST_CODE_IMAGE ->
                    //String fileName = null;
                    if (data != null) {
                        val originalUri = data.data
                        val cr = activity.contentResolver
                        //FileUtil.compressImageFromFile(fileName);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(cr, originalUri)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        imageLocalPath = FileUtil.saveToSdCard(bitmap!!, context, fileName)
                        ConsoleUtil.d(TAG, "------imageLocalPath--------" + imageLocalPath)
                        //imgPhoto.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        //imgPhoto.setImageBitmap(bitmap);

                        val message = Message()
                        message.obj = bitmap

                        if (null != animation) {
                            imgPhoto!!.startAnimation(animation)
                        }

                        uploadFileService!!.uploadFile(imageLocalPath, fileName, message)
                    }
                REQUEST_CODE_CAMERA -> {
                    val files = FileUtil.getCacheDirectory(context, true, "pic").toString() + dateTime!!
                    val file = File(files)
                    if (file.exists()) {
                        val bitmap = FileUtil.compressImageFromFile(files)
                        imageLocalPath = FileUtil.saveToSdCard(bitmap, context, fileName)
                        //imgPhoto.setImageBitmap(bitmap);
                        val message = Message()
                        message.obj = bitmap

                        if (null != animation) {
                            imgPhoto!!.startAnimation(animation)
                        }

                        uploadFileService!!.uploadFile(imageLocalPath, fileName, message)
                    } else {

                    }
                }
                else -> {
                }
            }
        }
    }

    private fun setBitmap(bitmap: Bitmap) {
        imgPhoto!!.setImageBitmap(bitmap)
    }

    /**
     * 图库
     */
    private fun getPicFromAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, REQUEST_CODE_IMAGE)
    }

    /**
     * 照相机
     */
    private fun getPicFromCamere() {
        val f = File(FileUtil.getCacheDirectory(context, true, "pic").toString() + dateTime!!)
        if (f.exists()) {
            f.delete()
        }
        try {
            f.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val uri = Uri.fromFile(f)
        Log.d(TAG, "---------------" + uri + "")

        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camera.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(camera, REQUEST_CODE_CAMERA)
    }

    override fun onUploadSuccess(message: Message) {
        handler.sendMessage(message)
        clean()
        ToastUtil.showToast(context, "上传头像成功", Toast.LENGTH_LONG)
    }

    override fun onUploadFailure(message: Message) {
        handler.sendMessage(message)
        clean()
        ToastUtil.showToast(context, "上传失败，请重试", Toast.LENGTH_LONG)
    }

    private fun clean() {
        imgPhoto!!.clearAnimation()
        ImageLoader.getInstance().clearDiskCache()
        ImageLoader.getInstance().clearMemoryCache()
    }

    override fun onClick(numValue: String) {
        //ConsoleUtil.i(TAG, numValue);
        if (numValue.isEmpty()) {
            ToastUtil.showToast(context, "电话号码不能为空", Toast.LENGTH_LONG)
            return
        }
        if (!numValue.isEmpty() && numValue.length != 11) {
            ToastUtil.showToast(context, "电话号码位数不正确", Toast.LENGTH_LONG)
            return
        }
        httpDialog = ProgressUtil().showHttpLoading(context)

        goUpdatePhoneNumber(numValue)
    }

    private fun goUpdatePhoneNumber(numValue: String) {
        updateService!!.updateCellNo(loginId!!, numValue, this)
    }

    override fun onUpdateSuccess(cellNo: String) {
        ToastUtil.showToast(context, "电话号码修改成功", Toast.LENGTH_LONG)
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_CELLNO, cellNo)

        val message = Message()
        message.what = 2
        message.obj = cellNo
        handler.sendMessage(message)

        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }
        if (updateNumDialog!!.isShowing) {
            updateNumDialog!!.dismiss()
        }
    }

    override fun onUpdateFailure(code: Int, msg: String?) {
        ToastUtil.showToast(context, "电话号码修改失败", Toast.LENGTH_LONG)
        if (httpDialog!!.isShowing) {
            httpDialog!!.dismiss()
        }
    }

    override fun OnDialogConfirmListener() {
        if (null != updateDialog) {
            updateDialog!!.dismiss()
            UpdateManagerListener.startDownloadTask(activity, downUri)
        }
    }

    companion object {

        const val REQUEST_CODE_CAMERA = 1
        const val REQUEST_CODE_IMAGE = 2
        const val REQUEST_CODE_RESULT = 3
    }
}
