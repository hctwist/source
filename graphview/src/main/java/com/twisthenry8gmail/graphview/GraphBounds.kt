package com.twisthenry8gmail.graphview

import android.graphics.RectF
import android.util.Range

class GraphBounds {

    var dataXRange: Range<Double>? = null
    var dataYRange: Range<Double>? = null

    val xAxisRect = RectF()
    val yAxisRect = RectF()
    val dataRect = RectF()

    fun requireDataXRange() = dataXRange!!

    fun requireDataYRange() = dataYRange!!

    fun ensureRangesInclude(points: List<DataElement.DataPoint>) {

        points.xRange()?.let { ensureXRangeIncludes(it) }
        points.yRange()?.let { ensureYRangeIncludes(it) }
    }

    fun ensureXRangeIncludes(range: Range<Double>) {

        dataXRange = dataXRange?.extend(range) ?: range
    }

    fun ensureYRangeIncludes(range: Range<Double>) {

        dataYRange = dataYRange?.extend(range) ?: range
    }

    fun mapToDataRect(dataPoint: DataElement.DataPoint): DataElement.PlotPoint {

        val x = mapToRect(dataPoint.x, requireDataXRange(), dataRect.left, dataRect.right)
        val y = mapToRect(dataPoint.y, requireDataYRange(), dataRect.top, dataRect.bottom)

        return DataElement.PlotPoint(x, y)
    }

    fun mapToRect(
        point: Double,
        range: Range<Double>,
        rectStart: Float,
        rectEnd: Float
    ): Float {

        return (((point - range.lower) / range.size()) * (rectEnd - rectStart) + rectStart).toFloat()
    }

    private fun Range<Double>.size() = upper - lower
}