package com.twisthenry8gmail.dragline

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import kotlin.math.*

class SeekableTextView(context: Context, attrs: AttributeSet) :
    View(context, attrs) {

    var value = 3L

    var minValue = 1L
    var maxValue = 10L
    var increment = 1L

    var valueChangedListener: (Long) -> Unit = {}
    var textFactory: (Long) -> String = { it.toString() }

    var incrementHeight = 100F
        set(value) {

            field = value
            invalidate()
        }
    var thumbRadius = 20F
        set(value) {

            field = value
            invalidate()
        }
    var lineWidth = 10F
        set(value) {

            field = value
            linePaint.strokeWidth = lineWidth
        }

    private var dragAllowanceMultiple = 0F
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

        strokeCap = Paint.Cap.ROUND
    }

    private val thumbPaint = Paint().apply {

        isAntiAlias = true
    }

    private val textBounds = Rect()
    private val textPaint = Paint().apply {

        isAntiAlias = true
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    init {

        dragAllowanceMultiple = 2F

        context.obtainStyledAttributes(attrs, R.styleable.SeekableTextView).run {

            incrementHeight = getDimension(R.styleable.SeekableTextView_incrementHeight, 100F)

            lineWidth = getDimension(R.styleable.SeekableTextView_seekLineWidth, 10F)
            linePaint.color = getColor(R.styleable.SeekableTextView_seekLineColor, Color.BLACK)

            thumbRadius = getDimension(R.styleable.SeekableTextView_seekThumbRadius, 20F)
            thumbPaint.color = getColor(R.styleable.SeekableTextView_seekThumbColor, Color.BLACK)

            textPaint.textSize = getDimension(R.styleable.SeekableTextView_android_textSize, 20F)
            textPaint.color = getColor(R.styleable.SeekableTextView_android_textColor, Color.BLACK)

            recycle()
        }

        setOnTouchListener { _, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {

                    val thumbBottom = height.toFloat() / 2 + thumbRadius
                    val thumbTop = height.toFloat() / 2 - thumbRadius

                    val thumbLeft = width.toFloat() - thumbRadius * 2

                    if (!bouncingBack && event.y in thumbTop..thumbBottom && event.x in thumbLeft..width.toFloat()) {

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

    private fun moveDecay(x: Float): Float {

        return min(dragDecayFactor * (1 - exp(-0.5F * x)), 1F)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val w = MeasureSpec.getSize(widthMeasureSpec)

        textPaint.getTextBounds(textFactory(value), 0, textFactory(value).length, textBounds)
        val h = textBounds.height()

        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            val lineRadius = linePaint.strokeWidth / 2
            val lineCx = width - thumbRadius
            c.drawLine(
                lineCx,
                lineRadius,
                lineCx,
                height.toFloat() - lineRadius,
                linePaint
            )

            val drawThumbY = if (dragging || bouncingBack) thumbY else height.toFloat() / 2
            c.drawCircle(lineCx, drawThumbY, thumbRadius, thumbPaint)

            c.drawText(textFactory(value), 0F, -textBounds.top.toFloat(), textPaint)
        }
    }
}