package com.twisthenry8gmail.progresscircles

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.PathInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import kotlin.math.min
import kotlin.math.roundToLong

class ProgressCircleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val backingArcPaint = Paint().apply {

        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = resources.getDimension(R.dimen.goal_progress_stroke_width)
    }

    private val filledBackingArcPaint = Paint().apply {

        isAntiAlias = true
    }

    private val arcPaint = Paint().apply {

        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    var gradientColors: IntArray? = null
        set(value) {

            field = value
            updateGradient()
        }

    var filledBackingArc = false
        set(value) {

            field = value
            invalidate()
        }

    var target = 10L
        set(value) {

            field = value
            updateProgress()
        }

    private var progress = 0L

    private var animationProgress = 0.0

    private val arcRect = RectF()

    private var animator: ValueAnimator? = null

    init {

        context.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressCircleView,
            R.attr.progressCircleStyle,
            0
        ).run {

            filledBackingArc = getBoolean(R.styleable.ProgressCircleView_filledBackingArc, false)

            setStrokeWidth(
                getDimension(
                    R.styleable.ProgressCircleView_strokeWidth,
                    resources.getDimension(R.dimen.goal_progress_stroke_width)
                )
            )

            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        when {
            widthMode == MeasureSpec.UNSPECIFIED -> {

                when (heightMode) {

                    MeasureSpec.UNSPECIFIED -> return super.onMeasure(
                        widthMeasureSpec,
                        heightMeasureSpec
                    )
                    MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> setMeasuredDimension(
                        heightSize,
                        heightSize
                    )
                }
            }
            heightMode == MeasureSpec.UNSPECIFIED -> {

                when (widthMode) {

                    MeasureSpec.UNSPECIFIED -> return super.onMeasure(
                        widthMeasureSpec,
                        heightMeasureSpec
                    )
                    MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> setMeasuredDimension(
                        widthSize,
                        widthSize
                    )
                }
            }
            else -> {

                val s = min(widthSize, heightSize)
                setMeasuredDimension(s, s)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        updateArcAllocations(w)
        updateGradient()
    }

    private fun updateArcAllocations(s: Int) {

        val offset = arcPaint.strokeWidth / 2
        arcRect.set(offset, offset, s - offset, s - offset)
    }

    private fun updateGradient() {

        gradientColors?.let { gradientColors ->

            val matrix = Matrix()

            val cx = width.toFloat() / 2
            val cy = height.toFloat() / 2

            val gradient = SweepGradient(
                cx,
                cy,
                gradientColors,
                FloatArray(gradientColors.size) {

                    it.toFloat() / (gradientColors.size - 1)
                })

            gradient.getLocalMatrix(matrix)
            matrix.postRotate(-90F, cx, cy)
            gradient.setLocalMatrix(matrix)

            arcPaint.shader = gradient
        }
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            if (filledBackingArc) {

                c.drawCircle(
                    arcRect.centerX(),
                    arcRect.centerY(),
                    height.toFloat() / 2,
                    filledBackingArcPaint
                )
            } else {

                c.drawArc(arcRect, -90F, 360F, false, backingArcPaint)
            }
            c.drawArc(arcRect, -90F, getSweepAngle(animationProgress), false, arcPaint)
        }
    }

    fun setBackingArcColor(color: Int) {

        backingArcPaint.color = color
        filledBackingArcPaint.color = color
        invalidate()
    }

    fun setStrokeWidth(strokeWidth: Float) {

        arcPaint.strokeWidth = strokeWidth
        backingArcPaint.strokeWidth = strokeWidth
        invalidate()
    }

    fun setColor(color: Int) {

        arcPaint.color = color
        invalidate()
    }

    fun setProgress(progress: Long, animate: Boolean = false, animationCallback: () -> Unit = {}) {

        this.progress = progress
        if (animate) {
            animateProgress(animationCallback)
        } else {
            updateProgress()
            animationCallback()
        }
    }

    private fun updateProgress() {

        animator?.cancel()
        animationProgress = progress.toDouble()
        invalidate()
    }

    private fun animateProgress(callback: () -> Unit) {

        animator?.cancel()

        val totalDuration = 2000

        animator = ValueAnimator.ofFloat(animationProgress.toFloat(), progress.toFloat()).apply {

            interpolator = chooseInterpolator()
            duration =
                (totalDuration * ((progress - animationProgress) / target)).roundToLong()
            addUpdateListener {

                animationProgress = (it.animatedValue as Float).toDouble()
                invalidate()
            }

            doOnEnd {

                callback()
            }

            start()
        }
    }

    private fun chooseInterpolator(): Interpolator {

        return if (progress >= target) {
            PathInterpolator(0.6F, 0F, 0.4F, 1F)
        } else {
            PathInterpolator(0.8F, 0F, 0.2F, 1.4F)
        }
    }

    private fun getSweepAngle(p: Double): Float {

        return ((p / target) * 360).toFloat()
    }
}