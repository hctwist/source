package com.twisthenry8gmail.graphview

import android.graphics.RectF
import android.util.Range

class GraphBounds {

    var dataXRange = Range(0.0, 0.0)
    var dataYRange = Range(0.0, 0.0)

    // This can be changed in data element draw functions
    val drawArea = RectF()

    fun ensureRangesInclude(points: List<DataElement.DataPoint>) {

        points.xRange()?.let { dataXRange = dataXRange.extend(it) }
        points.yRange()?.let { dataYRange = dataYRange.extend(it) }
    }

    fun mapToDrawArea(dataPoint: DataElement.DataPoint): DataElement.PlotPoint {

        val x = mapToDrawArea(dataPoint.x, dataXRange, drawArea.left, drawArea.right)
        val y = mapToDrawArea(dataPoint.y, dataYRange, drawArea.top, drawArea.bottom)

        return DataElement.PlotPoint(x, y)
    }

    fun mapToDrawArea(
        point: Double,
        range: Range<Double>,
        drawStart: Float,
        drawEnd: Float
    ): Float {

        return (((point - range.lower) / range.size()) * (drawEnd - drawStart) + drawStart).toFloat()
    }

    private fun Range<Double>.size() = upper - lower
}