package uk.henrytwist.androidbasics

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.OnRebindCallback
import androidx.databinding.ViewDataBinding

fun <B : ViewDataBinding> B.interceptNextRebind(bind: B.() -> Unit) {

    addOnRebindCallback(object : OnRebindCallback<B>() {

        override fun onPreBind(binding: B): Boolean {

            bind(binding)
            removeOnRebindCallback(this)
            return false
        }
    })
}

@BindingAdapter("invisibleUnless")
fun invisibleUnless(view: View, boolean: Boolean) {

    view.visibility = if (boolean) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("goneUnless")
fun goneUnless(view: View, boolean: Boolean) {

    view.visibility = if (boolean) View.VISIBLE else View.GONE
}