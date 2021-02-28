package uk.henrytwist.androidbasics.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.annotation.Dimension
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil
import kotlin.math.roundToInt

class MarginItemDecoration(@Dimension margin: Float) : RecyclerView.ItemDecoration() {

    private val halfMargin = (margin / 2).roundToInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val adapterPosition = parent.getChildAdapterPosition(view)

        when (val manager = parent.layoutManager) {

            is GridLayoutManager -> {

                val spanCount = manager.spanCount
                if (adapterPosition % spanCount != 0) {

                    outRect.left = halfMargin
                }
                if (adapterPosition % spanCount < spanCount - 1) {

                    outRect.right = halfMargin
                }

                if (adapterPosition >= spanCount) {

                    outRect.top = halfMargin
                }
                if (adapterPosition < ceil(state.itemCount.toDouble() / spanCount - 1) * spanCount) {

                    outRect.bottom = halfMargin
                }
            }

            is LinearLayoutManager -> {

                val horizontal = manager.orientation == LinearLayoutManager.HORIZONTAL
                if (adapterPosition != 0) {

                    if (horizontal) {

                        outRect.left = halfMargin
                    } else {

                        outRect.top = halfMargin
                    }
                }
                if (adapterPosition < state.itemCount - 1) {

                    if (horizontal) {

                        outRect.right = halfMargin
                    } else {

                        outRect.bottom = halfMargin
                    }
                }
            }

            else -> throw IllegalArgumentException("Only LinearLayoutManager and GridLayoutManager are supported")
        }
    }
}