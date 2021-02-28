package uk.henrytwist.buttons

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View
import java.lang.RuntimeException

object AccessibleButtonsUtil {

    fun expandTouchRegion(view: View, touchPadding: Int) {

        expandTouchRegion(view, touchPadding, touchPadding)
    }

    fun expandTouchRegion(view: View, touchPaddingHorizontal: Int, touchPaddingVertical: Int) {

        if (touchPaddingHorizontal > 0 || touchPaddingVertical > 0) {

            val viewParent = view.parent as? View ?: return

            view.post {

                viewParent.post {

                    val hitRect = Rect()
                    view.getHitRect(hitRect)

                    hitRect.top -= touchPaddingVertical
                    hitRect.left -= touchPaddingHorizontal
                    hitRect.bottom += touchPaddingVertical
                    hitRect.right += touchPaddingHorizontal

                    viewParent.touchDelegate = TouchDelegate(hitRect, view)
                }
            }
        }
    }
}