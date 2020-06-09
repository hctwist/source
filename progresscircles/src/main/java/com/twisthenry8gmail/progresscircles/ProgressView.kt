package com.twisthenry8gmail.progresscircles

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.PathInterpolator
import androidx.core.animation.doOnEnd
import kotlin.math.abs
import kotlin.math.roundToLong

abstract class ProgressView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    protected val backingPaint = Paint().apply {

        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = resources.getDimension(R.dimen.progress_circle_stroke_width)
    }

    protected val foregroundPaint = Paint().apply {

        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    var target = 10L

    private var progress = 0L

    protected var animationProgress = 0.0

    private var animator: ValueAnimator? = null

    init {

        context.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressView,
            R.attr.progressCircleStyle,
            0
        ).run {

            setStrokeWidth(
                getDimension(
                    R.styleable.ProgressView_strokeWidth,
                    resources.getDimension(R.dimen.progress_circle_stroke_width)
                )
            )

            recycle()
        }
    }

    fun setBackingColor(color: Int) {

        backingPaint.color = color
        invalidate()
    }

    fun setStrokeWidth(strokeWidth: Float) {

        foregroundPaint.strokeWidth = strokeWidth
        backingPaint.strokeWidth = strokeWidth
        invalidate()
    }

    fun setColor(color: Int) {

        foregroundPaint.color = color
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
                (totalDuration * ((abs(progress - animationProgress)) / target)).roundToLong()
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

    override fun onDetachedFromWindow() {

        animator?.removeAllListeners()
        animator?.cancel()
        super.onDetachedFromWindow()
    }
}