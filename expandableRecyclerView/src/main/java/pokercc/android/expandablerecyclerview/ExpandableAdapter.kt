package pokercc.android.expandablerecyclerview

import android.os.Looper
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.*
import kotlin.collections.HashMap

/**
 * 可展开的数据适配器
 *
 * @param <Parent>
 * @param <Children>
 * @author pokercc
 * @date 2019-6-2 11:38:13
</Children></Parent> */
abstract class ExpandableAdapter<VH : ViewHolder> : RecyclerView.Adapter<VH>() {
    companion object {
        const val GROUP_VIEW_TYPE = 100
        const val CHILDREN_VIEW_TYPE = GROUP_VIEW_TYPE + 1
        const val GROUP_IS_EXPAND_FLAG = 3 shl 24
        const val GROUP_INDEX_FLAG = 3 shl 24 + 1
        const val CHILDREN_INDEX_FLAG = 3 shl 24 + 2
        private val GROUP_EXPAND_CHANGE = Any()
    }

    private sealed class RecyclerViewItem(
        val viewType: Int
    ) {
        class Parent(val groupPosition: Int) :
            RecyclerViewItem(GROUP_VIEW_TYPE)

        class Children(
            val groupPosition: Int,
            val childPosition: Int
        ) : RecyclerViewItem(CHILDREN_VIEW_TYPE)
    }

    private val recyclerViewItemList: MutableList<RecyclerViewItem> = ArrayList()
    private val groupItemList: MutableList<RecyclerViewItem.Parent> = ArrayList()
    private val expandState = HashMap<Int, Boolean>()

    /**
     * 设置只展开一个group
     */
    var onlyOneGroupExpand = false

    /** 是否开启展开动画 */
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
        this.recyclerViewItemList.clear()
        this.groupItemList.clear()
        for (i in 0 until getGroupCount()) {
            val parent = RecyclerViewItem.Parent(i)
            recyclerViewItemList.add(parent)
            groupItemList.add(parent)
            if (!isExpand(i)) continue
            for (j in 0 until getChildrenCount(i)) {
                recyclerViewItemList.add(RecyclerViewItem.Children(i, j))
            }
        }
    }

    private fun performGroupExpandChange(groupPosition: Int, expand: Boolean) {
        val groupLayoutPosition = getGroupAdapterPosition(groupPosition)
        onGroupExpandChange(groupPosition, groupLayoutPosition, expand)
        notifyGroupChange(groupPosition, GROUP_EXPAND_CHANGE)
    }

    open fun onGroupExpandChange(
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
     * @param childrenPosition
     * @param refreshToken
     */
    fun notifyChildrenChange(
        groupPosition: Int,
        childrenPosition: Int,
        refreshToken: Any?
    ) {
        if (isExpand(groupPosition)) {
            notifyItemChanged(
                getChildrenAdapterPosition(groupPosition, childrenPosition),
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
                val item = groupItemList[i]
                if (i == groupPosition && !isExpand(i)) {
                    setExpand(i, true)
                    // 改变数据源布局刷新
                    setDataInternal()
                    performGroupExpandChange(i, true)
                    if (anim) {
                        notifyItemRangeInserted(
                            getGroupAdapterPosition(groupPosition) + 1,
                            getChildrenCount(item.groupPosition)
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
                            getGroupAdapterPosition(i) + 1, getChildrenCount(item.groupPosition)
                        )
                    } else {
                        notifyDataSetChanged()
                    }
                }
            }
        } else {
            val item = groupItemList[groupPosition]
            if (!isExpand(groupPosition)) {
                setExpand(groupPosition, true)
                // 改变数据源布局刷新
                setDataInternal()
                performGroupExpandChange(groupPosition, true)
                if (anim) {
                    notifyItemRangeInserted(
                        getGroupAdapterPosition(groupPosition) + 1,
                        getChildrenCount(item.groupPosition)
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
        val item = groupItemList[groupPosition]
        if (isExpand(groupPosition)) {
            setExpand(groupPosition, false)
            // 改变数据源布局刷新
            setDataInternal()
            if (anim) {
                notifyItemRangeRemoved(
                    getGroupAdapterPosition(groupPosition) + 1,
                    getChildrenCount(item.groupPosition)
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
                position += getChildrenCount(i)
            }
        }
        return position
    }

    private fun getChildrenAdapterPosition(groupPosition: Int, childrenPosition: Int): Int {
        return getGroupAdapterPosition(groupPosition) + 1 + childrenPosition
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): VH {
        return if (isGroup(viewType)) {
            val parentViewHolder = onCreateParentViewHolder(viewGroup, viewType)
            parentViewHolder.itemView.z = 1f
            parentViewHolder
        } else {
            //            childrenViewHolder.itemView.setZ(100);
            onCreateChildrenViewHolder(viewGroup, viewType)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return recyclerViewItemList[position].viewType
    }


    override fun onBindViewHolder(viewHolder: VH, position: Int) = Unit

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        payloads: List<Any>
    ) {
        val item = recyclerViewItemList[position]
        if (isGroup(getItemViewType(position))) {
            val parent = item as RecyclerViewItem.Parent
            holder.itemView.setTag(
                GROUP_INDEX_FLAG,
                parent.groupPosition
            )
            performBindParentViewHolder(item, holder, payloads)
        } else {
            val children = item as RecyclerViewItem.Children
            holder.itemView.setTag(
                GROUP_INDEX_FLAG,
                children.groupPosition
            )

            holder.itemView.setTag(
                CHILDREN_INDEX_FLAG,
                item.childPosition
            )
            onBindChildrenViewHolder(
                holder,
                item.groupPosition,
                item.childPosition,
                payloads
            )

        }
        onBindViewHolder(holder, position)
    }

    private fun performBindParentViewHolder(
        recyclerViewItem: RecyclerViewItem.Parent,
        holder: VH,
        payloads: List<Any>
    ) {
        val expand = isExpand(recyclerViewItem.groupPosition)
        onBindParentViewHolder(
            holder,
            recyclerViewItem.groupPosition,
            expand,
            payloads
        )
        if (payloads.isEmpty()) {
            holder.itemView.setTag(GROUP_IS_EXPAND_FLAG, expand)
            holder.itemView.setOnClickListener {
                if (isExpand(recyclerViewItem.groupPosition)) {
                    collapseGroup(recyclerViewItem.groupPosition, enableAnimation)
                } else {
                    expandGroup(recyclerViewItem.groupPosition, enableAnimation)
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
                onParentViewHolderExpandChange(
                    holder,
                    recyclerViewItem.groupPosition,
                    animDuration,
                    expand
                )
            }
        }
    }


    final override fun getItemCount(): Int {
        return recyclerViewItemList.size
    }

    open fun isGroup(viewType: Int): Boolean = viewType == GROUP_VIEW_TYPE

    protected abstract fun onCreateParentViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): VH

    protected abstract fun onCreateChildrenViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): VH


    protected abstract fun onBindChildrenViewHolder(
        holder: VH,
        groupPosition: Int,
        childrenPosition: Int,
        payloads: List<Any>
    )

    protected abstract fun onBindParentViewHolder(
        holder: VH,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    )

    protected abstract fun onParentViewHolderExpandChange(
        holder: VH,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    )


    abstract fun getGroupCount(): Int
    abstract fun getChildrenCount(groupPosition: Int): Int

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
     * 获取children index
     *
     * @param viewHolder
     * @return
     */
    fun getChildrenPosition(viewHolder: ViewHolder): Int =
        viewHolder.itemView.getTag(CHILDREN_INDEX_FLAG)?.let { it as? Int }
            ?: RecyclerView.NO_POSITION


}