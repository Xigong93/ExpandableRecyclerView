package pokercc.android.expandablerecyclerview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), StickyHeaderDecoration.Callback {

    private val stickyHeaderDecoration = StickyHeaderDecoration(this)
    private var header: View? = null


    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        if (child is RecyclerView) {
            child.addItemDecoration(stickyHeaderDecoration)
        }
    }


    override fun onViewRemoved(child: View) {
        super.onViewRemoved(child)
        if (child is RecyclerView) {
            child.removeItemDecoration(stickyHeaderDecoration)
        }
    }
    
    override fun onGroupShow(
        recyclerView: RecyclerView,
        expandableAdapter: ExpandableAdapter<RecyclerView.ViewHolder>,
        viewHolder: RecyclerView.ViewHolder,
        offset: Float
    ) {
        val itemView = viewHolder.itemView
        header?.isVisible = true
        if (header == itemView) {
            itemView.translationY = offset
            return
        }
        header?.let { removeView(it) }
        header = itemView
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.TOP
        )
        addView(header, layoutParams)
    }

    override fun onGroupHide() {
        header?.isVisible = false
    }


}

/**
 * 固定头的装饰器
 */
class StickyHeaderDecoration(private val callback: Callback) : RecyclerView.ItemDecoration() {
    companion object {
        private const val LOG_TAG = "ExpandStickyHeaderD"
    }


    private var groupPosition = -1
    private var groupItemViewType: Int? = null
    private var groupViewHolder: RecyclerView.ViewHolder? = null

    /* |------
     * |------ 0
     * |
     * |
     * |------ last
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val expandableAdapter =
            parent.adapter as? ExpandableAdapter<RecyclerView.ViewHolder> ?: return
        if (parent.isEmpty()) return
        val viewHolder = parent.getChildViewHolder(parent[0])
        val groupPosition = expandableAdapter.getGroupPosition(viewHolder)
        if (!expandableAdapter.isExpand(groupPosition)) {
            callback.onGroupHide()
            return
        }
        val groupItemViewType = expandableAdapter.getGroupItemViewType(groupPosition)

        // 创建数据
        if (this.groupViewHolder == null || this.groupItemViewType != groupItemViewType) {
            this.groupPosition = -1
            this.groupItemViewType = groupItemViewType
            this.groupViewHolder = expandableAdapter.onCreateViewHolder(parent, groupItemViewType)
        }

        // 绑定数据
        val groupViewHolder = this.groupViewHolder!!
        if (this.groupPosition != groupPosition) {
            expandableAdapter.onBindViewHolder(
                groupViewHolder, expandableAdapter.getGroupAdapterPosition(groupPosition),
                emptyList()
            )
            this.groupPosition = groupPosition
        }

        // 计算偏移量
        val nextViewHolder = findGroupViewHolder(parent, groupPosition + 1)
        var offset = 0f
        if (nextViewHolder != null) {
            val view = nextViewHolder.itemView
            offset = view.top + view.translationY - groupViewHolder.itemView.height
            offset = minOf(offset, 0f)
        }
        callback.onGroupShow(parent, expandableAdapter, groupViewHolder, offset)
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

    interface Callback {
        fun onGroupShow(
            recyclerView: RecyclerView,
            expandableAdapter: ExpandableAdapter<RecyclerView.ViewHolder>,
            viewHolder: RecyclerView.ViewHolder,
            offset: Float
        )

        fun onGroupHide()
    }
}