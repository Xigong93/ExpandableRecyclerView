package pokercc.android.expandablerecyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

private const val DEBUG = false

private const val LOG_TAG = "StickyHeader"

open class StickyHeader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private val stickyHeaderDecoration = StickyHeaderDecoration(::onShowHeader)
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
        if (DEBUG) {
            Log.d(
                LOG_TAG,
                "onShowHeader(y:${y},header:${header},this.header:${this.header})"
            )
        }
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
private class StickyHeaderDecoration(private val onShowHeader: (View, Float) -> Unit) :
    RecyclerView.ItemDecoration() {


    private var headerGroup = -1
    private var headerType: Int? = null
    private var header: ExpandableAdapter.ViewHolder? = null
    private var adapter: ExpandableAdapter<ExpandableAdapter.ViewHolder>? = null
    private var changeObservable = object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            val adapter = adapter ?: return
            val viewHolder = header ?: return
            val headerPosition = adapter.getGroupAdapterPosition(headerGroup)
            if (headerPosition in positionStart..(positionStart + itemCount)) {
                val payloads = payload?.let { mutableListOf(it) } ?: mutableListOf()
                adapter.onBindViewHolder(viewHolder, headerPosition, payloads)
            }

        }

        override fun onChanged() {
            super.onChanged()
//            header = null
            headerGroup = -1
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
        val firstGroup = getFirstVisibleHolder(recyclerView)
            ?.let { adapter.getItemLayoutPosition(it) }
            ?.groupPosition
            ?: return
        val groupType = adapter.getGroupItemViewType(firstGroup)
        // Create Header
        if (this.header == null || this.headerType != groupType) {
            this.headerGroup = -1
            this.headerType = groupType
            this.header = adapter.onCreateViewHolder(recyclerView, groupType)
            if (DEBUG) {
                this.header?.itemView?.setBackgroundColor(Color.RED)
            }
        }

        // Bind Header
        val headerViewHolder = this.header ?: return
        if (this.headerGroup != firstGroup) {
            val position = adapter.getGroupAdapterPosition(firstGroup)
            adapter.onBindViewHolder(headerViewHolder, position, mutableListOf())
            this.headerGroup = firstGroup
        }

        // Calculate position
        val nextGroupView = recyclerView.findGroupViewHolder(firstGroup + 1)?.itemView
        var y = 0f
        if (nextGroupView != null) {
            y = nextGroupView.y - headerViewHolder.itemView.height
        }
        y = y.coerceAtMost(0f)
        onShowHeader(headerViewHolder.itemView, y)
        if (DEBUG) {
            Log.d(
                LOG_TAG,
                "onGroupShow,y:${y},height:${headerViewHolder.itemView.height},nextGroupView:${nextGroupView}"
            )
        }

    }

}