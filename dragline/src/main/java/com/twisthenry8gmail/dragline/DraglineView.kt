package com.twisthenry8gmail.dragline

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import kotlin.math.ceil
import kotlin.math.roundToLong

class DraglineView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var minValue: Long = Long.MIN_VALUE
        set(v) {

            field = v
            value = value.coerceIn(v, maxValue)
            draggedValue = value
            invalidate()
        }
    var maxValue = Long.MAX_VALUE
        set(v) {

            field = v
            value = value.coerceIn(minValue, v)
            draggedValue = value
            invalidate()
        }
    var increment = 1L
        set(v) {

            field = v
            if (value % v != 0L) {

                value = (value - value % v).coerceIn(minValue, maxValue)
                draggedValue = value
                invalidate()
            }
        }

    var value = 0L
        set(value) {

            draggedValue = value
            field = value
            invalidate()
        }

    var valueChangedListener: (Long) -> Unit = {}
    var textFactory: TextFactory = object : TextFactory {

        override fun generateTextFor(value: Long): String {

            return value.toString()
        }
    }

    private var centralMargin = resources.getDimension(R.dimen.dragline_central_margin)
    private var thumbRadius = resources.getDimension(R.dimen.dragline_thumb_radius)
    private var tickHeight = resources.getDimension(R.dimen.dragline_tick_height)
    private var incrementWidth = resources.getDimension(R.dimen.dragline_increment_width)
    private var nTicks = 5

    private var thumbBounceAnimationDuration = 200L
    private var tickSettleAnimationDuration = 100L

    private val textPaint = Paint().apply {

        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private val tickPaint = Paint().apply {

        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val thumbPaint = Paint().apply {

        isAntiAlias = true
    }

    private var dragging = false
    private var bouncingBack = false

    private var draggedValue = 0L
    private var dragStartX = 0F

    private var releasedThumbOffset = 0F
    private var releasedTickOffset = 0F

    private var thumbOffset = 0F
    private var tickOffset = 0F

    private var cx = 0F

    private var thumbBounceAnimator = ValueAnimator.ofFloat(0F, 1F).apply {

        duration = thumbBounceAnimationDuration
        interpolator = OvershootInterpolator()
        addUpdateListener {

            val animatedValue = it.animatedValue as Float

            thumbOffset = releasedThumbOffset * (1 - animatedValue)
            invalidate()
        }
        doOnEnd {

            bouncingBack = false
        }
    }

    private var tickSettleAnimator = ValueAnimator.ofFloat(0F, 1F).apply {

        duration = tickSettleAnimationDuration
        addUpdateListener {

            val animatedValue = it.animatedValue as Float

            tickOffset = releasedTickOffset * (1 - animatedValue)

            invalidate()
        }
    }

    init {

        // Resolve attributes
        context.obtainStyledAttributes(attrs, R.styleable.DraglineView).run {

            if (hasValue(R.styleable.DraglineView_minValue)) {
                minValue = getInt(R.styleable.DraglineView_minValue, 0).toLong()
            }

            if (hasValue(R.styleable.DraglineView_maxValue)) {
                maxValue = getInt(R.styleable.DraglineView_maxValue, 0).toLong()
            }

            if (hasValue(R.styleable.DraglineView_increment)) {
                increment = getInt(R.styleable.DraglineView_increment, 0).toLong()
            }

            if (hasValue(R.styleable.DraglineView_value)) {
                value = getInt(R.styleable.DraglineView_value, 0).toLong()
            }

            setTextSize(
                getDimension(
                    R.styleable.DraglineView_android_textSize,
                    resources.getDimension(R.dimen.dragline_text_size)
                )
            )
            setTextColor(getColor(R.styleable.DraglineView_android_textColor, Color.RED))

            if (hasValue(R.styleable.DraglineView_android_fontFamily)) {
                textPaint.typeface = Typeface.create(
                    getString(R.styleable.DraglineView_android_fontFamily)!!,
                    Typeface.NORMAL
                )
            }

            centralMargin = getDimension(R.styleable.DraglineView_centralMargin, centralMargin)

            tickHeight = getDimension(R.styleable.DraglineView_tickHeight, tickHeight)
            tickPaint.strokeWidth = getDimension(
                R.styleable.DraglineView_tickWidth,
                resources.getDimension(R.dimen.dragline_tick_width)
            )
            tickPaint.color = getColor(R.styleable.DraglineView_tickColor, Color.RED)
            nTicks = getInteger(R.styleable.DraglineView_numberOfTicks, nTicks)

            thumbRadius = getDimension(R.styleable.DraglineView_thumbRadius, thumbRadius)
            thumbPaint.color = getColor(R.styleable.DraglineView_thumbColor, Color.RED)

            recycle()
        }
    }

    fun setAbiBanana(v: Long) {

        value = v
    }

    fun getAbiBanana() = value

    fun setTextSize(size: Float) {

        textPaint.textSize = size
    }

    fun setTextColor(color: Int) {

        textPaint.color = color
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return when (event?.action) {

            MotionEvent.ACTION_DOWN -> {

                if (!bouncingBack) {

                    dragging = true
                    dragStartX = event.x
                    true
                } else {

                    super.onTouchEvent(event)
                }
            }

            MotionEvent.ACTION_MOVE -> {

                val dx = event.x - dragStartX

                thumbOffset = dx

                draggedValue = (value + (dx / incrementWidth).roundToLong() * increment).coerceIn(
                    minValue,
                    maxValue
                )

                val tickOffsetDeterminator = value + (dx / incrementWidth) * increment
                if (tickOffsetDeterminator in minValue.toFloat()..maxValue.toFloat()) {

                    tickOffset = -dx % incrementWidth

                    if (tickOffset > incrementWidth / 2) tickOffset -= incrementWidth
                    if (tickOffset < -incrementWidth / 2) tickOffset += incrementWidth
                } else {

                    tickOffset = 0F
                }

                invalidate()
                true
            }

            MotionEvent.ACTION_UP -> {

                performClick()
                true
            }

            MotionEvent.ACTION_CANCEL -> {

                releaseThumb()
                true
            }

            else -> super.onTouchEvent(event)
        }
    }

    override fun performClick(): Boolean {

        releaseThumb()
        return super.performClick()
    }

    fun releaseThumb() {

        dragging = false
        bouncingBack = true

        releasedThumbOffset = thumbOffset
        thumbBounceAnimator.start()

        releasedTickOffset = tickOffset
        tickSettleAnimator.start()

        if (value != draggedValue) {
            value = draggedValue
            valueChangedListener(value)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        cx = width.toFloat() / 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val textH = -textPaint.fontMetrics.top
        val centralMarginH = centralMargin * 2
        val thumbH = thumbRadius * 2

        val heightSpecSize = textH + centralMarginH + tickHeight + thumbH

        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(ceil(heightSpecSize).toInt(), MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            val textY = -textPaint.fontMetrics.top
            c.drawText(textFactory.generateTextFor(draggedValue), width.toFloat() / 2, textY, textPaint)

            // Ticks
            val tickCy = textY + centralMargin + tickHeight / 2
            val centralTickX = cx + tickOffset

            c.drawLine(
                centralTickX,
                tickCy - tickHeight / 2,
                centralTickX,
                tickCy + tickHeight / 2,
                tickPaint
            )

            // Left
            val leftTickLimit = cx - (nTicks / 2) * incrementWidth - tickPaint.strokeWidth / 2
            var leftTickX = centralTickX - incrementWidth
            while (leftTickX >= leftTickLimit) {

                val r = decayTickHeight(leftTickX, leftTickLimit) / 2
                c.drawLine(leftTickX, tickCy - r, leftTickX, tickCy + r, tickPaint)
                leftTickX -= incrementWidth
            }

            // Right
            val rightTickLimit = cx + (nTicks / 2) * incrementWidth + tickPaint.strokeWidth / 2
            var rightTickX = centralTickX + incrementWidth
            while (rightTickX <= rightTickLimit) {

                val r = decayTickHeight(rightTickX, rightTickLimit) / 2
                c.drawLine(rightTickX, tickCy - r, rightTickX, tickCy + r, tickPaint)
                rightTickX += incrementWidth
            }

            val thumbY = tickCy + tickHeight / 2 + centralMargin + thumbRadius
            c.drawCircle(cx + thumbOffset, thumbY, thumbRadius, thumbPaint)
        }
    }

    private fun decayTickHeight(tickX: Float, limitX: Float): Float {

        val fraction = (cx - tickX) / (cx - limitX)

        return tickHeight * (1 - fraction * fraction)
    }

    interface TextFactory {

        fun generateTextFor(value: Long): String
    }
}