package pokercc.android.expandablerecyclerview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderRecyclerViewContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), StickyHeaderDecoration.Callback {

    private val stickyHeaderDecoration = StickyHeaderDecoration(this)
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

    override fun onGroupShow(
        offset: Float,
        recyclerView: RecyclerView,
        viewHolder: ExpandableAdapter.ViewHolder,
        expandableAdapter: ExpandableAdapter<ExpandableAdapter.ViewHolder>
    ) {
        val itemView = viewHolder.itemView
        header?.isVisible = true
        if (header == itemView) {
            itemView.translationY = offset
            return
        }
        header?.let { removeView(it) }
        header = itemView
        val layoutParams = if (itemView.layoutParams != null) {
            LayoutParams(itemView.layoutParams)
        } else {
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }.apply {
            gravity = Gravity.TOP
        }
        addView(header, layoutParams)
    }

    override fun onGroupHide() {
        header?.isVisible = false
    }


}

/**
 * 固定头的装饰器
 */
private class StickyHeaderDecoration(private val callback: Callback) :
    RecyclerView.ItemDecoration() {
    companion object {
        private const val LOG_TAG = "ExpandStickyHeaderD"
    }


    private var headerGroupPosition = -1
    private var headerViewType: Int? = null
    private var headerViewHolder: ExpandableAdapter.ViewHolder? = null
    private var expandableAdapter: ExpandableAdapter<ExpandableAdapter.ViewHolder>? = null
    private var changeObservable = object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            if (headerGroupPosition in positionStart..(positionStart + itemCount)) {
                val viewHolder = headerViewHolder ?: return
                val payloads = if (payload != null) listOf(payload) else emptyList()
                expandableAdapter?.onBindViewHolder(
                    viewHolder,
                    headerGroupPosition,
                    payloads.toMutableList()
                )
            }

        }
    }

    override fun onDraw(c: Canvas, p: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, p, state)
        val expandableRecyclerView = p as? ExpandableRecyclerView ?: return
        val expandableAdapter = p.adapter as? ExpandableAdapter<ExpandableAdapter.ViewHolder> ?: return
        if (this.expandableAdapter != expandableAdapter) {
            this.expandableAdapter?.unregisterAdapterDataObserver(changeObservable)
            expandableAdapter.registerAdapterDataObserver(changeObservable)
            this.expandableAdapter = expandableAdapter
            headerViewHolder = null
        }
        if (expandableRecyclerView.isEmpty()) return
        val viewHolder = expandableRecyclerView.getChildViewHolder(expandableRecyclerView[0])
        val groupPosition = expandableAdapter.getItemLayoutPosition(viewHolder).groupPosition
        val animationRunning = state.willRunPredictiveAnimations()
                || state.willRunSimpleAnimations()
                || viewHolder.itemView.hasTransientState()
                || expandableRecyclerView.isAnimating
        if (ExpandableAdapter.DEBUG) {
            Log.d(LOG_TAG, "animationRunning:${animationRunning}")
        }

        if (!animationRunning && !expandableAdapter.isExpand(groupPosition)) {
            callback.onGroupHide()
            if (ExpandableAdapter.DEBUG) {
                Log.d(LOG_TAG, "onGroupHide1")
            }
            return
        }
        if (expandableAdapter.isExpand(groupPosition)
            && expandableAdapter.getChildCount(groupPosition) > 0
        ) {
            val groupItemViewType = expandableAdapter.getGroupItemViewType(groupPosition)
            // 创建ViewHolder
            if (this.headerViewHolder == null || this.headerViewType != groupItemViewType) {
                this.headerGroupPosition = -1
                this.headerViewType = groupItemViewType
                this.headerViewHolder =
                    expandableAdapter.onCreateViewHolder(expandableRecyclerView, groupItemViewType)
            }

            // 绑定ViewHolder
            val headerViewHolder = this.headerViewHolder!!
            if (this.headerGroupPosition != groupPosition) {
                expandableAdapter.onBindViewHolder(
                    headerViewHolder, expandableAdapter.getGroupAdapterPosition(groupPosition),
                    ArrayList()
                )
                this.headerGroupPosition = groupPosition
            }


            // 计算偏移量
            val nextGroupView =
                expandableRecyclerView.findGroupViewHolder(groupPosition + 1)?.itemView
            var offset = 0f
            if (nextGroupView != null) {
                offset = nextGroupView.y - headerViewHolder.itemView.height
                offset = minOf(offset, 0f)
            }
            callback.onGroupShow(
                offset,
                expandableRecyclerView,
                headerViewHolder,
                expandableAdapter
            )
            if (ExpandableAdapter.DEBUG) {
                Log.d(LOG_TAG, "onGroupShow,offset:${offset}")
            }
        }

        // 隐藏Header
        val groupView = expandableRecyclerView.findGroupViewHolder(headerGroupPosition)?.itemView
        if (groupView != null && (groupView.y) > 0) {
            callback.onGroupHide()
            if (ExpandableAdapter.DEBUG) {
                Log.d(LOG_TAG, "onGroupHide2")
            }
        }
    }


    interface Callback {
        fun onGroupShow(
            offset: Float,
            recyclerView: RecyclerView,
            viewHolder: ExpandableAdapter.ViewHolder,
            expandableAdapter: ExpandableAdapter<ExpandableAdapter.ViewHolder>
        )

        fun onGroupHide()
    }
}