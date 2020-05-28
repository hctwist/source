package com.twisthenry8gmail.graphview

import android.graphics.Canvas

abstract class TickAxes(
    xAxes: Boolean,
    val ticks: List<Double>,
    val labelFactory: (Double) -> String
) : AxesElement(xAxes) {

    class TickAxesX(ticks: List<Double>, labelFactory: (Double) -> String) :
        TickAxes(true, ticks, labelFactory) {

        override fun measureOffset(canvas: Canvas): Float {

            return 0F
        }

        override fun draw(canvas: Canvas, graphBounds: GraphBounds) {


        }
    }

    class TickAxesY(ticks: List<Double>, labelFactory: (Double) -> String) :
        TickAxes(false, ticks, labelFactory) {

        override fun measureOffset(canvas: Canvas): Float {

            return 0F
        }

        override fun draw(canvas: Canvas, graphBounds: GraphBounds) {


        }
    }
}