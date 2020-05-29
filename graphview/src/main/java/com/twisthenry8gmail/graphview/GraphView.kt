package com.twisthenry8gmail.graphview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Range
import android.view.View

class GraphView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var xAxis: AxisElement? = null
    private var yAxis: AxisElement? = null
    private val dataElements = mutableListOf<DataElement>()
    private val graphBounds = GraphBounds()

    fun setElements(graphElements: List<GraphElement>) {

        graphElements.forEach {

            when (it) {

                is AxisElement -> {

                    if (it.isXAxis) {

                        xAxis = it
                        graphBounds.ensureXRangeIncludes(it.range)
                    } else {

                        yAxis = it
                        graphBounds.ensureYRangeIncludes(it.range)
                    }
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

            graphBounds.dataRect.set(yAxisOffset, 0F, w, h - xAxisOffset)

            xAxis?.let { axis ->

                graphBounds.xAxisRect.set(yAxisOffset, h - xAxisOffset, w, h)
                axis.draw(c, graphBounds)
            }

            yAxis?.let { axis ->

                graphBounds.yAxisRect.set(0F, 0F, yAxisOffset, h - xAxisOffset)
                axis.draw(c, graphBounds)
            }

            dataElements.forEach {

                it.draw(c, graphBounds)
            }
        }
    }
}