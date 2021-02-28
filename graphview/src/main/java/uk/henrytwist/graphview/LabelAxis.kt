package uk.henrytwist.graphview

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

    protected val axisPaint = Paint()

    protected val labelPaint = Paint().apply {

        isAntiAlias = true
    }

    protected var margin = 0F

    override fun resolveStyle(context: Context, globalStyle: GlobalGraphStyle) {

        axisPaint.color = globalStyle.axisColor
        axisPaint.strokeWidth = globalStyle.axisWidth

        labelPaint.color =
            style?.textColorRes?.let { context.getColor(it) } ?: globalStyle.textColor
        labelPaint.textSize = globalStyle.textSize

        margin = globalStyle.defaultMargin
    }

    class Style {

        @ColorRes
        var textColorRes: Int? = null
    }

    class X(range: Range<Double>, labels: List<Label>, style: Style? = null) :
        LabelAxis(true, range, labels, style) {

        init {

            labelPaint.textAlign = Paint.Align.CENTER
        }

        override fun resolveBounds(
            graphBounds: GraphBounds
        ) {

            graphBounds.apply {

                plotRect.bottom -= labelPaint.descent() - labelPaint.ascent() + margin

                // TODO Only do this if text is going outside the view - might be fixed when 'max' overhaul happens, see GraphElement comment
                plotRect.left += labelPaint.measureText(labels.first().label) / 2
                plotRect.right -= labelPaint.measureText(labels.last().label) / 2
            }
        }

        override fun draw(canvas: Canvas, graphBounds: GraphBounds) {

            val plotRect = graphBounds.plotRect

            canvas.drawLine(
                plotRect.left,
                plotRect.bottom,
                plotRect.right,
                plotRect.bottom,
                axisPaint
            )

            labels.forEach {

                val x = graphBounds.mapToRect(
                    it.point,
                    graphBounds.requireDataXRange(),
                    plotRect.left,
                    plotRect.right
                )

                canvas.drawText(
                    it.label,
                    x,
                    graphBounds.height - labelPaint.descent(),
                    labelPaint
                )
            }
        }
    }

    class Y(
        range: Range<Double>,
        labels: List<Label>,
        style: Style? = null
    ) :
        LabelAxis(false, range, labels, style) {

        private val textBoundsAlloc = Rect()

        init {

            labelPaint.textAlign = Paint.Align.RIGHT
        }

        override fun resolveBounds(graphBounds: GraphBounds) {

            if (labels.isNotEmpty()) {
                graphBounds.apply {

                    // TODO This is happening for X as well, need max system
                    plotRect.left += (labels.map { labelPaint.measureText(it.label) }.max()
                        ?: 0F) + margin

                    val topLabel = labels.last().label
                    labelPaint.getTextBounds(topLabel, 0, topLabel.length, textBoundsAlloc)
                    plotRect.top += textBoundsAlloc.height().toFloat() / 2
                }
            }
        }

        override fun draw(canvas: Canvas, graphBounds: GraphBounds) {

            val plotRect = graphBounds.plotRect

            canvas.drawLine(
                plotRect.left,
                plotRect.bottom,
                plotRect.left,
                plotRect.top,
                axisPaint
            )

            labels.forEach {

                val y = graphBounds.mapToRect(
                    it.point,
                    graphBounds.requireDataYRange(),
                    plotRect.bottom,
                    plotRect.top
                )

                canvas.drawText(
                    it.label,
                    plotRect.left - margin,
                    y + textBoundsAlloc.height().toFloat() / 2,
                    labelPaint
                )
            }
        }
    }

    class Label(val point: Double, val label: String)
}