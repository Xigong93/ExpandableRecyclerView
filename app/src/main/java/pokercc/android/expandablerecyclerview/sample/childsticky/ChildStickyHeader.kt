package pokercc.android.expandablerecyclerview.sample.childsticky

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.math.MathUtils
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.ExpandableRecyclerView

private const val DEBUG = false

open class ChildStickyHeader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private val stickyHeaderDecoration = StickyChildHeaderDecoration(::onShowHeader)
    private var header: View? = null


    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        if (child is ExpandableRecyclerView) {
            child.addItemDecoration(stickyHeaderDecoration)
            child.adapter
        }
    }


    override fun onViewRemoved(child: View) {
        super.onViewRemoved(child)
        if (child is ExpandableRecyclerView) {
            child.removeItemDecoration(stickyHeaderDecoration)
        }
    }

    private fun onShowHeader(
        header: View,
        y: Float
    ) {
        if (this.header == header) {
            this.header?.y = y
            return
        }
        this.header?.let { removeView(it) }
        this.header = header
        val layoutParams = header.layoutParams?.let(::LayoutParams)
            ?: LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.TOP
        addView(header, layoutParams)
        this.header?.y = y
    }


}

/**
 * 固定头的装饰器
 */
private class StickyChildHeaderDecoration(private val onShowHeader: (View, Float) -> Unit) :
    RecyclerView.ItemDecoration() {
    companion object {
        private const val LOG_TAG = "StickyChildHeader"
    }


    private var headerGroup = -1
    private var headerType: Int? = null
    private var header: ExpandableAdapter.ViewHolder? = null
    private var adapter: ExpandableAdapter<ExpandableAdapter.ViewHolder>? = null
    private var changeObservable = object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            val adapter = adapter ?: return
            val viewHolder = header ?: return
//            val headerPosition = adapter.getGroupAdapterPosition(headerGroup)
//            //TODO
//            if (headerPosition in positionStart..(positionStart + itemCount)) {
//                val payloads = payload?.let { mutableListOf(it) } ?: mutableListOf()
//                adapter.onBindViewHolder(viewHolder, headerPosition, payloads)
//            }

        }

        override fun onChanged() {
            super.onChanged()
            header = null
        }
    }

    private fun getFirstVisibleHolder(
        recyclerView: RecyclerView
    ) = recyclerView.children
        .firstOrNull { it.y <= 0 && it.y + it.height > 0 }
        ?.let { recyclerView.getChildViewHolder(it) }


    override fun onDraw(c: Canvas, p: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, p, state)
        val recyclerView = p as? ExpandableRecyclerView ?: return

        @Suppress("UNCHECKED_CAST")
        val adapter = recyclerView.adapter as? ExpandableAdapter<ExpandableAdapter.ViewHolder>
        adapter ?: return
        if (this.adapter != adapter) {
            this.adapter?.unregisterAdapterDataObserver(changeObservable)
            adapter.registerAdapterDataObserver(changeObservable)
            this.adapter = adapter
            header = null
        }
        val groupPosition = getFirstVisibleHolder(recyclerView)
            ?.let { adapter.getItemLayoutPosition(it) }
            ?.groupPosition ?: return
        val childItemType = adapter.getChildItemViewType(groupPosition, 0)
        // Create Header
        if (this.header == null || this.headerType != childItemType) {
            this.headerGroup = -1
            this.headerType = childItemType
            this.header = adapter.createViewHolder(recyclerView, childItemType)
            if (DEBUG) {
                this.header?.itemView?.setBackgroundColor(Color.RED)
            }
        }

        // Bind Header
        val headerViewHolder = this.header ?: return
        if (this.headerGroup != groupPosition) {
            val position = adapter.getChildAdapterPosition2(groupPosition, 0)
            if (position != null) {
                adapter.bindViewHolder(headerViewHolder, position)
                this.headerGroup = groupPosition
            }
        }

        // Calculate position in [originalPosition,beforeLastChildPosition]
        val topBorder = recyclerView.findChildViewHolder(groupPosition, 0)
        var topY = 0f
        if (topBorder != null) {
            topY = topBorder.itemView.y
        }
        val groupTopBorder = recyclerView.findGroupViewHolder(groupPosition)
        if (groupTopBorder != null) {
            topY = topY.coerceAtLeast(groupTopBorder.itemView.y + groupTopBorder.itemView.height)
        }
        val childCount = adapter.getChildCount(groupPosition)
        val bottomBorder = recyclerView.findChildViewHolder(groupPosition, childCount - 1)?.itemView
        var bottomY = 0f
        if (bottomBorder != null) {
            bottomY = bottomBorder.y - headerViewHolder.itemView.height
        }
        val y = MathUtils.clamp(
            topY.coerceAtLeast(0f),
            topY,
            bottomY
        )
        onShowHeader(headerViewHolder.itemView, y)
        headerViewHolder.itemView.isVisible = adapter.isExpand(groupPosition)
        if (DEBUG) {
            Log.d(
                LOG_TAG,
                "onGroupShow,topY:${topY},bottomY:${bottomY},nextGroupView:${bottomBorder}"
            )
        }

    }

}