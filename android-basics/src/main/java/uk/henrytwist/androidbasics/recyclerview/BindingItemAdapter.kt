package uk.henrytwist.androidbasics.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BindingItemAdapter<T : ViewDataBinding>(@LayoutRes val layoutRes: Int) : RecyclerView.Adapter<BindingItemAdapter.Holder<T>>() {

    abstract fun onBind(binding: T, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<T> {

        return Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutRes, parent, false))
    }

    override fun onBindViewHolder(holder: Holder<T>, position: Int) {

        onBind(holder.binding, position)
        holder.binding.executePendingBindings()
    }

    class Holder<T : ViewDataBinding>(internal val binding: T) : RecyclerView.ViewHolder(binding.root)
}