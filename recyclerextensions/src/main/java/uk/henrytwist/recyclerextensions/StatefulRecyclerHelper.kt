package uk.henrytwist.recyclerextensions

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StatefulRecyclerHelper<A : RecyclerView.Adapter<VH>, VH : RecyclerView.ViewHolder>(val mainAdapter: A) {

    var loadingAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null
        set(value) {

            field = value
            if (loading) {

                setAdapter()
            }
        }

    var emptyPlaceholder: View? = null
        set(value) {

            if (value == null) return

            field = value

            value.visibility = if (loading || !isAdapterEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

    // This can be safely called multiple times
    var loading = true
        set(value) {

            if (field != value) {

                field = value
                setAdapter()
            }
        }

    var canReuseViewHolders = false

    private var recyclerView: RecyclerView? = null

    private val adapterObserver = object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {

            onAdapterEmptyChanged(false)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {

            onAdapterEmptyChanged(isAdapterEmpty())
        }
    }

    init {

        mainAdapter.registerAdapterDataObserver(adapterObserver)
    }

    fun attachToRecyclerView(recyclerView: RecyclerView) {

        this.recyclerView = recyclerView
        setAdapter()
    }

    private fun setAdapter() {

        if (loading) {

            loadingAdapter?.let {

                setAdapter(it)
            }
        } else {

            setAdapter(mainAdapter)
            onAdapterEmptyChanged(isAdapterEmpty())
        }
    }

    private fun setAdapter(adapter: RecyclerView.Adapter<*>) {

        if (canReuseViewHolders) {

            recyclerView?.swapAdapter(adapter, false)
        } else {

            recyclerView?.adapter = adapter
        }
    }

    private fun onAdapterEmptyChanged(empty: Boolean) {

        if (loading || emptyPlaceholder == null) return

        if (empty) {

            recyclerView?.animateVisibility(View.GONE)
            emptyPlaceholder?.animateVisibility(View.VISIBLE)
        } else {

            recyclerView?.animateVisibility(View.VISIBLE)
            emptyPlaceholder?.animateVisibility(View.GONE)
        }
    }

    private fun isAdapterEmpty() = mainAdapter.itemCount == 0

    private fun View.animateVisibility(toVisibility: Int) {

        if (visibility == toVisibility) return

        val duration = 200L
        val to = if (toVisibility == View.VISIBLE) 1F else 0F

        animate().alpha(to).setDuration(duration).withStartAction {

            visibility = toVisibility
        }.start()
    }

    abstract class LoadingAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

        override fun getItemCount(): Int {

            return Integer.MAX_VALUE
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {

            recyclerView.suppressLayout(true)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {

            recyclerView.suppressLayout(false)
        }
    }
}