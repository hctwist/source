package uk.henrytwist.selectslider

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class EndMarginItemDecoration(margin: Float) : RecyclerView.ItemDecoration() {

    private val margin = margin.roundToInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        if (parent.findContainingViewHolder(view)?.adapterPosition != state.itemCount - 1) {

            if(parent.layoutDirection == RecyclerView.LAYOUT_DIRECTION_RTL) {

                outRect.left = margin
            }
            else {

                outRect.right = margin
            }
        }
    }
}