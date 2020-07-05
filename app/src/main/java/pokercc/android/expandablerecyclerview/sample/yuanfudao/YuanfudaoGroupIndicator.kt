package pokercc.android.expandablerecyclerview.sample.yuanfudao

import android.animation.Animator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import pokercc.android.expandablerecyclerview.sample.dpToPx

internal class YuanfudaoGroupIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val circlePaint = Paint().apply {
        isAntiAlias = true
        color = 0xff297be8.toInt()
        style = Paint.Style.FILL
    }
    private val linePaint1 = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val linePaint2 = Paint().apply {
        color = 0x7a7a7a7a.toInt()
        style = Paint.Style.FILL
    }

    init {
        linePaint1.strokeWidth = 1.5.dpToPx(context)
        linePaint2.strokeWidth = 1.dpToPx(context)
    }

    private var animator: Animator? = null
    private var isExpand = false
    private var alignView: View? = null

    fun setExpand(expand: Boolean, anim: Boolean) {
        if (this.isExpand == expand) return
        this.isExpand = expand
        if (!anim) {
            invalidate()
        } else {
            postInvalidate()
        }
    }

    fun setAlignView(alignView: View) {
        require(alignView.parent == parent)
        this.alignView = alignView
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val alignView = alignView ?: return

        // 绘制一个5dp的原点
        val cx = (width / 2).toFloat()
        val cy = alignView.y + alignView.height / 2
        val radius = 8.dpToPx(context)
        canvas.drawCircle(cx, cy, radius, circlePaint)

        val lineLength = 4.dpToPx(context)
        val animator = animator
        if (animator?.isRunning == true) {

        } else {

            if (isExpand) {
                canvas.drawLine(
                    cx - lineLength,
                    cy,
                    cx + lineLength,
                    cy,
                    linePaint1
                )
                canvas.drawLine(
                    cx,
                    cy + radius + 2.dpToPx(context),
                    cx,
                    bottom.toFloat(),
                    linePaint2
                )
            } else {
                canvas.drawLine(
                    cx,
                    cy - lineLength,
                    cx,
                    cy + lineLength,
                    linePaint1
                )
                canvas.drawLine(
                    cx - lineLength,
                    cy,
                    cx + lineLength,
                    cy,
                    linePaint1
                )
            }
        }
    }
}