package pokercc.android.expandablerecyclerview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView

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
        for (i in 0 until childCount) {
            val other = getChildAt(i)
            if (other == child) continue
            val otherViewHolder = getChildViewHolder(other)
            // adapterPosition小的，不能超过adapterPosition大的
            if (childViewHolder.realPosition < otherViewHolder.realPosition) {
                // 兄弟节点，不涉及裁剪的问题
                if (!expandableAdapter.isGroup(otherViewHolder.itemViewType) &&
                    expandableAdapter.getGroupPosition(childViewHolder) == expandableAdapter.getGroupPosition(
                        otherViewHolder
                    )
                ) continue
                if (child.y + child.height > other.y) {
                    if (DEBUG) Log.d(
                        LOG_TAG,
                        "${childViewHolder.desc()}越过了${otherViewHolder.desc()},被裁剪"
                    )
                    return child.draws(canvas, drawingTime) {
                        it.clipRect(
                            paddingStart,
                            other.y.toInt(),
                            width - paddingEnd,
                            height - paddingBottom
                        )
                    }
                }
            }

        }
        return super.drawChild(canvas, child, drawingTime)
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

    private val ViewHolder.realPosition: Int
        get() {
            val expandableAdapter = adapter as? ExpandableAdapter<*> ?: return 0
            val groupPosition = expandableAdapter.getGroupPosition(this)
            val childPosition = expandableAdapter.getChildPosition(this)
            val isGroup = expandableAdapter.isGroup(itemViewType)

            var position = 0
            for (i in 0 until groupPosition) {
                position += (1 + expandableAdapter.getChildCount(i))
            }
            return if (isGroup) {
                position
            } else {
                position + childPosition + 1
            }
        }

    private fun ViewHolder.desc(): String {
        val expandableAdapter = adapter as? ExpandableAdapter<*> ?: return "null"
        return "{group:${expandableAdapter.getGroupPosition(this)}," +
                "child:${expandableAdapter.getChildPosition(this)}," +
                "adapter:${adapterPosition},layout:${layoutPosition},realPosition:${realPosition}}"
    }

}