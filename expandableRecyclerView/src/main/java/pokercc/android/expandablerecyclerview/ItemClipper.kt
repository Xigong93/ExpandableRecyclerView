package pokercc.android.expandablerecyclerview

import android.graphics.Rect
import android.view.View
import kotlin.math.ceil
import kotlin.math.floor

internal class ItemClipper(private val target: View) {

    private val clipRect = Rect()

    fun setBorder(left: Float, top: Float, right: Float, bottom: Float) {
        val y = target.y
        clipRect.set(
            ceil(left).toInt(),
            ceil(top - y).toInt(),
            floor(right).toInt(),
            floor(bottom - y).toInt()
        )
        target.clipBounds = clipRect
    }

    val skipDraw: Boolean
        get() = clipRect.isEmpty || clipRect.top >= target.height || clipRect.bottom <= 0


    override fun toString(): String {
        return "ItemClipper(clipRect=$clipRect,skipDraw=$skipDraw)"
    }


}