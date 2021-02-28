package uk.henrytwist.buttons

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageButton

class AccessibleTouchImageButton(context: Context, attrs: AttributeSet) :
        AppCompatImageButton(context, attrs) {

    private val minTouchSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48F, resources.displayMetrics).toInt()
    private var autoExpandTouchRegion = false

    init {

        val typedAttrs =
                context.obtainStyledAttributes(attrs, R.styleable.AccessibleTouchImageButton)

        if (typedAttrs.hasValue(R.styleable.AccessibleTouchImageButton_touchPadding)) {

            val touchPadding = typedAttrs.getDimensionPixelSize(R.styleable.AccessibleTouchImageButton_touchPadding, 0)
            AccessibleButtonsUtil.expandTouchRegion(this, touchPadding)
        } else {

            autoExpandTouchRegion = true
        }

        typedAttrs.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        if (autoExpandTouchRegion) {

            val horizontalTouchPadding = ((minTouchSize - w) / 2).coerceAtLeast(0)
            val verticalTouchPadding = ((minTouchSize - h) / 2).coerceAtLeast(0)

            AccessibleButtonsUtil.expandTouchRegion(this, horizontalTouchPadding, verticalTouchPadding)
        }
    }
}