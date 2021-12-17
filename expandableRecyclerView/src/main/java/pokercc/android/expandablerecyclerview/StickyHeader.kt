package pokercc.android.expandablerecyclerview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

private const val DEBUG = false

private const val LOG_TAG = "StickyHeader"

open class StickyHeader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private val headerUpdater = HeaderUpdater(::onShowHeader)
    private var header: View? = null

    private val itemDecoration = object : RecyclerView.ItemDecoration() {
        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)
            headerUpdater.updateHeader(parent as ExpandableRecyclerView)

        }
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        if (child is ExpandableRecyclerView) {
            child.addItemDecoration(itemDecoration)
        }
    }


    override fun onViewRemoved(child: View) {
        super.onViewRemoved(child)
        if (child is ExpandableRecyclerView) {
            child.removeItemDecoration(itemDecoration)
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

        if (this.header != header) {
            this.header?.let { removeView(it) }
            this.header = header
            val defaultParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            val layoutParams = header.layoutParams?.let(::LayoutParams) ?: defaultParams
            layoutParams.gravity = Gravity.TOP
            addView(header, layoutParams)
        }
        this.header?.y = y
    }


}

class HeaderUpdater(private val onShowHeader: (View, Float) -> Unit) {


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
        .firstOrNull { it.y <= 0 && (it.y + it.height) > 0 }
        ?.let { recyclerView.getChildViewHolder(it) }

    fun updateHeader(recyclerView: ExpandableRecyclerView) {
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
            this.header = adapter.createViewHolder(recyclerView, groupType)
            if (DEBUG) {
                Log.d(LOG_TAG, "onCreateVH $groupType")
//                this.header?.itemView?.setBackgroundColor(Color.RED)
            }
        }

        var hadBindView = false
        // Bind Header
        val headerViewHolder = this.header ?: return
        if (this.headerGroup != firstGroup) {
            val position = adapter.getGroupAdapterPosition(firstGroup)
            adapter.bindViewHolder(headerViewHolder, position)
            this.headerGroup = firstGroup
            hadBindView = true
            if (DEBUG) {
                Log.d(LOG_TAG, "bindViewHolder group:$firstGroup")
            }
        }
        // Calculate position
        val nextGroupView = recyclerView.findGroupViewHolder(firstGroup + 1)?.itemView
        var y = 0f
        if (nextGroupView != null) {
            val itemHeight = headerViewHolder.itemView.height.takeIf { it != 0 }
                ?: headerViewHolder.itemView.measuredHeight
            y = nextGroupView.y - itemHeight
        }

        if (hadBindView) { // 绑定后这一帧，不显示Header
            y = recyclerView.height * -1f
        }
        y = y.coerceAtMost(0f)
        onShowHeader(headerViewHolder.itemView, y)
        if (DEBUG) {
            Log.d(
                LOG_TAG,
                "onGroupShow,group:$headerGroup,y:${y},height:${headerViewHolder.itemView.height},nextGroupView:${nextGroupView}"
            )
        }

    }

}