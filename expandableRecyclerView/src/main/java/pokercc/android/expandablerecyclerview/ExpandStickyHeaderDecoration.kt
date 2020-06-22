package pokercc.android.expandablerecyclerview

import android.graphics.Canvas
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

/**
 * 固定头的装饰器
 */
class ExpandStickyHeaderDecoration : RecyclerView.ItemDecoration() {
    companion object {
        private const val LOG_TAG = "ExpandStickyHeaderD"
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val expandableAdapter = parent.adapter as? ExpandableAdapter<*> ?: return
        for (child in parent.children) {
            val viewHolder = parent.getChildViewHolder(child)
            if (!expandableAdapter.isGroup(viewHolder.itemViewType)) continue
            val groupPosition = expandableAdapter.getGroupPosition(viewHolder)
            val expand = expandableAdapter.isExpand(groupPosition)
            if (!expand) continue
//            ViewCompat.offsetTopAndBottom(viewHolder.itemView, -viewHolder.itemView.top)
            val itemView = viewHolder.itemView
            val nextViewHolder = findGroupViewHolder(parent, groupPosition + 1)

            // top+tY+height<=new.top
            if (nextViewHolder != null && nextViewHolder.itemView.top < itemView.height) {
                itemView.translationY =
                    -itemView.top.toFloat() - (itemView.height - nextViewHolder.itemView.top)
            } else {
                itemView.translationY = -itemView.top.toFloat()
            }
            // 找到下一个的group的top,bottom不能超过nextViewHolder.top
            Log.d(LOG_TAG, "expand.groupPosition=${groupPosition}")
            break

        }

    }

    private fun findGroupViewHolder(
        parent: RecyclerView,
        groupPosition: Int
    ): RecyclerView.ViewHolder? {
        val expandableAdapter = parent.adapter as? ExpandableAdapter<*> ?: return null
        for (child in parent.children) {
            val viewHolder = parent.getChildViewHolder(child)
            if (!expandableAdapter.isGroup(viewHolder.itemViewType)) continue
            if (groupPosition == expandableAdapter.getGroupPosition(viewHolder)) {
                return viewHolder
            }
        }
        return null
    }

}