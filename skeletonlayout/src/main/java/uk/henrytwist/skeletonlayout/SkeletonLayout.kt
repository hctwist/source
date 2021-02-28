package uk.henrytwist.skeletonlayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.twisthenry8gmail.skeletonlayout.R

class SkeletonLayout(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var showSkeleton = false

    private val boneDrawable: Drawable

    private val visibilityStore = hashMapOf<View, Int>()

    init {

        context.obtainStyledAttributes(attrs, R.styleable.SkeletonLayout).run {

            boneDrawable = getDrawable(R.styleable.SkeletonLayout_skeleton)
                ?: throw IllegalArgumentException("There must be a skeleton resource on this view")
            recycle()
        }
    }

    fun toggleSkeleton(show: Boolean) {

        if (showSkeleton != show) {

            showSkeleton = show
            invalidateVisibility()
            invalidate()
        }
    }

    // TODO Global layout tree listener fires when view visibility changes, use that to update store and override back to invisible if needed
    private fun invalidateVisibility() {

        for (i in 0 until childCount) {

            val child = getChildAt(i)
            val params = child.layoutParams

            if (params is LayoutParams) {

                if (showSkeleton) {

                    if (params.mode == LayoutParams.Mode.SKELETON || params.mode == LayoutParams.Mode.INVISIBLE) {

                        visibilityStore[child] = child.visibility
                        child.visibility = View.INVISIBLE
                    }
                } else {

                    child.visibility = visibilityStore[child] ?: View.VISIBLE
                }
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): ConstraintLayout.LayoutParams {

        return LayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {

        return p is LayoutParams
    }

    override fun generateDefaultLayoutParams(): ConstraintLayout.LayoutParams {

        return LayoutParams(super.generateDefaultLayoutParams())
    }

    override fun dispatchDraw(canvas: Canvas?) {

        super.dispatchDraw(canvas)

        if (showSkeleton && canvas != null) {

            for (i in 0 until childCount) {

                drawSkeleton(canvas, getChildAt(i))
            }
        }
    }

    private fun drawSkeleton(canvas: Canvas, child: View) {

        val childParams = child.layoutParams
        if (childParams is LayoutParams && childParams.mode == LayoutParams.Mode.SKELETON) {

            drawBone(canvas, child)
        }
    }

    private fun drawBone(canvas: Canvas, view: View) {

        when (view) {

            is TextView -> drawBoneForTextView(canvas, view)

            else -> drawBoneForView(canvas, view)
        }
    }

    private fun drawBoneForView(canvas: Canvas, view: View) {

        drawBone(canvas, view.left, view.top, view.right, view.bottom)
    }

    private fun drawBoneForTextView(canvas: Canvas, textView: TextView) {

        val params = textView.layoutParams as LayoutParams

        val textLength = params.textLength
        if (textLength == LayoutParams.ORIGINAL_TEXT_LENGTH) {

            drawBoneForView(canvas, textView as View)
        } else {

            // TODO Text alignment and RTL etc
            val builder = StringBuilder()
            repeat(textLength) {

                builder.append("M")
            }
            val textWidth = textView.paint.measureText(builder.toString())
            drawBone(
                canvas,
                textView.left,
                textView.top,
                textView.left + textWidth.toInt(),
                textView.bottom
            )
        }
    }

    private fun drawBone(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) {

        boneDrawable.setBounds(left, top, right, bottom)
        boneDrawable.draw(canvas)
    }

    class LayoutParams : ConstraintLayout.LayoutParams {

        val mode: Mode
        val textLength: Int

        constructor(source: ConstraintLayout.LayoutParams) : super(source) {

            mode = SHOW_SKELETON_DEFAULT
            textLength = TEXT_LENGTH_DEFAULT
        }

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

            context.obtainStyledAttributes(attrs, R.styleable.SkeletonLayout_Layout).run {

                mode =
                    Mode.values()[getInt(
                        R.styleable.SkeletonLayout_Layout_layout_skeletonMode,
                        0
                    )]

                textLength =
                    getInteger(
                        R.styleable.SkeletonLayout_Layout_layout_skeletonTextLength,
                        TEXT_LENGTH_DEFAULT
                    )

                recycle()
            }
        }

        enum class Mode {

            SKELETON, INVISIBLE, CONTENT
        }

        companion object {

            const val ORIGINAL_TEXT_LENGTH = -1

            val SHOW_SKELETON_DEFAULT = Mode.SKELETON
            const val TEXT_LENGTH_DEFAULT = ORIGINAL_TEXT_LENGTH
        }
    }
}