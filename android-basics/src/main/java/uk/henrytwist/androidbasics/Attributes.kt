package uk.henrytwist.androidbasics

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

@ColorInt
fun Context.getColorAttr(@AttrRes colorAttr: Int): Int {

    val typedValue = TypedValue()
    if (!theme.resolveAttribute(colorAttr, typedValue, true)) {

        throw IllegalArgumentException("Could not find the color attribute $colorAttr")
    }

    return if (typedValue.resourceId != 0) {

        ContextCompat.getColor(this, typedValue.resourceId)
    } else {

        typedValue.data
    }
}