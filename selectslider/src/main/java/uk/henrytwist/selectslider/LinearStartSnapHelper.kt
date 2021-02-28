package uk.henrytwist.selectslider

import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class LinearStartSnapHelper : LinearSnapHelper() {

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {

        if (layoutManager == null || layoutManager.childCount == 0) return null

        val start = layoutManager.paddingStart

        var closestDistance: Int? = null
        var closestChild: View? = null

        for (i in 0 until layoutManager.childCount) {

            val child = layoutManager.getChildAt(i) ?: continue

            val childStart = layoutManager.getDecoratedStart(child)

            val distance = abs(childStart - start)
            if (closestDistance == null || distance < closestDistance) {

                closestDistance = distance
                closestChild = child
            }
        }

        return closestChild
    }

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray {

        val direction = if (layoutManager.isRTL()) -1 else 1
        return intArrayOf(direction * (layoutManager.getDecoratedStart(targetView) - layoutManager.paddingStart), 0)
    }
}