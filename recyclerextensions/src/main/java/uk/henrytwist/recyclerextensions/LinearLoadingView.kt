package uk.henrytwist.recyclerextensions

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout

abstract class LinearLoadingView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {

    var bindingStrategy: (View) -> Unit = {}

    var viewRes: Int? = null
        set(value) {

            field = value
            inflateLoadingViews()
        }

    private fun inflateLoadingViews() {

        viewRes?.let { res ->

            View.inflate(context, res, this)

            val viewHeight = getChildAt(0).height
            Log.v("VHeight", viewHeight.toString())
        }
    }
}