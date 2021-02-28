package uk.henrytwist.graphview

import android.content.Context
import androidx.annotation.ColorRes

class ColorResolver {

    @ColorRes
    var colorResource: Int? = null

    var colorInt: Int? = null

    fun resolve(context: Context): Int? {

        return colorInt ?: colorResource?.let { context.getColor(it) }
    }
}