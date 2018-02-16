package com.eland.android.eoas.Views.stepView

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

import com.eland.android.eoas.R

import java.util.ArrayList


class StepsViewIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {

    private val startPaint = Paint()
    private val paint = Paint()
    private val selectedPaint = Paint()

    private val mThumbContainerXPosition = ArrayList<Float>()
    private var mDrawListener: OnDrawListener? = null

    val thumbContainerXPosition: List<Float>
        get() = mThumbContainerXPosition

    init {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.StepsViewIndicator)
        mNumOfStep = a.getInt(R.styleable.StepsViewIndicator_numOfSteps, 0)
        a.recycle()

        init()
    }

    private fun init() {
        mLineHeight = 0.2f * THUMB_SIZE
        mThumbRadius = 0.4f * THUMB_SIZE
        mCircleRadius = 0.7f * mThumbRadius
        mPadding = 0.5f * THUMB_SIZE
    }

    fun setSize(size: Int) {
        mNumOfStep = size
        setContainerXPosition()
        invalidate()
        //mDrawListener.onFinish();
    }

    private fun setContainerXPosition() {
        mCenterY = 0.5f * height
        mLeftX = mPadding
        mLeftY = mCenterY - mLineHeight / 2
        mRightX = width - mPadding
        mRightY = 0.5f * (height + mLineHeight)
        mDelta = (mRightX - mLeftX) / (mNumOfStep - 1)

        mThumbContainerXPosition.add(mLeftX)
        (1 until mNumOfStep - 1).mapTo(mThumbContainerXPosition) { mLeftX + it * mDelta }
        mThumbContainerXPosition.add(mRightX)
        mDrawListener!!.onFinish()
    }

    fun setDrawListener(drawListener: OnDrawListener) {
        mDrawListener = drawListener
    }

    public override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mCenterY = 0.5f * height
        mLeftX = mPadding
        mLeftY = mCenterY - mLineHeight / 2
        mRightX = width - mPadding
        mRightY = 0.5f * (height + mLineHeight)
        mDelta = (mRightX - mLeftX) / (mNumOfStep - 1)

        //mThumbContainerXPosition.add(mLeftX);
        (1 until mNumOfStep - 1).mapTo(mThumbContainerXPosition) { mLeftX + it * mDelta }
        //mThumbContainerXPosition.add(mRightX);
        mDrawListener!!.onFinish()
    }

    @Synchronized override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = 200
        if (View.MeasureSpec.UNSPECIFIED != View.MeasureSpec.getMode(widthMeasureSpec)) {
            width = View.MeasureSpec.getSize(widthMeasureSpec)
        }
        var height = THUMB_SIZE + 20
        if (View.MeasureSpec.UNSPECIFIED != View.MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, View.MeasureSpec.getSize(heightMeasureSpec))
        }
        setMeasuredDimension(width, height)
    }

    fun setCompletedPosition(position: Int) {
        mCompletedPosition = position
        invalidate()
    }

    fun reset() {
        setCompletedPosition(0)
    }

    fun setThumbColor(thumbColor: Int) {
        mThumbColor = thumbColor
    }

    fun setBarColor(barColor: Int) {
        mBarColor = barColor
    }

    @Synchronized override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        startPaint.isAntiAlias = true
        startPaint.color = mStartColor
        startPaint.style = Paint.Style.STROKE
        startPaint.strokeWidth = 2f

        // Draw rect bounds
        paint.isAntiAlias = true
        paint.color = mBarColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f

        selectedPaint.isAntiAlias = true
        selectedPaint.color = mThumbColor
        selectedPaint.style = Paint.Style.STROKE
        selectedPaint.strokeWidth = 2f



        // Draw rest of the circle'Bounds
        for (i in mThumbContainerXPosition.indices) {
            if(i == 0) {
                canvas.drawCircle(mThumbContainerXPosition[i], mCenterY, mCircleRadius, startPaint)
            }
            else {
                canvas.drawCircle(mThumbContainerXPosition[i], mCenterY, mCircleRadius,
                        if (i <= mCompletedPosition) selectedPaint else paint)
            }
        }

        paint.style = Paint.Style.FILL
        selectedPaint.style = Paint.Style.FILL
        for (i in 0 until mThumbContainerXPosition.size - 1) {
            val pos = mThumbContainerXPosition[i]
            val pos2 = mThumbContainerXPosition[i + 1]
            canvas.drawRect(pos, mLeftY, pos2, mRightY,
                    if (i < mCompletedPosition) selectedPaint else paint)
        }

        // Draw rest of circle
        for (i in mThumbContainerXPosition.indices) {
            val pos = mThumbContainerXPosition[i]
            canvas.drawCircle(pos, mCenterY, mCircleRadius,
                    if (i <= mCompletedPosition) selectedPaint else paint)

            if (i == mCompletedPosition) {
                selectedPaint.color = getColorWithAlpha(mThumbColor, 0.2f)
                canvas.drawCircle(pos, mCenterY, mCircleRadius * 1.8f, selectedPaint)
            }
        }
    }

    interface OnDrawListener {
        fun onFinish()
    }

    companion object {

        private val THUMB_SIZE = 100

        private var mNumOfStep = 2
        private var mLineHeight: Float = 0.toFloat()
        private var mThumbRadius: Float = 0.toFloat()
        private var mCircleRadius: Float = 0.toFloat()
        private var mPadding: Float = 0.toFloat()
        private var mThumbColor = Color.RED
        private var mBarColor = Color.GRAY
        private val mStartColor = Color.GREEN

        private var mCenterY: Float = 0.toFloat()
        private var mLeftX: Float = 0.toFloat()
        private var mLeftY: Float = 0.toFloat()
        private var mRightX: Float = 0.toFloat()
        private var mRightY: Float = 0.toFloat()
        private var mDelta: Float = 0.toFloat()

        private var mCompletedPosition: Int = 0

        fun getColorWithAlpha(color: Int, ratio: Float): Int {
            var newColor = 0
            val alpha = Math.round(Color.alpha(color) * ratio)
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            newColor = Color.argb(alpha, r, g, b)
            return newColor
        }
    }
}
