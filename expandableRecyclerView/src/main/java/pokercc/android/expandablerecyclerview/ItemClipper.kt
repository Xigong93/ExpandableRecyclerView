package pokercc.android.expandablerecyclerview

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.RectF
import android.view.View
import android.widget.FrameLayout

@SuppressLint("ViewConstructor")
internal class ItemCliper(private val child: View)   {


    private val clipRect = RectF()

    private var willNotClip = false
    fun setClipRect(left: Float, top: Float, right: Float, bottom: Float) {
        clipRect.set(
            left,
            top - y,
            right,
            bottom - y
        )
        invalidate()
        willNotClip = false
        invalidateOutline()
    }

    fun willNotClip() {
        willNotClip = true
    }

    override fun dispatchDraw(canvas: Canvas) {
        val clip = clipRect
        if (willNotClip) {
            super.dispatchDraw(canvas)
            return
        }
        val count = canvas.save()
        try {
            canvas.clipRect(clip)
            super.dispatchDraw(canvas)
        } finally {
            canvas.restoreToCount(count)
        }
    }
}