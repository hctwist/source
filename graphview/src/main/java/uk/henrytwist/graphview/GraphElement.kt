package uk.henrytwist.graphview

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.Range

sealed class GraphElement {

    open fun resolveStyle(context: Context, globalStyle: GlobalGraphStyle) {}

    // TODO Rethink system to make only max insets get applied, cumulative at the moment even though some elements draw on top of eachother
    open fun resolveBounds(graphBounds: GraphBounds) {}

    abstract fun draw(canvas: Canvas, graphBounds: GraphBounds)
}

abstract class SeriesElement(val data: List<DataPoint>) : GraphElement()

abstract class PointElement(val point: DataPoint) : GraphElement()

abstract class AxisElement(internal val isXAxis: Boolean, val range: Range<Double>) :
    GraphElement()

class DataPoint(val x: Double, val y: Double)

class PlotPoint(val x: Float, val y: Float)

fun List<DataPoint>.sort(): List<DataPoint> {

    return sortedBy { it.x }
}

internal fun List<DataPoint>.xRange() =
    if (isEmpty()) null else Range(minBy { it.x }!!.x, maxBy { it.x }!!.x)

internal fun List<DataPoint>.yRange() =
    if (isEmpty()) null else Range(minBy { it.y }!!.y, maxBy { it.y }!!.y)

internal fun RectF.pushBy(other: RectF) {

    if (top - other.top > 0) {

        top += top - other.top
    }

    if (left - other.left > 0) {

        left += left - other.left
    }

    if (right - other.right < 0) {

        right += right - other.right
    }

    if (bottom - other.bottom < 0) {

        bottom += bottom - other.bottom
    }
}