package uk.henrytwist.recyclerextensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class StateRecyclerAdapter(var emptyViewRes: Int? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class State {

        LOADING, EMPTY, NON_EMPTY
    }

    private var state = State.LOADING

    open fun getEmptyItemCount() = 0

    abstract fun getStatefulItemCount(): Int

    abstract fun onCreateStatefulViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder

    abstract fun onBindStatefulViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        isLoading: Boolean
    )

    abstract fun getStatefulItemViewType(position: Int, isLoading: Boolean): Int

    fun notifyLoadingFinished() {

        if (state == State.LOADING) {

            resolveEmptyState()

            notifyDataSetChanged()
        }
    }

    private fun resolveEmptyState() {

        state = if (getStatefulItemCount() == getEmptyItemCount()) State.EMPTY else State.NON_EMPTY
    }

    final override fun getItemCount(): Int {

        return when (state) {

            State.LOADING -> Integer.MAX_VALUE
            State.EMPTY -> getEmptyItemCount() + 1
            State.NON_EMPTY -> getStatefulItemCount()
        }
    }

    final override fun getItemViewType(position: Int): Int {

        return getStatefulItemViewType(position, state == State.LOADING)
    }

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return if (state == State.EMPTY) {

            EmptyViewHolder(
                LayoutInflater.from(parent.context).inflate(emptyViewRes!!, parent, false)
            )
        } else {

            onCreateStatefulViewHolder(parent, viewType)
        }
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (state != State.EMPTY) {

            onBindStatefulViewHolder(holder, position, state == State.LOADING)
        }
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}