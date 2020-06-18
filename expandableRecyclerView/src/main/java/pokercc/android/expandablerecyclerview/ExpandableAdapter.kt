package pokercc.android.expandablerecyclerview

import android.os.Looper
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.*

/**
 * 可展开的数据适配器
 *
 * @param <Parent>
 * @param <Children>
 * @author pokercc
 * @date 2019-6-2 11:38:13
</Children></Parent> */
abstract class ExpandableAdapter<Parent, Children> protected constructor(groups: List<Group<Parent, Children>>?) :
    RecyclerView.Adapter<ViewHolder>() {
    companion object {
        const val GROUP_VIEW_TYPE = 100
        const val CHILDREN_VIEW_TYPE = GROUP_VIEW_TYPE + 1
        const val GROUP_IS_EXPAND_FLAG = 3 shl 24
        const val GROUP_INDEX_FLAG = 3 shl 24 + 1
        const val CHILDREN_INDEX_FLAG = 3 shl 24 + 2
        private val GROUP_EXPAND_CHANGE = Any()
    }

    class Group<Parent, Children>(val parent: Parent, val children: List<Children>) {
        var expanded = false
        val viewHolderCount: Int get() = if (expanded) children.size + 1 else 1
    }

    private sealed class RecyclerViewItem(
        val viewType: Int,
        val groupPosition: Int,
        val childPosition: Int
    ) {
        class Parent<T>(val data: T, groupPosition: Int) :
            RecyclerViewItem(GROUP_VIEW_TYPE, groupPosition, -1)

        class Children<T>(val data: T, groupPosition: Int, childPosition: Int) :
            RecyclerViewItem(CHILDREN_VIEW_TYPE, groupPosition, childPosition)
    }

    private val groups: List<Group<Parent, Children>> = groups ?: emptyList()
    private val recyclerViewItemList: MutableList<RecyclerViewItem> = ArrayList()
    private var onlyOneGroupExpand = false

    init {
        setDataInternal()
    }

    /**
     * 设置只展开一个group
     *
     * @param onlyOneGroupExpand
     */
    fun setOnlyOneGroupExpand(onlyOneGroupExpand: Boolean) {
        this.onlyOneGroupExpand = onlyOneGroupExpand
    }

    private val expandableItemAnimator by lazy { ExpandableItemAnimator(this) }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator = expandableItemAnimator
    }

    /**
     * 某个group是否展开
     *
     * @param groupPosition
     * @return
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun isExpand(groupPosition: Int): Boolean {
        return groups[groupPosition].expanded
    }

    private fun setDataInternal() {
        check(Looper.myLooper() == Looper.getMainLooper()) { "must execute on main thread" }
        recyclerViewItemList.clear()
        for (i in groups.indices) {
            val group =
                groups[i]
            recyclerViewItemList.add(RecyclerViewItem.Parent(group.parent, i))
            if (group.expanded) {
                for (j in group.children.indices) {
                    recyclerViewItemList.add(RecyclerViewItem.Children(group.children[j], i, j))
                }
            }
        }
    }

    private fun performGroupExpandChange(group: Int, expand: Boolean) {
        val groupLayoutPosition = getGroupLayoutPosition(group)
        onGroupExpandChange(group, groupLayoutPosition, expand)
        notifyGroupChange(group, GROUP_EXPAND_CHANGE)
    }

    open fun onGroupExpandChange(
        group: Int,
        adapterPosition: Int,
        expand: Boolean
    ) {
    }

    /**
     * 通知group布局刷新
     *
     * @param groupPosition
     * @param refreshToken
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun notifyGroupChange(groupPosition: Int, refreshToken: Any?) {
        notifyItemChanged(getGroupLayoutPosition(groupPosition), refreshToken)
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
        val item =
            groups[groupPosition]
        if (item.expanded) {
            notifyItemChanged(
                getChildrenLayoutPosition(groupPosition, childrenPosition),
                refreshToken
            )
        }
    }

    /**
     * 展开一个group
     *
     * @param group
     * @param anim
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun expandGroup(group: Int, anim: Boolean) {
        if (onlyOneGroupExpand) {
            for (i in groups.indices) {
                val item =
                    groups[i]
                if (i == group && !item.expanded) {
                    item.expanded = true
                    // 改变数据源布局刷新
                    setDataInternal()
                    performGroupExpandChange(i, true)
                    if (anim) {
                        notifyItemRangeInserted(
                            getGroupLayoutPosition(group) + 1,
                            item.children.size
                        )
                    } else {
                        notifyDataSetChanged()
                    }
                } else if (item.expanded) {
                    item.expanded = false
                    // 改变数据源布局刷新
                    setDataInternal()
                    performGroupExpandChange(i, false)
                    if (anim) {
                        notifyItemRangeRemoved(getGroupLayoutPosition(i) + 1, item.children.size)
                    } else {
                        notifyDataSetChanged()
                    }
                }
            }
        } else {
            val item =
                groups[group]
            if (!item.expanded) {
                item.expanded = true
                // 改变数据源布局刷新
                setDataInternal()
                performGroupExpandChange(group, true)
                if (anim) {
                    notifyItemRangeInserted(getGroupLayoutPosition(group) + 1, item.children.size)
                } else {
                    notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * 折叠一个group
     *
     * @param group
     * @param anim
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun collapseGroup(group: Int, anim: Boolean) {
        val item =
            groups[group]
        if (item.expanded) {
            item.expanded = false
            // 改变数据源布局刷新
            setDataInternal()
            if (anim) {
                notifyItemRangeRemoved(getGroupLayoutPosition(group) + 1, item.children.size)
            } else {
                notifyDataSetChanged()
            }
            performGroupExpandChange(group, false)
        }
    }

    private fun getGroupLayoutPosition(groupPosition: Int): Int {
        var layoutPosition = 0
        val itemData =
            groups.subList(0, groupPosition)
        for (item in itemData) {
            layoutPosition += item.viewHolderCount
        }
        return layoutPosition
    }

    private fun getChildrenLayoutPosition(groupPosition: Int, childrenPosition: Int): Int {
        return getGroupLayoutPosition(groupPosition) + 1 + childrenPosition
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == GROUP_VIEW_TYPE) {
            val parentViewHolder = onCreateParentViewHolder(viewGroup)
            parentViewHolder.itemView.z = 1f
            parentViewHolder
        } else {
            //            childrenViewHolder.itemView.setZ(100);
            onCreateChildrenViewHolder(viewGroup)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return recyclerViewItemList[position].viewType
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        throw UnsupportedOperationException("not implementation")
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        val item =
            recyclerViewItemList[holder.adapterPosition]
        holder.itemView.setTag(
            GROUP_INDEX_FLAG,
            item.groupPosition
        )
        holder.itemView.setTag(
            CHILDREN_INDEX_FLAG,
            item.childPosition
        )
        if (getItemViewType(holder.adapterPosition) == GROUP_VIEW_TYPE) {
            @Suppress("UNCHECKED_CAST")
            performBindParentViewHolder(item as RecyclerViewItem.Parent<Parent>, holder, payloads)
        } else {
            @Suppress("UNCHECKED_CAST")
            onBindChildrenViewHolder(
                holder,
                (item as RecyclerViewItem.Children<Children>).data,
                item.groupPosition,
                item.childPosition,
                payloads
            )

        }
    }

    private fun performBindParentViewHolder(
        recyclerViewItem: RecyclerViewItem.Parent<Parent>,
        holder: ViewHolder,
        payloads: List<Any>
    ) {
        val expand = isExpand(recyclerViewItem.groupPosition)
        onBindParentViewHolder(
            holder,
            recyclerViewItem.data,
            recyclerViewItem.groupPosition,
            expand,
            payloads
        )
        if (payloads.isEmpty()) {
            holder.itemView.setTag(GROUP_IS_EXPAND_FLAG, expand)
            holder.itemView.setOnClickListener {
                if (isExpand(recyclerViewItem.groupPosition)) {
                    collapseGroup(recyclerViewItem.groupPosition, true)
                } else {
                    expandGroup(recyclerViewItem.groupPosition, true)
                }
            }
        }
        for (payload in payloads) {
            if (GROUP_EXPAND_CHANGE === payload) {
                holder.itemView.setTag(
                    GROUP_IS_EXPAND_FLAG,
                    expand
                )
                val animDuration =
                    if (expand) expandableItemAnimator.addDuration else expandableItemAnimator.removeDuration
                onParentViewHolderExpandChange(
                    holder,
                    recyclerViewItem.data,
                    recyclerViewItem.groupPosition,
                    animDuration,
                    expand
                )
            }
        }
    }


    protected abstract fun onCreateParentViewHolder(viewGroup: ViewGroup): ViewHolder
    protected abstract fun onCreateChildrenViewHolder(viewGroup: ViewGroup): ViewHolder

    protected abstract fun onBindChildrenViewHolder(
        holder: ViewHolder,
        children: Children,
        groupPosition: Int,
        childrenPosition: Int,
        payloads: List<Any>
    )


    protected abstract fun onBindParentViewHolder(
        holder: ViewHolder,
        parent: Parent,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    )

    protected abstract fun onParentViewHolderExpandChange(
        holder: ViewHolder,
        parent: Parent,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    )

    override fun getItemCount(): Int {
        return recyclerViewItemList.size
    }

    val groupCount: Int get() = groups.size

    fun getChildrenCount(groupIndex: Int): Int {
        return groups[groupIndex].children.size
    }

    /**
     * 获取组index
     *
     * @param viewHolder
     * @return
     */
    fun getGroupIndex(viewHolder: ViewHolder): Int =
        viewHolder.itemView.getTag(GROUP_INDEX_FLAG)?.let { it as? Int }
            ?: RecyclerView.NO_POSITION


    /**
     * 获取children index
     *
     * @param viewHolder
     * @return
     */
    fun getChildrenIndex(viewHolder: ViewHolder): Int =
        viewHolder.itemView.getTag(CHILDREN_INDEX_FLAG)?.let { it as? Int }
            ?: RecyclerView.NO_POSITION


}