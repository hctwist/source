package com.twisthenry8gmail.graphview

import android.content.Context
import android.graphics.Canvas
import android.util.Range

sealed class GraphElement {

    open fun resolveStyle(context: Context, globalStyle: GlobalGraphStyle) {}

    abstract fun draw(canvas: Canvas, graphBounds: GraphBounds)
}

abstract class DataElement(val data: List<DataPoint>) : GraphElement() {

    class DataPoint(val x: Double, val y: Double)

    class PlotPoint(val x: Float, val y: Float)
}

abstract class AxisElement(internal val isXAxis: Boolean, val range: Range<Double>) :
    GraphElement() {

    abstract fun measureInset(canvas: Canvas): Float
}

fun List<DataElement.DataPoint>.sort(): List<DataElement.DataPoint> {

    return sortedBy { it.x }
}

internal fun List<DataElement.DataPoint>.xRange() =
    if (isEmpty()) null else Range(minBy { it.x }!!.x, maxBy { it.x }!!.x)

internal fun List<DataElement.DataPoint>.yRange() =
    if (isEmpty()) null else Range(minBy { it.y }!!.y, maxBy { it.y }!!.y)