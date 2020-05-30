package com.twisthenry8gmail.graphview

import android.content.res.TypedArray
import android.graphics.Color

class GlobalGraphStyle(attributesArray: TypedArray) {

    val plotColor = attributesArray.getColor(R.styleable.GraphView_graphPlotColor, Color.BLACK)
    val plotColorSecondary =
        attributesArray.getColor(R.styleable.GraphView_graphPlotColorSecondary, Color.BLACK)

    val defaultMargin = attributesArray.getDimension(R.styleable.GraphView_graphDefaultMargin, 0F)

    val plotLineWidth = attributesArray.getDimension(R.styleable.GraphView_graphPlotLineWidth, 1F)

    val axisWidth = attributesArray.getDimension(R.styleable.GraphView_graphAxisWidth, 1F)
    val axisColor = attributesArray.getColor(R.styleable.GraphView_graphAxisColor, Color.BLACK)
    val axisTextColor =
        attributesArray.getColor(R.styleable.GraphView_graphAxisTextColor, Color.BLACK)
    val axisTextSize = attributesArray.getDimension(R.styleable.GraphView_graphAxisTextSize, 1F)
}