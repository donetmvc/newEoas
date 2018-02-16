package com.eland.android.eoas.Views.stepView

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

import com.eland.android.eoas.R


class StepsView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                          defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), StepsViewIndicator.OnDrawListener {

    private var mStepsViewIndicator: StepsViewIndicator? = null
    private var mLabelsLayout: FrameLayout? = null
    private var mLabels: ArrayList<String?>? = null
    private var mCompletedPosition: Int = 0
    private var mLabelColor = Color.BLACK
    private var mIndicatorColor = Color.RED

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.StepsViewIndicator, defStyleAttr, 0)
        val count = ta.indexCount
        for (i in 0 until count) {
            val itemId = ta.getIndex(i) // 获取某个属性的Id值
            when (itemId) {
                R.styleable.StepsViewIndicator_stepViewColor // 设置当前按钮的状态
                -> mIndicatorColor = ta.getColor(itemId, Color.RED)
                else -> {
                }
            }
        }
        init()
    }

    private fun init() {
        val rootView = LayoutInflater.from(context).inflate(R.layout.widget_steps_view, this)
        mStepsViewIndicator = rootView.findViewById<View>(R.id.steps_indicator_view) as StepsViewIndicator
        mStepsViewIndicator!!.setDrawListener(this)
        mLabelsLayout = rootView.findViewById<View>(R.id.labels_container) as FrameLayout
    }

    fun setLabels(labels: ArrayList<String?>): StepsView {
        mLabels = labels
        mStepsViewIndicator!!.setSize(mLabels!!.size)
        return this
    }

    fun getCompletedPosition(): Int {
        return mCompletedPosition
    }

    fun setCompletedPosition(completedPosition: Int): StepsView {
        mCompletedPosition = completedPosition
        mStepsViewIndicator!!.setCompletedPosition(completedPosition)
        return this
    }

    fun setColorIndicator(color: Int): StepsView {
        mStepsViewIndicator!!.setThumbColor(mIndicatorColor)
        return this
    }

    fun setBarColor(color: Int): StepsView {
        mStepsViewIndicator!!.setBarColor(color)
        return this
    }

    fun setLabelColor(labelColor: Int): StepsView {
        mLabelColor = labelColor
        return this
    }

    override fun onFinish() {
        drawLabels()
    }

    private fun drawLabels() {
        val indicatorPosition = mStepsViewIndicator!!.thumbContainerXPosition

        if (mLabels != null) {
            for (i in mLabels!!.indices) {
                val textView = TextView(context)
                textView.text = mLabels!![i]
                textView.setTextColor(mLabelColor)
                textView.x = indicatorPosition[i]
                textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)

                if (i <= mCompletedPosition) {
                    textView.setTypeface(null, Typeface.BOLD)
                }

                mLabelsLayout!!.addView(textView)
            }
        }
    }

}
