package com.eland.android.eoas.Util

import android.graphics.Bitmap

import com.eland.android.eoas.R
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer

/**
 * Created by liu.wenbin on 15/12/15.
 */
object ImageLoaderOption {

    // // 设置图片在下载期间显示的图片
    // // 设置图片Uri为空或是错误的时候显示的图片
    // // 设置图片加载/解码过程中错误时候显示的图片
    // 设置下载的图片是否缓存在内存中
    // 设置下载的图片是否缓存在SD卡中
    // 设置图片以如何的编码方式显示
    // 设置图片的解码类型
    // .decodingOptions(android.graphics.BitmapFactory.Options
    // decodingOptions)//设置图片的解码配置
    // 设置图片下载前的延迟
    // .delayBeforeLoading(int delayInMillis)//int
    // delayInMillis为你设置的延迟时间
    // 设置图片加入缓存前，对bitmap进行设置
    // 。preProcessor(BitmapProcessor preProcessor)
    // 设置图片在下载前是否重置，复位
    // .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
    // 淡入
    val optionsForNormal: DisplayImageOptions
        get() = DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_load_bg)
                .showImageForEmptyUri(R.drawable.default_fail_bg)
                .showImageOnFail(R.drawable.default_fail_bg)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .resetViewBeforeLoading(true)
                .displayer(FadeInBitmapDisplayer(400))
                .build()

    /**
     * @return ViewPage options
     */
    val optionsForPager: DisplayImageOptions
        get() = DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_load_bg)
                .showImageForEmptyUri(R.drawable.default_fail_bg)
                .showImageOnFail(R.drawable.default_fail_bg)
                .cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(false)
                .displayer(FadeInBitmapDisplayer(400))
                .bitmapConfig(Bitmap.Config.ALPHA_8).build()

    fun getOptionsById(defaultDrawable: Int): DisplayImageOptions {
        return DisplayImageOptions.Builder()
                .showImageOnLoading(defaultDrawable)
                .showImageForEmptyUri(defaultDrawable)
                .showImageOnFail(defaultDrawable)
                .cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(false)
                .displayer(FadeInBitmapDisplayer(400))
                .bitmapConfig(Bitmap.Config.ALPHA_8).build()
    }
}
