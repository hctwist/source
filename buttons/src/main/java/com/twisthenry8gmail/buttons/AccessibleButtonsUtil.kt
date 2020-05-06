package com.twisthenry8gmail.buttons

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View

object AccessibleButtonsUtil {

    fun expandTouchRegion(view: View, touchPadding: Int) {

        if (touchPadding > 0) {

            view.post {
                (view.parent as? View)?.let { p ->

                    p.post {

                        val hitRect = Rect()
                        view.getHitRect(hitRect)

                        hitRect.top -= touchPadding
                        hitRect.left -= touchPadding
                        hitRect.bottom += touchPadding
                        hitRect.right += touchPadding

                        p.touchDelegate = TouchDelegate(hitRect, view)
                    }
                }
            }
        }
    }
}