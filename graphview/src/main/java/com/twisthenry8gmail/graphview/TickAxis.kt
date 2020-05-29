package com.twisthenry8gmail.graphview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Range

abstract class TickAxis(
    isXAxis: Boolean,
    range: Range<Double>,
    val ticks: List<Tick>
) : AxisElement(isXAxis, range) {

    val textPaint = Paint().apply {

        textSize = 20F
        color = Color.GREEN
        textAlign = Paint.Align.CENTER
    }

    class X(range: Range<Double>, ticks: List<Tick>) :
        TickAxis(true, range, ticks) {

        override fun measureOffset(canvas: Canvas): Float {

            return -textPaint.ascent()
        }

        override fun draw(canvas: Canvas, graphBounds: GraphBounds) {

            val leftInset = textPaint.measureText(ticks.first().label) / 2
            val rightInset = textPaint.measureText(ticks.last().label) / 2

            graphBounds.xAxisRect.left += leftInset
            graphBounds.xAxisRect.right -= rightInset
            graphBounds.dataRect.left += leftInset
            graphBounds.dataRect.right -= rightInset

            ticks.forEach {

                val x = graphBounds.mapToRect(
                    it.point,
                    graphBounds.requireDataXRange(),
                    graphBounds.xAxisRect.left,
                    graphBounds.xAxisRect.right
                )

                canvas.drawText(it.label, x, graphBounds.xAxisRect.bottom, textPaint)
            }
        }
    }

    class Y(range: Range<Double>, ticks: List<Tick>) :
        TickAxis(false, range, ticks) {

        override fun measureOffset(canvas: Canvas): Float {

            return 0F
        }

        override fun draw(canvas: Canvas, graphBounds: GraphBounds) {


        }
    }

    class Tick(val point: Double, val label: String)
}