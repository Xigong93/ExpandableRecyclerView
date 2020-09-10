package pokercc.android.expandablerecyclerview.sample.markets

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.view.iterator
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableRecyclerView
import pokercc.android.expandablerecyclerview.sample.dpToPx

class MarketsItemDecoration : RecyclerView.ItemDecoration() {
    private val linePaint = Paint().apply {
        color = 0xfff6f6f8.toInt()
        strokeWidth = 1.dpToPx()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        parent as ExpandableRecyclerView
        val adapter = parent.requireAdapter()
        val layoutManager = parent.layoutManager!!

        // Draw divide line between children item.
        for (view in parent) {
            val viewHolder = parent.getChildViewHolder(view)
            val childPosition = adapter.getChildPosition(viewHolder)
            val groupPosition = adapter.getGroupPosition(viewHolder)
            val childCount = adapter.getChildCount(groupPosition)
            if (!adapter.isGroup(viewHolder.itemViewType) && childPosition != childCount - 1) {
                val y = layoutManager.getDecoratedBottom(view) + view.translationY
                parent.clipAndDrawChild(c, view) {
                    it.drawLine(
                        parent.paddingStart + 10.dpToPx(), y,
                        parent.width - parent.paddingEnd.toFloat(), y,
                        linePaint
                    )
                }

            }
        }

    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        parent as ExpandableRecyclerView
        val adapter = parent.requireAdapter()
        val viewHolder = parent.getChildViewHolder(view)

        //Give bottom margin when it is group type or it is the last one of group.
        val isGroup = adapter.isGroup(viewHolder.itemViewType)
        val isLastChild = {
            val childPosition = adapter.getChildPosition(viewHolder)
            val groupPosition = adapter.getGroupPosition(viewHolder)
            val childCount = adapter.getChildCount(groupPosition)
            childPosition == childCount - 1
        }
        if (/*isGroup ||*/ isLastChild()) {
            outRect.bottom = 12.dpToPx().toInt()
        }

    }
}