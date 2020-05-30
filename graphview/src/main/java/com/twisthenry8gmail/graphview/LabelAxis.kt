package com.twisthenry8gmail.graphview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Range
import androidx.annotation.ColorRes

abstract class LabelAxis(
    isXAxis: Boolean,
    range: Range<Double>,
    val labels: List<Label>,
    val style: Style? = null
) : AxisElement(isXAxis, range) {

    protected val textPaint = Paint().apply {

        isAntiAlias = true
    }

    protected val axisPaint = Paint()

    protected var margin = 0F

    override fun resolveStyle(context: Context, globalStyle: GlobalGraphStyle) {

        textPaint.color =
            style?.textColorRes?.let { context.getColor(it) } ?: globalStyle.axisTextColor
        textPaint.textSize = globalStyle.axisTextSize

        margin = globalStyle.defaultMargin

        axisPaint.strokeWidth = globalStyle.axisWidth
        axisPaint.color = globalStyle.axisColor
    }

    class Style {

        @ColorRes
        var textColorRes: Int? = null
    }

    class X(range: Range<Double>, labels: List<Label>, style: Style? = null) :
        LabelAxis(true, range, labels, style) {

        init {

            textPaint.textAlign = Paint.Align.CENTER
        }

        override fun measureInset(canvas: Canvas): Float {

            // TODO Use real text bounds somehow
            return -textPaint.ascent() + margin
        }

        override fun draw(canvas: Canvas, graphBounds: GraphBounds) {

            val leftInset = textPaint.measureText(labels.first().label) / 2
            val rightInset = textPaint.measureText(labels.last().label) / 2

            graphBounds.xAxisRect.left += leftInset
            graphBounds.xAxisRect.right -= rightInset
            graphBounds.dataRect.left += leftInset
            graphBounds.dataRect.right -= rightInset

            labels.forEach {

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

    class Y(range: Range<Double>, labels: List<Label>, style: Style? = null) :
        LabelAxis(false, range, labels, style) {

        private val textBoundsAlloc = Rect()

        override fun measureInset(canvas: Canvas): Float {

            return (labels.map { textPaint.measureText(it.label) }.max() ?: 0F) + margin
        }

        override fun draw(canvas: Canvas, graphBounds: GraphBounds) {

            val topLabel = labels.last().label
            textPaint.getTextBounds(topLabel, 0, topLabel.length, textBoundsAlloc)
            val topInset = textBoundsAlloc.height() / 2

            graphBounds.yAxisRect.top += topInset
            graphBounds.dataRect.top += topInset

            labels.forEach {

                val y = graphBounds.mapToRect(
                    it.point,
                    graphBounds.requireDataYRange(),
                    graphBounds.yAxisRect.bottom,
                    graphBounds.yAxisRect.top
                )

                canvas.drawText(it.label, graphBounds.yAxisRect.left, y + topInset, textPaint)
            }
        }
    }

    class Label(val point: Double, val label: String)
}