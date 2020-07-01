package pokercc.android.expandablerecyclerview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min

/**
 * 可展开的RecycleView
 * - 支持展开动画，防止View动画错乱
 * @author pokercc
 * @date 2020年06月30日22:18:47
 */
open class ExpandableRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    companion object {
        private const val LOG_TAG = "ExpandableRecyclerView"

        @Suppress("MayBeConstant")
        val DEBUG = BuildConfig.DEBUG
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter != null) {
            require(adapter is ExpandableAdapter)
            itemAnimator = ExpandableItemAnimator(adapter)
        }
        super.setAdapter(adapter)
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        // 未执行动画，不需要裁减
        if (!isAnimating) {
            return super.drawChild(canvas, child, drawingTime)
        }
        val expandableAdapter = adapter as? ExpandableAdapter<*>
            ?: return super.drawChild(canvas, child, drawingTime)
        val childViewHolder = getChildViewHolder(child)
        // 不裁减GroupViewHolder
        if (expandableAdapter.isGroup(childViewHolder.itemViewType)) {
            return super.drawChild(canvas, child, drawingTime)
        }
        val childGroupPosition = expandableAdapter.getGroupPosition(childViewHolder)
        // 不能越过自己的group,也不能越过上一个group
        val groupView = findGroupViewHolder(childGroupPosition)?.itemView
        val groupViewBottom = groupView?.let { it.y + it.height } ?: 0f
        val nextGroupView = findGroupViewHolder(childGroupPosition + 1)?.itemView
        val top = max(child.y, groupViewBottom)
        val bottom = min(child.y + child.bottom, nextGroupView?.y ?: height.toFloat())
        return child.draws(canvas, drawingTime) {
            it.clipRect(
                child.x,
                top,
                child.x + child.width,
                bottom
            )
        }
    }

    private fun findGroupViewHolder(groupPosition: Int): ViewHolder? {
        val expandableAdapter = adapter as? ExpandableAdapter<*> ?: return null
        for (child in children) {
            val viewHolder = getChildViewHolder(child)
            if (!expandableAdapter.isGroup(viewHolder.itemViewType)) continue
            if (groupPosition == expandableAdapter.getGroupPosition(viewHolder)) {
                return viewHolder
            }
        }
        return null
    }

    /**
     * 绘制到这个View下面
     */
    private fun View.draws(
        canvas: Canvas,
        drawingTime: Long,
        canvasCallback: (Canvas) -> Unit
    ): Boolean {
        val saveCount = canvas.save()
        val drawChild: Boolean
        try {
            canvasCallback(canvas)
            drawChild = super.drawChild(canvas, this, drawingTime)
        } finally {
            canvas.restoreToCount(saveCount)
        }
        return drawChild
    }



}