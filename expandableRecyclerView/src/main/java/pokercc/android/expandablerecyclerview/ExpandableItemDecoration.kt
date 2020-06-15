package pokercc.android.expandablerecyclerview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

class ExpandableItemDecoration(@ColorInt private val backgroundColor: Int = Color.WHITE) :
    RecyclerView.ItemDecoration() {
    private val backgroundPaint = Paint().apply {
        color = backgroundColor
    }


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        // 需要把ChildrenViewHolder 超出Group的裁剪掉
        // 把下面的空隙补上白色，防止动画穿帮
        var lastParentViewHolder: RecyclerView.ViewHolder? = null
        for (child in parent.children) {
            val childViewHolder = parent.getChildViewHolder(child)
            if (childViewHolder.itemViewType == ExpandableAdapter.GROUP_VIEW_TYPE &&
                childViewHolder.adapterPosition > lastParentViewHolder?.adapterPosition ?: -1
            ) {
                lastParentViewHolder = childViewHolder
            }
        }
        val lastParentItemView = lastParentViewHolder?.itemView ?: return
        // 如果最后一个展开了，就不遮白布了
        if (lastParentItemView.getTag(ExpandableAdapter.GROUP_IS_EXPAND_FLAG) == true) return
        c.drawRect(
            parent.paddingStart.toFloat(),
            lastParentItemView.bottom + lastParentItemView.translationY,
            (parent.width - parent.paddingEnd).toFloat(),
            (parent.height - parent.paddingBottom).toFloat(),
            backgroundPaint
        )
    }
}