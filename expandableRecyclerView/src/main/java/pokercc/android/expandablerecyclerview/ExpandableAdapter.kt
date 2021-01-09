@file:Suppress("MemberVisibilityCanBePrivate")

package pokercc.android.expandablerecyclerview

import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.core.util.putAll
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView

/**
 * ExpandableAdapter working with ExpandableRecyclerView
 * @author pokercc
 * @date 2019-6-2 11:38:13
 * */
@UiThread
abstract class ExpandableAdapter<VH : ExpandableAdapter.ViewHolder> : RecyclerView.Adapter<VH>() {
    companion object {
        @Suppress("MayBeConstant")
        var DEBUG = BuildConfig.DEBUG
        private const val LOG_TAG = "ExpandableAdapter"
        private val GROUP_EXPAND_CHANGE = Any()
    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal lateinit var layoutItemPosition: ItemPosition
    }

    data class ItemPosition(var groupPosition: Int, var childPosition: Int?)

    private val tempItemPosition = ItemPosition(0, null)

    private val expandState = SparseBooleanArray()

    var onlyOneGroupExpand = false

    var enableAnimation = true

    private var recyclerView: RecyclerView? = null

    @CallSuper
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        require(recyclerView is ExpandableRecyclerView)
        this.recyclerView = recyclerView
    }

    @CallSuper
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    /**
     * Whether the  special group is expand
     *
     * @param groupPosition
     * @return
     */
    fun isExpand(groupPosition: Int): Boolean {
        val groupCount = getGroupCount()
        require(groupPosition in 0 until groupCount) {
            "$groupPosition must in 0 until $groupCount"
        }
        return expandState[groupPosition]
    }

    private fun setExpand(groupPosition: Int, expand: Boolean) {
        expandState[groupPosition] = expand
        onGroupExpandChange(groupPosition, expand)
        notifyGroupChange(groupPosition, GROUP_EXPAND_CHANGE)
    }


    @Deprecated("deprecated", replaceWith = ReplaceWith("onGroupExpandChange(int, boolean)"))
    protected open fun onGroupExpandChange(
        groupPosition: Int,
        adapterPosition: Int,
        expand: Boolean
    ) = Unit

    protected open fun onGroupExpandChange(
        groupPosition: Int,
        expand: Boolean
    ) {
        @Suppress("DEPRECATION")
        onGroupExpandChange(groupPosition, getGroupAdapterPosition(groupPosition), expand)
    }

    fun expandAllGroup() {
        onlyOneGroupExpand = false
        for (i in 0 until getGroupCount()) {
            expandState[i] = true
        }
        notifyDataSetChanged()
    }

    fun collapseAllGroup() {
        for (i in 0 until getGroupCount()) {
            expandState[i] = false
        }
        notifyDataSetChanged()
    }

    /**
     *
     * @param groupPosition
     * @param anim
     */
    fun expandGroup(groupPosition: Int, anim: Boolean) {
        val groupCount = getGroupCount()
        require(groupPosition in 0 until groupCount) {
            "$groupPosition must in 0 until $groupCount"
        }
        if (!onlyOneGroupExpand) {
            if (!isExpand(groupPosition)) {
                setExpand(groupPosition, true)
                if (anim) {
                    val adapterPosition = getChildAdapterPosition2(groupPosition, 0) ?: return
                    notifyItemRangeInserted(adapterPosition, getChildCount(groupPosition))
                } else {
                    notifyDataSetChanged()
                }
            }
            return
        }

        if (anim) {
            for (i in 0 until getGroupCount()) {
                if (i == groupPosition && !isExpand(i)) {
                    setExpand(i, true)
                    val childAdapterPosition = getChildAdapterPosition2(i, 0) ?: 0
                    notifyItemRangeInserted(childAdapterPosition, getChildCount(i))
                } else if (isExpand(i)) {
                    val childAdapterPosition = getChildAdapterPosition2(i, 0) ?: 0
                    setExpand(i, false)
                    notifyItemRangeRemoved(childAdapterPosition, getChildCount(i))
                }
            }
        } else {
            for (i in 0 until getGroupCount()) {
                if (i == groupPosition && !isExpand(i)) {
                    setExpand(i, true)
                } else if (isExpand(i)) {
                    setExpand(i, false)
                }
            }
            notifyDataSetChanged()
        }

    }

    /**
     *
     * @param groupPosition
     * @param anim
     */
    fun collapseGroup(groupPosition: Int, anim: Boolean) {
        val groupCount = getGroupCount()
        require(groupPosition in 0 until groupCount) {
            "$groupPosition must in 0 until $groupCount"
        }
        if (!isExpand(groupPosition)) return
        val childAdapterPosition = getChildAdapterPosition2(groupPosition, 0) ?: return
        setExpand(groupPosition, false)
        if (anim) {
            notifyItemRangeRemoved(childAdapterPosition, getChildCount(groupPosition))
        } else {
            notifyDataSetChanged()
        }
    }


    fun getGroupAdapterPosition(groupPosition: Int): Int {
        val groupCount = getGroupCount()
        require(groupPosition in 0 until groupCount) {
            "$groupPosition must in 0 until $groupCount"
        }
        var position = groupPosition
        for (i in 0 until groupPosition) {
            if (isExpand(i)) {
                position += getChildCount(i)
            }
        }
        return position
    }


    @Deprecated(
        message = "Unclear return value",
        replaceWith = ReplaceWith("getChildAdapterPosition2")
    )
    fun getChildAdapterPosition(groupPosition: Int, childPosition: Int): Int {
        return getChildAdapterPosition2(groupPosition, childPosition) ?: RecyclerView.NO_POSITION
    }

    /**
     * Get special child adapter position,if the group is not expand return null.
     */
    fun getChildAdapterPosition2(groupPosition: Int, childPosition: Int): Int? {
        return if (!isExpand(groupPosition)) {
            null
        } else {
            val childCount = getChildCount(groupPosition)
            require(childPosition in 0 until childCount) {
                "$childPosition must in 0 until $$childCount"
            }
            getGroupAdapterPosition(groupPosition) + 1 + childPosition
        }
    }

    final override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): VH {
        return if (isGroup(viewType)) {
            onCreateGroupViewHolder(viewGroup, viewType)
        } else {
            onCreateChildViewHolder(viewGroup, viewType)
        }
    }

    final override fun getItemViewType(position: Int): Int {
        require(position in 0 until itemCount) {
            "$position must in 0 unit $itemCount"
        }
        val (groupPosition, childPosition) = getItemAdapterPosition(position)
        return if (childPosition == null) {
            getGroupItemViewType(groupPosition)
        } else {
            getChildItemViewType(groupPosition, childPosition)
        }
    }

    open fun getGroupItemViewType(groupPosition: Int): Int = 1

    open fun getChildItemViewType(groupPosition: Int, childPosition: Int): Int = -1

    open fun isGroup(viewType: Int): Boolean = viewType > 0

    final override fun onBindViewHolder(viewHolder: VH, position: Int) = Unit

    final override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        val itemPosition = getItemAdapterPosition(position)
        holder.layoutItemPosition = itemPosition.copy()
        if (DEBUG) {
            Log.d(LOG_TAG, "onBindViewHolder $tempItemPosition")
        }
        val (groupPosition, childPosition) = itemPosition
        if (childPosition == null) {
            performBindParentViewHolder(groupPosition, holder, payloads)
        } else {
            onBindChildViewHolder(holder, groupPosition, childPosition, payloads)
        }
    }

    private fun performBindParentViewHolder(
        groupPosition: Int,
        holder: VH,
        payloads: List<Any>
    ) {
        val expand = isExpand(groupPosition)
        if (payloads.isEmpty()) {
            holder.itemView.setOnClickListener {
                if (isExpand(groupPosition)) {
                    collapseGroup(groupPosition, enableAnimation)
                } else {
                    expandGroup(groupPosition, enableAnimation)
                }
            }
        }
        onBindGroupViewHolder(holder, groupPosition, expand, payloads)
        if (payloads.any { it == GROUP_EXPAND_CHANGE }) {
            val animDuration =
                recyclerView?.itemAnimator?.let { if (expand) it.addDuration else it.removeDuration }
            onGroupViewHolderExpandChange(holder, groupPosition, animDuration ?: 300, expand)
        }
    }

    fun onSaveInstanceState(): Parcelable {
        return ExpandableState(expandState)
    }

    fun onRestoreInstanceState(state: Parcelable?) {
        (state as? ExpandableState)?.expandState?.let {
            expandState.clear()
            expandState.putAll(it)
        }
    }


    /**
     *
     * Notify any registered observers that the group item at <code>groupPosition</code> has changed with
     * an optional payload object.
     *
     * @param groupPosition
     * @param payload
     */
    fun notifyGroupChange(groupPosition: Int, payload: Any? = null) {
        notifyItemChanged(getGroupAdapterPosition(groupPosition), payload)
    }

    /**
     * Notify any registered observers that the child item at <code>groupPosition,childPosition</code> has changed with
     * an optional payload object.
     *
     * @param groupPosition
     * @param childPosition
     * @param payload
     */
    fun notifyChildChange(
        groupPosition: Int,
        childPosition: Int,
        payload: Any? = null
    ) {
        if (isExpand(groupPosition)) {
            val adapterPosition = getChildAdapterPosition2(groupPosition, childPosition) ?: return
            notifyItemChanged(adapterPosition, payload)
        }
    }

    fun notifyGroupInserted(groupPosition: Int) {
        // FIXME: 2020/12/18 Not strict
        notifyItemInserted(getGroupAdapterPosition(groupPosition))
    }

    fun notifyGroupRangeInserted(range: IntRange) {
        // FIXME: 2020/12/18 Not strict
        notifyItemRangeInserted(getGroupAdapterPosition(range.first), range.last - range.first)
    }

    fun notifyGroupRemove(groupPosition: Int) {
        // FIXME: 2020/12/18 Not strict
        notifyItemRemoved(getGroupAdapterPosition(groupPosition))
    }

    fun notifyGroupRangeRemove(range: IntRange) {
        // FIXME: 2020/12/18 Not strict
        notifyItemRangeRemoved(getGroupAdapterPosition(range.first), range.last - range.first)
    }

    fun notifyChildInserted(groupPosition: Int, childPosition: Int) {
        if (isExpand(groupPosition)) {
            val adapterPosition = getChildAdapterPosition2(groupPosition, childPosition) ?: return
            notifyItemInserted(adapterPosition)
        }
    }

    fun notifyChildRangeInserted(groupPosition: Int, range: IntRange) {
        if (isExpand(groupPosition)) {
            val adapterPosition = getChildAdapterPosition2(groupPosition, range.first) ?: return
            notifyItemRangeInserted(adapterPosition, range.last - range.first)
        }
    }

    fun notifyGroupMove(fromGroupPosition: Int, toGroupPosition: Int) {
        // FIXME: 2020/12/18 Not strict
        notifyItemMoved(
            getGroupAdapterPosition(fromGroupPosition),
            getGroupAdapterPosition(toGroupPosition)
        )
    }

    fun notifyChildMove(groupPosition: Int, fromChildPosition: Int, toChildPosition: Int) {
        if (isExpand(groupPosition)) {
            val startPosition = getChildAdapterPosition2(groupPosition, fromChildPosition) ?: return
            val endPosition = getChildAdapterPosition2(groupPosition, toChildPosition) ?: return
            notifyItemMoved(startPosition, endPosition)
        }
    }

    fun notifyChildRemove(groupPosition: Int, childPosition: Int) {
        if (isExpand(groupPosition)) {
            val adapterPosition = getChildAdapterPosition2(groupPosition, childPosition) ?: return
            notifyItemRemoved(adapterPosition)
        }
    }

    fun notifyChildRangeRemove(groupPosition: Int, range: IntRange) {
        if (isExpand(groupPosition)) {
            val adapterPosition = getChildAdapterPosition2(groupPosition, range.first) ?: return
            notifyItemRangeRemoved(adapterPosition, range.last - range.first)
        }
    }

    /**
     * Clear expand state ,working with notifyDataSetChange()
     */
    fun clearExpandState() {
        expandState.clear()
    }

    final override fun getItemCount(): Int {
        var itemCount = 0
        for (g in 0 until getGroupCount()) {
            itemCount++
            if (isExpand(g)) {
                itemCount += getChildCount(g)
            }
        }
        return itemCount
    }


    protected abstract fun onCreateGroupViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): VH

    protected abstract fun onCreateChildViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): VH


    protected abstract fun onBindChildViewHolder(
        holder: VH,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    )

    protected abstract fun onBindGroupViewHolder(
        holder: VH,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    )

    protected abstract fun onGroupViewHolderExpandChange(
        holder: VH,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    )

    abstract fun getGroupCount(): Int

    abstract fun getChildCount(groupPosition: Int): Int

    fun getItemLayoutPosition(viewHolder: RecyclerView.ViewHolder): ItemPosition =
        (viewHolder as ViewHolder).layoutItemPosition

    fun getItemAdapterPosition(viewHolder: RecyclerView.ViewHolder): ItemPosition? {
        return if (viewHolder.adapterPosition == RecyclerView.NO_POSITION) {
            null
        } else {
            getItemAdapterPosition(viewHolder.adapterPosition)
        }
    }

    fun getItemAdapterPosition(adapterPosition: Int): ItemPosition {
        require(Looper.myLooper() == Looper.getMainLooper()) {
            "Must run on ui thread"
        }
        require(adapterPosition in 0 until itemCount) {
            "$adapterPosition must in 0 unit $itemCount"
        }
        tempItemPosition.groupPosition = RecyclerView.NO_POSITION
        tempItemPosition.childPosition = null
        var position = -1
        A@ for (g in 0 until getGroupCount()) {
            position++
            if (position == adapterPosition) {
                tempItemPosition.groupPosition = g
                tempItemPosition.childPosition = null
                break@A
            }
            if (!isExpand(g)) continue
            for (c in 0 until getChildCount(g)) {
                position++
                if (position == adapterPosition) {
                    tempItemPosition.groupPosition = g
                    tempItemPosition.childPosition = c
                    break@A
                }
            }
        }
        return tempItemPosition
    }

    /**
     *
     *
     * @param viewHolder
     * @return
     */
    @Deprecated(
        message = "Mix adapterPosition and layoutPosition",
        replaceWith = ReplaceWith("getItemAdapterPosition() or getItemLayoutPosition()")
    )
    fun getChildPosition(viewHolder: RecyclerView.ViewHolder): Int {
        return getItemLayoutPosition(viewHolder).childPosition ?: RecyclerView.NO_POSITION
    }

    /**
     *
     * @param viewHolder
     * @return
     */
    @Deprecated(
        message = "Mix adapterPosition and layoutPosition",
        replaceWith = ReplaceWith("getItemAdapterPosition() or getItemLayoutPosition()")
    )
    fun getGroupPosition(viewHolder: RecyclerView.ViewHolder): Int {
        return getItemLayoutPosition(viewHolder).groupPosition
    }

    class ExpandableState(var expandState: SparseBooleanArray?) : Parcelable {

        constructor(parcel: Parcel) : this(parcel.readSparseBooleanArray())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeSparseBooleanArray(expandState)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<ExpandableState> {
            override fun createFromParcel(parcel: Parcel): ExpandableState {
                return ExpandableState(parcel)
            }

            override fun newArray(size: Int): Array<ExpandableState?> {
                return arrayOfNulls(size)
            }
        }
    }
}

