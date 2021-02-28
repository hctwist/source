package uk.henrytwist.selectslider

import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal fun RecyclerView.LayoutManager.isRTL() = layoutDirection == View.LAYOUT_DIRECTION_RTL

internal fun RecyclerView.LayoutManager.getDecoratedStart(child: View): Int {

    return if (isRTL()) width - getDecoratedRight(child) else getDecoratedLeft(child)
}