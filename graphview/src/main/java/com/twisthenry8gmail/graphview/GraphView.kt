package com.twisthenry8gmail.graphview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Range
import android.view.View

class GraphView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var xAxis: AxesElement? = null
    private var yAxis: AxesElement? = null
    private val dataElements = mutableListOf<DataElement>()
    private val graphBounds = GraphBounds()

    fun setMinX(x: Double) {

        graphBounds.dataXRange = Range(x, graphBounds.dataXRange.upper)
        invalidate()
    }

    fun setMaxX(x: Double) {

        graphBounds.dataXRange = Range(graphBounds.dataXRange.lower, x)
        invalidate()
    }

    fun setMinY(y: Double) {

        graphBounds.dataYRange = Range(y, graphBounds.dataYRange.upper)
        invalidate()
    }

    fun setMaxY(y: Double) {

        graphBounds.dataYRange = Range(graphBounds.dataYRange.lower, y)
        invalidate()
    }

    fun setElements(graphElements: List<GraphElement>) {

        graphElements.forEach {

            when (it) {

                is AxesElement -> {

                    if (it.isXAxes) xAxis = it
                    else yAxis = it
                }

                is DataElement -> {

                    dataElements.add(it)
                    graphBounds.ensureRangesInclude(it.data)
                }
            }
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            val w = width.toFloat()
            val h = height.toFloat()

            val xAxisOffset = xAxis?.measureOffset(c) ?: 0F
            val yAxisOffset = yAxis?.measureOffset(c) ?: 0F

            xAxis?.let { axis ->

                graphBounds.drawArea.set(yAxisOffset, h - xAxisOffset, w, h)
                axis.draw(c, graphBounds)
            }

            yAxis?.let { axis ->

                graphBounds.drawArea.set(0F, 0F, yAxisOffset, h - xAxisOffset)
                axis.draw(c, graphBounds)
            }

            graphBounds.drawArea.set(yAxisOffset, 0F, w, h - xAxisOffset)
            dataElements.forEach {

                it.draw(c, graphBounds)
            }
        }
    }
}