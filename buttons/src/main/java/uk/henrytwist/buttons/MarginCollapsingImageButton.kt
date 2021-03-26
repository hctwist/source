package uk.henrytwist.buttons

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton

class MarginCollapsingImageButton(context: Context, attributeSet: AttributeSet) : AppCompatImageButton(context, attributeSet) {

    private var marginsCollapsed = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (!marginsCollapsed) {
            (layoutParams as? ViewGroup.MarginLayoutParams)?.let {

                it.topMargin -= paddingTop
                it.rightMargin -= paddingRight
                it.bottomMargin -= paddingBottom
                it.leftMargin -= paddingLeft

                layoutParams = it
            }

            marginsCollapsed = true
        }
    }
}