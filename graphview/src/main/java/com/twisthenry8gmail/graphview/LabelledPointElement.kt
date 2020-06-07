package com.twisthenry8gmail.graphview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.max

class LabelledPointElement(
    point: DataPoint,
    private val label: String?,
    private val style: Style? = null
) : PointElement(point) {

    private val pointPaint = Paint().apply {

        isAntiAlias = true
    }

    private val labelPaint = Paint().apply {

        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    var pointRadius = 0F
    var labelMargin = 0F

    private val pointRectAlloc = RectF()

    override fun resolveStyle(context: Context, globalStyle: GlobalGraphStyle) {

        pointPaint.color = style?.color?.resolve(context) ?: globalStyle.plotColor

        labelPaint.color = globalStyle.textColor
        labelPaint.textSize = globalStyle.textSize

        pointRadius = globalStyle.plotPointRadius

        labelMargin = if (label == null) 0F else globalStyle.defaultMargin
    }

    override fun resolveBounds(
        graphBounds: GraphBounds
    ) {

        val labelWidth = label?.let { labelPaint.measureText(it) } ?: 0F
        val xOffset = max(pointRadius, labelWidth / 2)

        val labelMaxHeight = labelPaint.descent() - labelPaint.ascent()
        val topOffset = pointRadius + labelMargin + labelMaxHeight

        val plotPoint = graphBounds.mapToPlotRect(point)

        pointRectAlloc.set(
            plotPoint.x - xOffset,
            plotPoint.y - topOffset,
            plotPoint.x + xOffset,
            plotPoint.y + pointRadius
        )

        graphBounds.plotRect.pushBy(pointRectAlloc)
    }

    override fun draw(canvas: Canvas, graphBounds: GraphBounds) {

        val plotPoint = graphBounds.mapToPlotRect(point)

        canvas.drawCircle(plotPoint.x, plotPoint.y, pointRadius, pointPaint)

        label?.let {

            canvas.drawText(
                it,
                plotPoint.x,
                plotPoint.y - pointRadius - labelMargin - labelPaint.descent(),
                labelPaint
            )
        }
    }

    class Style {

        val color = ColorResolver()
    }
}