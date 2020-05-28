package com.twisthenry8gmail.graphview

import android.graphics.Canvas
import android.graphics.Paint

class LineElement(data: List<DataPoint>, lineWidth: Float, lineColor: Int) : DataElement(data) {

    val linePaint = Paint().apply {

        strokeWidth = lineWidth
        color = lineColor
    }

    override fun draw(canvas: Canvas, graphBounds: GraphBounds) {

        val offset = 10F

        graphBounds.drawArea.inset(offset, offset)

        if (data.size > 1) {

            for (i in 0 until data.size - 1) {

                val p1 = graphBounds.mapToDrawArea(data[i])
                val p2 = graphBounds.mapToDrawArea(data[i + 1])

                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, linePaint)
            }
        }
    }
}