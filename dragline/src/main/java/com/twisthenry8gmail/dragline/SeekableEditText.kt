package com.twisthenry8gmail.dragline

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.core.animation.doOnEnd
import kotlinx.android.synthetic.main.seekable_text_view.view.*
import kotlin.math.*

class SeekableEditText(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {

    var textFactory: (Long) -> String = { it.toString() }
        set(value) {
            field = value
            seekable_text_view_text.setText(textFactory(seekable_text_view_seekbar.value))
        }

    private val textView: TextView
    private val seekbarView: View

    init {

        inflate(context, R.layout.seekable_text_view, this)

        textView = getChildAt(0) as TextView
        seekbarView = getChildAt(1)

        seekable_text_view_seekbar.valueChangedListener = {

            seekable_text_view_text.setText(it.toString())
        }

//        context.obtainStyledAttributes(attrs, R.styleable.SeekableTextView).run {
//
//            // TODO Default style
//            setIncrementHeight(getDimension(R.styleable.SeekableTextView_incrementHeight, 100F))
//            setThumbRadius(getDimension(R.styleable.SeekableTextView_seekThumbRadius, 20F))
//            setLineWidth(getDimension(R.styleable.SeekableTextView_seekLineWidth, 10F))
//            setLineColor(getColor(R.styleable.SeekableTextView_seekLineColor, Color.BLACK))
//            setThumbColor(getColor(R.styleable.SeekableTextView_seekThumbColor, Color.BLACK))
//            setTextSize(getDimension(R.styleable.SeekableTextView_android_textSize, 20F))
//            setTextColor(getColor(R.styleable.SeekableTextView_android_textColor, Color.BLACK))
//
//            recycle()
//        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        measureChild(textView, widthMeasureSpec, heightMeasureSpec)

        val bottom = textView.paint.fontMetrics.bottom
        val height = bottom + textView.paint.fontMetrics.ascent


    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        val textView = getChildAt(0)

        val seekBar = getChildAt(1)
    }

    fun setValue(value: Long) {

        seekable_text_view_text.setText(textFactory(value))
        seekable_text_view_seekbar.value = value
    }

    fun setMinValue(value: Long) {

        seekable_text_view_seekbar.minValue = value
    }

    fun setMaxValue(value: Long) {

        seekable_text_view_seekbar.maxValue = value
    }

    fun setIncrement(value: Long) {

        seekable_text_view_seekbar.increment = value
    }

    fun setIncrementHeight(height: Float) {

        seekable_text_view_seekbar.incrementHeight = height
    }

    fun setThumbRadius(radius: Float) {

        seekable_text_view_seekbar.thumbRadius = radius
    }

    fun setLineWidth(width: Float) {

        seekable_text_view_seekbar.lineWidth = width
    }

    fun setLineColor(color: Int) {

        seekable_text_view_seekbar.setLineColor(color)
    }

    fun setTextSize(size: Float) {

        seekable_text_view_text.textSize = size
    }

    fun setThumbColor(color: Int) {

        seekable_text_view_seekbar.setThumbColor(color)
    }

    fun setTextColor(color: Int) {

        seekable_text_view_text.setTextColor(color)
    }
}

private class InfiniteVerticalSeekbar(context: Context, attrs: AttributeSet) :
    View(context, attrs) {

    var value = 1L

    var minValue = 1L
    var maxValue = 10L
    var increment = 1L

    var valueChangedListener: (Long) -> Unit = {}

    var incrementHeight = 100F
    var thumbRadius = 20F
    var lineWidth = 10F
        set(value) {

            field = value
            linePaint.strokeWidth = lineWidth
        }

    var dragAllowanceMultiple = 0F
        set(value) {

            field = value
            dragDecayFactor = 1F / (1F - exp(-0.5F * value))
        }
    private var dragDecayFactor = 0F

    private var thumbY = 0F

    private var preDragValue = 0L

    private var dragging = false
    private var bouncingBack = false

    private val linePaint = Paint().apply {

        color = Color.LTGRAY
        strokeCap = Paint.Cap.ROUND
    }

    private val thumbPaint = Paint().apply {

        isAntiAlias = true
        color = Color.BLUE
    }

    init {

        dragAllowanceMultiple = 2F

        linePaint.strokeWidth = lineWidth

        setOnTouchListener { _, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {

                    val thumbBottom = height.toFloat() / 2 + thumbRadius
                    val thumbTop = height.toFloat() / 2 - thumbRadius
                    if (!bouncingBack && event.y in thumbTop..thumbBottom) {

                        preDragValue = value
                        dragging = true
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {

                    if (dragging) {

                        val availableArea = height.toFloat() / 2 - thumbRadius
                        val yMove = event.y - height.toFloat() / 2
                        val decayedMove =
                            moveDecay(abs(yMove / availableArea)) * availableArea * sign(yMove)

                        thumbY = height.toFloat() / 2 + decayedMove

                        val incrementsMoved = (decayedMove / incrementHeight).roundToLong()
                        value = (preDragValue + incrementsMoved * increment).coerceIn(
                            minValue,
                            maxValue
                        )
                        valueChangedListener(value)

                        invalidate()
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {

                    dragging = false
                    bouncingBack = true

                    ValueAnimator.ofFloat(thumbY, height.toFloat() / 2).apply {

                        duration = 200
                        interpolator = OvershootInterpolator()
                        addUpdateListener {

                            val animatedValue = it.animatedValue as Float

                            thumbY = animatedValue
                            invalidate()
                        }
                        doOnEnd {

                            bouncingBack = false
                        }

                        start()
                    }

                    true
                }

                else -> false
            }
        }
    }

    override fun getBaseline(): Int {
        return height
    }

    fun setLineColor(color: Int) {

        linePaint.color = color
        invalidate()
    }

    fun setThumbColor(color: Int) {

        thumbPaint.color = color
        invalidate()
    }

    private fun moveDecay(x: Float): Float {

        return min(dragDecayFactor * (1 - exp(-0.5F * x)), 1F)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthSpec =
            MeasureSpec.makeMeasureSpec(ceil(thumbRadius * 2).toInt(), MeasureSpec.EXACTLY)

        super.onMeasure(widthSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            val centreX = width.toFloat() / 2

            val capRadius = linePaint.strokeWidth / 2
            c.drawLine(
                centreX,
                capRadius,
                width.toFloat() / 2,
                height.toFloat() - capRadius,
                linePaint
            )

            val drawThumbY = if (dragging || bouncingBack) thumbY else height.toFloat() / 2
            c.drawCircle(centreX, drawThumbY, thumbRadius, thumbPaint)
        }
    }
}