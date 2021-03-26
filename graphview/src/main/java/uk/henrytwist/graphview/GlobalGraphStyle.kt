package uk.henrytwist.graphview

import android.content.res.TypedArray
import android.graphics.Color
import com.twisthenry8gmail.graphview.R

class GlobalGraphStyle(attributesArray: TypedArray) {

    val plotColor = attributesArray.getColor(R.styleable.GraphView_graphPlotColor, Color.BLACK)
    val plotColorSecondary =
        attributesArray.getColor(R.styleable.GraphView_graphPlotColorSecondary, Color.BLACK)

    val textColor =
        attributesArray.getColor(R.styleable.GraphView_graphTextColor, Color.BLACK)
    val textSize = attributesArray.getDimension(R.styleable.GraphView_graphTextSize, 1F)

    val defaultMargin = attributesArray.getDimension(R.styleable.GraphView_graphDefaultMargin, 0F)

    val plotLineWidth = attributesArray.getDimension(R.styleable.GraphView_graphPlotLineWidth, 1F)
    
    val plotPointRadius = attributesArray.getDimension(R.styleable.GraphView_graphPlotPointRadius, 1F)

    val axisWidth = attributesArray.getDimension(R.styleable.GraphView_graphAxisWidth, 1F)
    val axisColor = attributesArray.getColor(R.styleable.GraphView_graphAxisColor, Color.BLACK)
}