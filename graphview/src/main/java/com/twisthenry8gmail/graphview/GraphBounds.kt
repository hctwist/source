package com.twisthenry8gmail.graphview

import android.graphics.RectF
import android.util.Range

class GraphBounds {

    var dataXRange: Range<Double>? = null
    var dataYRange: Range<Double>? = null

    var width = 0F
    var height = 0F
    val plotRect = RectF()

    fun resetPlotRect() {

        plotRect.set(0F, 0F, width, height)
    }

    fun requireDataXRange() = dataXRange!!

    fun requireDataYRange() = dataYRange!!

    fun ensureRangesInclude(point: DataPoint) {

        ensureXRangeIncludes(point.x)
        ensureYRangeIncludes(point.y)
    }

    fun ensureRangesInclude(points: List<DataPoint>) {

        points.xRange()?.let { ensureXRangeIncludes(it) }
        points.yRange()?.let { ensureYRangeIncludes(it) }
    }

    private fun ensureXRangeIncludes(point: Double) {

        dataXRange = dataXRange?.extend(point) ?: Range(point, point)
    }

    private fun ensureYRangeIncludes(point: Double) {

        dataYRange = dataYRange?.extend(point) ?: Range(point, point)
    }

    fun ensureXRangeIncludes(range: Range<Double>) {

        dataXRange = dataXRange?.extend(range) ?: range
    }

    fun ensureYRangeIncludes(range: Range<Double>) {

        dataYRange = dataYRange?.extend(range) ?: range
    }

    fun mapToPlotRect(seriesPoint: DataPoint): PlotPoint {

        val x = mapToRect(seriesPoint.x, requireDataXRange(), plotRect.left, plotRect.right)
        val y = mapToRect(
            seriesPoint.y,
            requireDataYRange(),
            plotRect.bottom,
            plotRect.top
        )

        return PlotPoint(x, y)
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