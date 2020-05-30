package com.twisthenry8gmail.graphview

import android.content.Context
import android.graphics.*
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes

class LineElement(data: List<DataPoint>, private val style: Style? = null) : DataElement(data) {

    private val linePaint = Paint().apply {

        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val lineFillPaint = Paint().apply {

        isAntiAlias = true
    }

    private val fill = style?.fillLine ?: false
    private val linePath = Path()
    private val fillPath = Path()

    override fun resolveStyle(context: Context, globalStyle: GlobalGraphStyle) {

        linePaint.strokeWidth = style?.lineWidthRes?.let { context.resources.getDimension(it) }
            ?: globalStyle.plotLineWidth
        linePaint.color = style?.lineColorRes?.let { context.getColor(it) } ?: globalStyle.plotColor
        style?.dashWidthRes?.let {

            val dashWidth = context.resources.getDimension(it.first)
            val spaceWidth = context.resources.getDimension(it.second)
            linePaint.pathEffect = DashPathEffect(floatArrayOf(dashWidth, spaceWidth), 0F)
        }

        lineFillPaint.color =
            style?.lineFillColorRes?.let { context.getColor(it) } ?: globalStyle.plotColorSecondary
    }

    override fun draw(canvas: Canvas, graphBounds: GraphBounds) {

        if (data.size > 1) {

            val dataRect = graphBounds.dataRect

            val firstPoint = graphBounds.mapToDataRect(data.first())

            linePath.reset()
            linePath.moveTo(firstPoint.x, firstPoint.y)

            if (fill) {

                fillPath.reset()
                fillPath.moveTo(dataRect.left, dataRect.bottom)
                fillPath.lineTo(firstPoint.x, firstPoint.y)
            }

            for (i in 1 until data.size) {

                val point = graphBounds.mapToDataRect(data[i])

                linePath.lineTo(point.x, point.y)
                if (fill) fillPath.lineTo(point.x, point.y)
            }

            if (fill) {

                fillPath.lineTo(graphBounds.mapToDataRect(data.last()).x, dataRect.bottom)
                fillPath.close()
                canvas.drawPath(fillPath, lineFillPaint)
            }

            canvas.drawPath(linePath, linePaint)
        }
    }

    class Style {

        @DimenRes
        var lineWidthRes: Int? = null

        @ColorRes
        var lineColorRes: Int? = null

        var dashWidthRes: Pair<Int, Int>? = null

        var fillLine: Boolean? = null

        @ColorRes
        var lineFillColorRes: Int? = null
    }
}