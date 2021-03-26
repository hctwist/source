package uk.henrytwist.androidbasics.recyclerview

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

abstract class SingleBindingItemAdapter<T : ViewDataBinding>(@LayoutRes layoutRes: Int) : BindingItemAdapter<T>(layoutRes) {

    abstract fun onBind(binding: T)

    override fun getItemCount(): Int {

        return 1
    }

    override fun onBind(binding: T, position: Int) {

        onBind(binding)
    }
}