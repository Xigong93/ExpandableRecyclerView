package pokercc.android.expandablerecyclerview

import android.os.Looper
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.*
import kotlin.collections.HashMap

/**
 * 可展开的数据适配器
 * @author pokercc
 * @date 2019-6-2 11:38:13
 * */
abstract class ExpandableAdapter<VH : ViewHolder> : RecyclerView.Adapter<VH>() {
    companion object {
        const val GROUP_IS_EXPAND_FLAG = 3 shl 24
        const val GROUP_INDEX_FLAG = 3 shl 24 + 1
        const val CHILD_INDEX_FLAG = 3 shl 24 + 2
        private val GROUP_EXPAND_CHANGE = Any()
    }

    private sealed class RealItem {
        class Parent(val groupPosition: Int) : RealItem()

        class Child(
            val groupPosition: Int,
            val childPosition: Int
        ) : RealItem()
    }

    private val items: MutableList<RealItem> = ArrayList()
    private val groupItems: MutableList<RealItem.Parent> = ArrayList()
    private val expandState = HashMap<Int, Boolean>()

    /**
     * 设置只展开一个group
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var onlyOneGroupExpand = false

    /** 是否开启展开动画 */
    @Suppress("MemberVisibilityCanBePrivate")
    var enableAnimation = true


    private val expandableItemAnimator by lazy { ExpandableItemAnimator(this) }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator = expandableItemAnimator
        setDataInternal()
    }

    /**
     * 某个group是否展开
     *
     * @param groupPosition
     * @return
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun isExpand(groupPosition: Int): Boolean {
        return expandState[groupPosition] ?: false
    }

    private fun setExpand(groupPosition: Int, expand: Boolean) {
        expandState[groupPosition] = expand
    }

    private fun setDataInternal() {
        check(Looper.myLooper() == Looper.getMainLooper()) { "must execute on main thread" }
        this.items.clear()
        this.groupItems.clear()
        for (i in 0 until getGroupCount()) {
            val parent = RealItem.Parent(i)
            items.add(parent)
            groupItems.add(parent)
            if (!isExpand(i)) continue
            for (j in 0 until getChildCount(i)) {
                items.add(RealItem.Child(i, j))
            }
        }
    }

    private fun performGroupExpandChange(groupPosition: Int, expand: Boolean) {
        val groupLayoutPosition = getGroupAdapterPosition(groupPosition)
        onGroupExpandChange(groupPosition, groupLayoutPosition, expand)
        notifyGroupChange(groupPosition, GROUP_EXPAND_CHANGE)
    }

    protected open fun onGroupExpandChange(
        groupPosition: Int,
        adapterPosition: Int,
        expand: Boolean
    ) = Unit

    /**
     * 通知group布局刷新
     *
     * @param groupPosition
     * @param refreshToken
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun notifyGroupChange(groupPosition: Int, refreshToken: Any?) {
        notifyItemChanged(getGroupAdapterPosition(groupPosition), refreshToken)
    }

    /**
     * 通知子item局部刷新
     *
     * @param groupPosition
     * @param childPosition
     * @param refreshToken
     */
    fun notifyChildChange(
        groupPosition: Int,
        childPosition: Int,
        refreshToken: Any?
    ) {
        if (isExpand(groupPosition)) {
            notifyItemChanged(
                getChildAdapterPosition(groupPosition, childPosition),
                refreshToken
            )
        }
    }

    /**
     * 展开一个group
     *
     * @param groupPosition
     * @param anim
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun expandGroup(groupPosition: Int, anim: Boolean) {
        if (onlyOneGroupExpand) {
            for (i in 0 until getGroupCount()) {
                val item = groupItems[i]
                if (i == groupPosition && !isExpand(i)) {
                    setExpand(i, true)
                    // 改变数据源布局刷新
                    setDataInternal()
                    performGroupExpandChange(i, true)
                    if (anim) {
                        notifyItemRangeInserted(
                            getGroupAdapterPosition(groupPosition) + 1,
                            getChildCount(item.groupPosition)
                        )
                    } else {
                        notifyDataSetChanged()
                    }
                } else if (isExpand(i)) {
                    setExpand(i, false)
                    // 改变数据源布局刷新
                    setDataInternal()
                    performGroupExpandChange(i, false)
                    if (anim) {
                        notifyItemRangeRemoved(
                            getGroupAdapterPosition(i) + 1, getChildCount(item.groupPosition)
                        )
                    } else {
                        notifyDataSetChanged()
                    }
                }
            }
        } else {
            val item = groupItems[groupPosition]
            if (!isExpand(groupPosition)) {
                setExpand(groupPosition, true)
                // 改变数据源布局刷新
                setDataInternal()
                performGroupExpandChange(groupPosition, true)
                if (anim) {
                    notifyItemRangeInserted(
                        getGroupAdapterPosition(groupPosition) + 1,
                        getChildCount(item.groupPosition)
                    )
                } else {
                    notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * 折叠一个group
     *
     * @param groupPosition
     * @param anim
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun collapseGroup(groupPosition: Int, anim: Boolean) {
        val item = groupItems[groupPosition]
        if (isExpand(groupPosition)) {
            setExpand(groupPosition, false)
            // 改变数据源布局刷新
            setDataInternal()
            if (anim) {
                notifyItemRangeRemoved(
                    getGroupAdapterPosition(groupPosition) + 1,
                    getChildCount(item.groupPosition)
                )
            } else {
                notifyDataSetChanged()
            }
            performGroupExpandChange(groupPosition, false)
        }
    }

    private fun getGroupAdapterPosition(groupPosition: Int): Int {
        var position = 0
        for (i in 0 until groupPosition) {
            position++
            if (isExpand(i)) {
                position += getChildCount(i)
            }
        }
        return position
    }

    private fun getChildAdapterPosition(groupPosition: Int, childPosition: Int): Int {
        return getGroupAdapterPosition(groupPosition) + 1 + childPosition
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): VH {
        return if (isGroup(viewType)) {
            val parentViewHolder = onCreateGroupViewHolder(viewGroup, viewType)
            parentViewHolder.itemView.z = 1f
            parentViewHolder
        } else {
            onCreateChildViewHolder(viewGroup, viewType)
        }
    }

    final override fun getItemViewType(position: Int): Int {
        val realItem = items[position]
        return if (realItem is RealItem.Parent) {
            getGroupItemViewType(realItem.groupPosition)
        } else {
            getChildItemViewType(
                (realItem as RealItem.Child).groupPosition,
                realItem.childPosition
            )
        }
    }

    open fun getGroupItemViewType(groupPosition: Int): Int = 1
    open fun getChildItemViewType(groupPosition: Int, childPosition: Int): Int = -1
    open fun isGroup(viewType: Int): Boolean = viewType > 0

    override fun onBindViewHolder(viewHolder: VH, position: Int) = Unit

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        payloads: List<Any>
    ) {
        val item = items[position]
        if (isGroup(getItemViewType(position))) {
            val parent = item as RealItem.Parent
            holder.itemView.setTag(
                GROUP_INDEX_FLAG,
                parent.groupPosition
            )
            performBindParentViewHolder(item, holder, payloads)
        } else {
            val child = item as RealItem.Child
            holder.itemView.setTag(
                GROUP_INDEX_FLAG,
                child.groupPosition
            )

            holder.itemView.setTag(
                CHILD_INDEX_FLAG,
                item.childPosition
            )
            onBindChildViewHolder(
                holder,
                item.groupPosition,
                item.childPosition,
                payloads
            )

        }
        onBindViewHolder(holder, position)
    }

    private fun performBindParentViewHolder(
        realItem: RealItem.Parent,
        holder: VH,
        payloads: List<Any>
    ) {
        val expand = isExpand(realItem.groupPosition)
        onBindGroupViewHolder(
            holder,
            realItem.groupPosition,
            expand,
            payloads
        )
        if (payloads.isEmpty()) {
            holder.itemView.setTag(GROUP_IS_EXPAND_FLAG, expand)
            holder.itemView.setOnClickListener {
                if (isExpand(realItem.groupPosition)) {
                    collapseGroup(realItem.groupPosition, enableAnimation)
                } else {
                    expandGroup(realItem.groupPosition, enableAnimation)
                }
            }
        }
        for (payload in payloads) {
            if (GROUP_EXPAND_CHANGE === payload) {
                holder.itemView.setTag(
                    GROUP_IS_EXPAND_FLAG,
                    expand
                )
                val animDuration = if (expand) {
                    expandableItemAnimator.addDuration
                } else {
                    expandableItemAnimator.removeDuration
                }
                onGroupViewHolderExpandChange(
                    holder,
                    realItem.groupPosition,
                    animDuration,
                    expand
                )
            }
        }
    }


    final override fun getItemCount(): Int {
        return items.size
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

    /**
     * 获取组index
     *
     * @param viewHolder
     * @return
     */
    fun getGroupPosition(viewHolder: ViewHolder): Int =
        viewHolder.itemView.getTag(GROUP_INDEX_FLAG)?.let { it as? Int }
            ?: RecyclerView.NO_POSITION


    /**
     * 获取child index
     *
     * @param viewHolder
     * @return
     */
    fun getChildPosition(viewHolder: ViewHolder): Int =
        viewHolder.itemView.getTag(CHILD_INDEX_FLAG)?.let { it as? Int }
            ?: RecyclerView.NO_POSITION


}