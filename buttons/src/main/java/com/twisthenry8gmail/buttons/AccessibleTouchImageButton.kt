package com.twisthenry8gmail.buttons

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton

class AccessibleTouchImageButton(context: Context, attrs: AttributeSet) :
    AppCompatImageButton(context, attrs) {

    init {

        val typedAttrs =
            context.obtainStyledAttributes(attrs, R.styleable.AccessibleTouchImageButton)
        val touchPadding =
            typedAttrs.getDimensionPixelSize(R.styleable.AccessibleTouchImageButton_touchPadding, 0)
        typedAttrs.recycle()

        AccessibleButtonsUtil.expandTouchRegion(this, touchPadding)
    }
}