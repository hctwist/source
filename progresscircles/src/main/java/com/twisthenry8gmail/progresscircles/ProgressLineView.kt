package com.twisthenry8gmail.progresscircles

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import kotlin.math.ceil

class ProgressLineView(context: Context, attrs: AttributeSet) : ProgressView(context, attrs) {

    init {

        backingPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val height = ceil(foregroundPaint.strokeWidth).toInt()
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            val offset = backingPaint.strokeWidth / 2

            val y = height.toFloat() / 2
            val w = width.toFloat()

            c.drawLine(offset, y, w - offset, y, backingPaint)

            if (animationProgress > 0) {

                val progressX = offset + (w - 2 * offset) * (animationProgress / target)
                c.drawLine(offset, y, progressX.toFloat(), y, foregroundPaint)
            }
        }
    }
}