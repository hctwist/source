package uk.henrytwist.progresscircles

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import kotlin.math.min

class ProgressCircleView(context: Context, attrs: AttributeSet?) : ProgressView(context, attrs) {

    var gradientColors: IntArray? = null
        set(value) {

            field = value
            updateGradient()
        }

    private val arcRect = RectF()

    private fun updateArcAllocations(s: Int) {

        val offset = foregroundPaint.strokeWidth / 2
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

            foregroundPaint.shader = gradient
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        updateArcAllocations(w)
        updateGradient()
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

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            c.drawArc(arcRect, -90F, 360F, false, backingPaint)

            if (target > 0) {

                c.drawArc(arcRect, -90F, getSweepAngle(animationProgress), false, foregroundPaint)
            }
        }
    }

    private fun getSweepAngle(p: Double): Float {

        return ((p / target) * 360).toFloat()
    }
}