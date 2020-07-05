package pokercc.android.expandablerecyclerview.sample.yuanfudao

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import pokercc.android.expandablerecyclerview.sample.dpToPx

internal class YuanfudaoChildIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val circlePaint = Paint().apply {
        isAntiAlias = true
        color = 0xff297be8.toInt()
        style = Paint.Style.FILL
    }
    private val linePaint = Paint().apply {
        color = 0x7a7a7a7a.toInt()
        style = Paint.Style.FILL
    }

    init {
        linePaint.strokeWidth = 1.dpToPx(context)
    }

    var isLast = false
        set(value) {
            field = value
            invalidate()
        }
    private var alignView: View? = null


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
        val radius = 6.dpToPx(context)
        canvas.drawCircle(cx, cy, radius, circlePaint)
        // 绘制线
        if (!isLast) {
            canvas.drawLine(
                cx,
                cy + radius + 2.dpToPx(context),
                cx,
                bottom.toFloat(),
                linePaint
            )
        }
        canvas.drawLine(
            cx,
            top.toFloat(),
            cx,
            cy - radius - 2.dpToPx(context),
            linePaint
        )
    }
}