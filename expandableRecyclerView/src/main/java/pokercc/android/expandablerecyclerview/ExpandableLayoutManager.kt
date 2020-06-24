package pokercc.android.expandablerecyclerview

import android.content.Context
import android.util.Log
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

class ExpandableLayoutManager(context: Context) : LinearLayoutManager(context) {
    companion object {
        private val DEBUG get() = ExpandableAdapter.DEBUG
        private const val LOG_TAG = "ExpandableLayoutManager"
    }

    @Deprecated("Unsupported", ReplaceWith("Only support Vertical"))
    override fun setOrientation(orientation: Int) {
        require(orientation == VERTICAL) { "invalid orientation:$orientation" }
        super.setOrientation(orientation)
    }

    private var recyclerView: WeakReference<RecyclerView>? = null
    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        recyclerView = WeakReference(view)
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        val result = super.scrollVerticallyBy(dy, recycler, state)
        tryToLayoutHeader(recycler)
        tryToRemoveHeader(recycler)
        return result
    }

    /**
     * 显示规则:
     * - 如果GroupHeader存在的话，需要在RecyclerView滑动后，GroupHeader保持不变
     *      - 如果group 中children[0] 的top+translateY >GroupHeader.bottom,GroupHeader 的滑动抑制要释放了
     *      - 如果group 中last Children 的bottom+translateY<GroupHeader.bottom,这个时候要滑动一点点
     * - 如果GroupHeader不存在的话，需要生成一个
     *      - children 显示出来了,并且上一组的 Children 没有显示出来
     */
    private fun tryToLayoutHeader(recycler: RecyclerView.Recycler) {
        // 找到当前的Group,如果不可见的话，布局一下
        val recyclerView = recyclerView?.get() ?: return
        val firstChild = recyclerView.getChildAt(0)
        val expandableAdapter = recyclerView.adapter as? ExpandableAdapter<*> ?: return
        val childViewHolder = recyclerView.getChildViewHolder(firstChild)
        if (expandableAdapter.isGroup(childViewHolder.itemViewType)) return
        val groupPosition = expandableAdapter.getGroupPosition(childViewHolder)
        if (!expandableAdapter.isExpand(groupPosition)) return
        // 检查是否已经添加了
        if (findGroupViewHolder(recyclerView, groupPosition).isNotEmpty()) return
        val groupAdapterPosition = expandableAdapter.getGroupAdapterPosition(groupPosition)
        val groupView = recycler.getViewForPosition(groupAdapterPosition)
        Log.d(
            LOG_TAG,
            "groupPosition:${groupPosition},groupAdapterPosition:${groupAdapterPosition}"
        )
        // 2. 添加
        addView(groupView)
        // 3. 测量
        measureChildWithMargins(groupView, paddingStart + paddingEnd, paddingTop + paddingBottom)
        // 4. 布局
        layoutDecoratedWithMargins(
            groupView,
            paddingStart,
            paddingTop,
            paddingStart + groupView.measuredWidth,
            paddingTop + groupView.measuredHeight
        )
    }

    private fun tryToRemoveHeader(recycler: RecyclerView.Recycler) {
        val recyclerView = recyclerView?.get() ?: return
        val expandableAdapter = recyclerView.adapter as? ExpandableAdapter<*> ?: return

        // 如果某个group有两条，就应该删除一个
        val removes = mutableListOf<RecyclerView.ViewHolder>()
        for (i in 0 until recyclerView.childCount) {
            val viewHolder = recyclerView.getChildViewHolder(recyclerView[i])
            val groupPosition = expandableAdapter.getGroupPosition(viewHolder)
            if (groupPosition < 0) continue
            val viewHolders = findGroupViewHolder(recyclerView, groupPosition)
            if (viewHolders.size > 1) {
                removes.add(viewHolders[1])
            }
        }
        for (viewHolder in removes) {
            removeAndRecycleView(viewHolder.itemView, recycler)
        }
    }

    private fun findGroupViewHolder(
        parent: RecyclerView,
        groupPosition: Int
    ): List<RecyclerView.ViewHolder> {
        val viewHolders = mutableListOf<RecyclerView.ViewHolder>()
        val expandableAdapter = parent.adapter as? ExpandableAdapter<*> ?: return viewHolders
        for (child in parent.children) {
            val viewHolder = parent.getChildViewHolder(child)
            if (!expandableAdapter.isGroup(viewHolder.itemViewType)) continue
            if (groupPosition == expandableAdapter.getGroupPosition(viewHolder)) {
                viewHolders += viewHolder
            }
        }
        return viewHolders
    }

}