package com.twisthenry8gmail.graphview

import android.graphics.Canvas
import android.util.Range

sealed class GraphElement {

    abstract fun draw(canvas: Canvas, graphBounds: GraphBounds)
}

abstract class DataElement(val data: List<DataPoint>) : GraphElement() {

    class DataPoint(val x: Double, val y: Double)

    class PlotPoint(val x: Float, val y: Float)
}

abstract class AxesElement(val isXAxes: Boolean): GraphElement() {

    abstract fun measureOffset(canvas: Canvas): Float
}

internal fun List<DataElement.DataPoint>.xRange() =
    if (isEmpty()) null else Range(minBy { it.x }!!.x, maxBy { it.x }!!.x)

internal fun List<DataElement.DataPoint>.yRange() =
    if (isEmpty()) null else Range(minBy { it.y }!!.y, maxBy { it.y }!!.y)