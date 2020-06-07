package com.twisthenry8gmail.graphview

import android.content.Context
import android.graphics.*
import androidx.annotation.DimenRes

class LineElement(data: List<DataPoint>, private val style: Style? = null) : SeriesElement(data) {

    private val linePaint = Paint().apply {

        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val lineFillPaint = Paint().apply {

        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
    }

    private val fill = style?.fillLine ?: false
    private val linePath = Path()
    private val fillPath = Path()

    override fun resolveStyle(context: Context, globalStyle: GlobalGraphStyle) {

        linePaint.strokeWidth = style?.lineWidthRes?.let { context.resources.getDimension(it) }
            ?: globalStyle.plotLineWidth
        linePaint.color = style?.lineColor?.resolve(context) ?: globalStyle.plotColor
        style?.dashWidthRes?.let {

            val dashWidth = context.resources.getDimension(it.first)
            val spaceWidth = context.resources.getDimension(it.second)
            linePaint.pathEffect = DashPathEffect(floatArrayOf(dashWidth, spaceWidth), 0F)
        }

        lineFillPaint.color =
            style?.lineFillColor?.resolve(context) ?: globalStyle.plotColorSecondary
        lineFillPaint.strokeWidth = linePaint.strokeWidth
    }

    override fun resolveBounds(graphBounds: GraphBounds) {

        graphBounds.plotRect.apply {

            val strokeWidth = linePaint.strokeWidth / 2
            left += strokeWidth
            right -= strokeWidth
            bottom -= strokeWidth
        }
    }

    override fun draw(canvas: Canvas, graphBounds: GraphBounds) {

        if (data.size > 1) {

            val plotRect = graphBounds.plotRect

            val firstPoint = graphBounds.mapToPlotRect(data.first())

            linePath.reset()
            linePath.moveTo(firstPoint.x, firstPoint.y)

            if (fill) {

                fillPath.reset()
                fillPath.moveTo(plotRect.left, plotRect.bottom)
                fillPath.lineTo(firstPoint.x, firstPoint.y)
            }

            for (i in 1 until data.size) {

                val point = graphBounds.mapToPlotRect(data[i])

                linePath.lineTo(point.x, point.y)
                if (fill) fillPath.lineTo(point.x, point.y)
            }

            if (fill) {

                fillPath.lineTo(graphBounds.mapToPlotRect(data.last()).x, plotRect.bottom)
                fillPath.close()
                canvas.drawPath(fillPath, lineFillPaint)
            }

            canvas.drawPath(linePath, linePaint)
        }
    }

    class Style {

        @DimenRes
        var lineWidthRes: Int? = null

        var lineColor = ColorResolver()

        var dashWidthRes: Pair<Int, Int>? = null

        var fillLine: Boolean? = null

        var lineFillColor = ColorResolver()
    }
}