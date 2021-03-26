package uk.henrytwist.graphview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.twisthenry8gmail.graphview.R

class GraphView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var graphElements = listOf<GraphElement>()

    private val graphBounds = GraphBounds()
    private val graphStyle: GlobalGraphStyle

    init {

        val attributesArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.GraphView,
            R.attr.graphViewStyle,
            0
        )

        graphStyle = GlobalGraphStyle(attributesArray)

        attributesArray.recycle()
    }

    fun setElements(elements: List<GraphElement>) {

        graphElements = elements
        graphElements.forEach {

            when (it) {

                is AxisElement -> {

                    if (it.isXAxis) {

                        graphBounds.ensureXRangeIncludes(it.range)
                    } else {

                        graphBounds.ensureYRangeIncludes(it.range)
                    }
                }

                is SeriesElement -> {

                    graphBounds.ensureRangesInclude(it.data)
                }

                is PointElement -> {

                    graphBounds.ensureRangesInclude(it.point)
                }
            }

            it.resolveStyle(context, graphStyle)
        }

        invalidateElements()
        invalidate()
    }

    fun invalidateElements() {

        graphBounds.resetPlotRect()
        graphElements.forEach {

            it.resolveBounds(graphBounds)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        graphBounds.width = w.toFloat()
        graphBounds.height = h.toFloat()
        invalidateElements()
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            graphElements.forEach {

                it.draw(c, graphBounds)
            }
        }
    }

    companion object {

        // RELEASE Remove
        val TEST_PAINT = Paint().apply {

            style = Paint.Style.STROKE
            color = Color.RED
            strokeWidth = 2F
        }
    }
}