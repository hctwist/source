package uk.henrytwist.selectslider

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class SelectSliderView(context: Context, attributeSet: AttributeSet) : RecyclerView(context, attributeSet) {

    private var smoothScrolling = false
    var onSelectListener: OnSelectListener? = null

    private var lastSelectedPosition = 0

    init {

        clipToPadding = false

        layoutManager = object : LinearLayoutManager(context, HORIZONTAL, false) {

            override fun onLayoutCompleted(state: State?) {
                super.onLayoutCompleted(state)
                invalidateItems()
            }
        }

        LinearStartSnapHelper().attachToRecyclerView(this)
    }

    fun setItemGap(margin: Float) {

        addItemDecoration(EndMarginItemDecoration(margin))
    }

    fun setSelection(position: Int) {

        if (lastSelectedPosition != position) {

            lastSelectedPosition = position
            (layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, 0)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        setPaddingRelative(paddingStart, paddingTop, w, paddingBottom)
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {

        if (adapter !is Adapter) {

            throw IllegalArgumentException("SelectSlider must be used with a SelectSliderAdapter")
        }

        adapter.onItemClickListener = {

            smoothScrollInternal(it)
        }

        super.setAdapter(adapter)
    }

    override fun onScrolled(dx: Int, dy: Int) {

        super.onScrolled(dx, dy)

        val selected = invalidateItems()

        if (!smoothScrolling && selected != null) {

            maybeNotifySelect(selected)
        }
    }

    /**
     * @return adapter position of the item that is considered selected
     */
    private fun invalidateItems(): Int? {

        val a = getSliderAdapter() ?: return null
        val lm = layoutManager ?: return null

        val startPadding = paddingStart

        var selected: Int? = null

        var lastItemViewWidth = 1
        forEach {

            val holder = getChildViewHolder(it)

            val itemViewStart = lm.getDecoratedStart(holder.itemView) - startPadding
            val itemViewWidth = holder.itemView.width

            // Start edge is to the end of the snap position
            val selectedFraction = 1F - if (itemViewStart > 0) {

                itemViewStart.toFloat() / lastItemViewWidth
            } else {

                -itemViewStart.toFloat() / itemViewWidth
            }.coerceIn(0F, 1F)

            a.onAnimateViewHolder(holder, selectedFraction)

            if (selectedFraction > 0.5F) {

                selected = holder.adapterPosition
            }

            lastItemViewWidth = itemViewWidth
        }

        return selected
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        if (state == SCROLL_STATE_IDLE) smoothScrolling = false
    }

    private fun getSliderAdapter() = adapter as? Adapter

    private fun smoothScrollInternal(position: Int) {

        val lm = layoutManager ?: return

        val scroller = object : LinearSmoothScroller(context) {

            override fun getHorizontalSnapPreference(): Int {

                return SNAP_TO_START
            }
        }
        scroller.targetPosition = position
        lm.startSmoothScroll(scroller)
        smoothScrolling = true
        maybeNotifySelect(position)
    }

    private fun maybeNotifySelect(position: Int) {

        if (position != lastSelectedPosition) {

            lastSelectedPosition = position
            onSelectListener?.onSelected(position)
        }
    }

    fun interface OnSelectListener {

        fun onSelected(position: Int)
    }

    abstract class Adapter<VH : ViewHolder> : RecyclerView.Adapter<VH>() {

        internal lateinit var onItemClickListener: (Int) -> Unit

        final override fun onBindViewHolder(holder: VH, position: Int) {

            onBindSliderViewHolder(holder, position)
            holder.itemView.setOnClickListener {

                onItemClick(holder, position)
                onItemClickListener(holder.adapterPosition)
            }
        }

        open fun onItemClick(holder: VH, position: Int) {}

        abstract fun onBindSliderViewHolder(holder: VH, position: Int)

        abstract fun onAnimateViewHolder(holder: VH, selectedFraction: Float)
    }
}