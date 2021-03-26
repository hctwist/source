package uk.henrytwist.progresscircles

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.PathInterpolator
import androidx.core.animation.doOnEnd
import com.twisthenry8gmail.progresscircles.R
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
        set(value) {

            field = value
            invalidate()
        }

    private var progress: Long? = null

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
                    R.styleable.ProgressView_progressStrokeWidth,
                    resources.getDimension(R.dimen.progress_circle_stroke_width)
                )
            )

            setBackingColor(getColor(R.styleable.ProgressView_progressBackingColor, Color.WHITE))
            setColor(getColor(R.styleable.ProgressView_progressColor, Color.BLACK))

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

    fun setProgress(progress: Long?) {

        if (progress != null) setProgress(progress, true)
    }

    fun setProgress(progress: Long, animate: Boolean = false, animationCallback: () -> Unit = {}) {

        val shouldAnimate = animate && this.progress != null

        this.progress = progress
        if (shouldAnimate) {
            animateProgress(animationCallback)
        } else {
            updateProgress()
            animationCallback()
        }
    }

    private fun resolveProgress(): Long = progress ?: 0L

    private fun updateProgress() {

        animator?.cancel()
        animationProgress = resolveProgress().toDouble()
        invalidate()
    }

    private fun animateProgress(callback: () -> Unit) {

        animator?.cancel()

        val totalDuration = 2000

        animator =
            ValueAnimator.ofFloat(animationProgress.toFloat(), resolveProgress().toFloat()).apply {

                interpolator = chooseInterpolator()
                duration =
                    (totalDuration * ((abs(resolveProgress() - animationProgress)) / target)).roundToLong()
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

        return if (resolveProgress() >= target) {
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