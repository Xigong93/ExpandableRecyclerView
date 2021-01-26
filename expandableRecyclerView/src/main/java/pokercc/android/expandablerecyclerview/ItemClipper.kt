package pokercc.android.expandablerecyclerview

import android.graphics.Rect
import android.view.View


internal class ItemClipper(private val target: View) {

    private val clipRect = Rect()
    //    private var clip = false
    fun setBorder(left: Int, top: Int, right: Int, bottom: Int) {
        val y = target.y
        clipRect.set(
            left, (top - y).toInt(),
            right, (bottom - y).toInt()
        )
        target.clipBounds = clipRect
    }


}