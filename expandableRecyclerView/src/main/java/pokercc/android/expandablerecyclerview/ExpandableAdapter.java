package pokercc.android.expandablerecyclerview;

import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 可展开的数据适配器
 *
 * @param <Parent>
 * @param <Children>
 * @author pokercc
 * @date 2019-6-2 11:38:13
 */
public abstract class ExpandableAdapter<Parent, Children> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int GROUP_VIEW_TYPE = 1;
    static final int CHILDREN_VIEW_TYPE = 2;
    static final int GROUP_IS_EXPAND_FLAG = 3 << 24;
    static final int GROUP_INDEX_FLAG = 3 << 24 + 1;
    static final int CHILDREN_INDEX_FLAG = 3 << 24 + 2;
    private static final Object GROUP_EXPAND_CHANGE = new Object();

    public static class Group<Parent, Children> {
        public final Parent parent;
        public final List<Children> children;
        public boolean expanded = false;

        public Group(@NonNull Parent parent, @Nullable List<Children> children) {
            this.parent = parent;
            this.children = children == null ? Collections.<Children>emptyList() : children;
        }

        public int getViewHolderCount() {
            if (expanded) {
                return children.size() + 1;
            } else {
                return 1;
            }
        }

    }

    private class RecyclerViewItem {
        final Parent parent;
        final Children children;
        final int viewType;
        final int groupPosition;
        final int childPosition;

        private RecyclerViewItem(Parent parent, int groupPosition) {
            this.parent = parent;
            this.children = null;
            this.viewType = GROUP_VIEW_TYPE;
            this.groupPosition = groupPosition;
            this.childPosition = 0;
        }

        private RecyclerViewItem(Children children, int groupPosition, int childPosition) {
            this.parent = null;
            this.children = children;
            this.viewType = CHILDREN_VIEW_TYPE;
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
        }
    }

    private final List<Group<Parent, Children>> groups;
    private final List<RecyclerViewItem> recyclerViewItemList = new ArrayList<>();

    private boolean onlyOneGroupExpand = false;

    protected ExpandableAdapter(List<Group<Parent, Children>> groups) {
        this.groups = groups == null ? Collections.<Group<Parent, Children>>emptyList() : groups;
        setDataInternal();
    }

    /**
     * 设置只展开一个group
     *
     * @param onlyOneGroupExpand
     */
    public void setOnlyOneGroupExpand(boolean onlyOneGroupExpand) {
        this.onlyOneGroupExpand = onlyOneGroupExpand;
    }

    private OnChildrenItemClickListener<Children> onChildrenItemClickListener;

    public void setOnChildrenItemClickListener(OnChildrenItemClickListener<Children> onChildrenItemClickListener) {
        this.onChildrenItemClickListener = onChildrenItemClickListener;
    }

    private final ExpandableItemAnimator expandableItemAnimator = new ExpandableItemAnimator(this);

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.setItemAnimator(expandableItemAnimator);
    }

    /**
     * 某个group是否展开
     *
     * @param groupPosition
     * @return
     */
    public boolean isExpand(int groupPosition) {
        return groups.get(groupPosition).expanded;
    }


    private void setDataInternal() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("main on main thread");
        }
        recyclerViewItemList.clear();
        for (int i = 0; i < groups.size(); i++) {
            Group<Parent, Children> group = groups.get(i);
            recyclerViewItemList.add(new RecyclerViewItem(group.parent, i));
            if (group.expanded) {
                for (int j = 0; j < group.children.size(); j++) {
                    recyclerViewItemList.add(new RecyclerViewItem(group.children.get(j), i, j));
                }
            }
        }

    }

    private void performGroupExpandChange(int group, boolean expand) {
        int groupLayoutPosition = getGroupLayoutPosition(group);
        onGroupExpandChange(group, groupLayoutPosition, expand);
        notifyGroupChange(group, GROUP_EXPAND_CHANGE);
    }

    public void onGroupExpandChange(int group, int adapterPosition, boolean expand) {

    }


    /**
     * 通知group布局刷新
     *
     * @param groupPosition
     * @param refreshToken
     */
    public final void notifyGroupChange(int groupPosition, Object refreshToken) {
        notifyItemChanged(getGroupLayoutPosition(groupPosition), refreshToken);
    }

    /**
     * 通知子item局部刷新
     *
     * @param groupPosition
     * @param childrenPosition
     * @param refreshToken
     */
    public final void notifyChildrenChange(int groupPosition, int childrenPosition, Object refreshToken) {
        Group<Parent, Children> item = groups.get(groupPosition);
        if (item.expanded) {
            notifyItemChanged(getChildrenLayoutPosition(groupPosition, childrenPosition), refreshToken);
        }

    }


    /**
     * 展开一个group
     *
     * @param group
     * @param anim
     */
    public final void expandGroup(int group, boolean anim) {
        if (onlyOneGroupExpand) {
            for (int i = 0; i < groups.size(); i++) {
                Group<Parent, Children> item = groups.get(i);
                if (i == group && !item.expanded) {
                    item.expanded = true;
                    // 改变数据源布局刷新
                    setDataInternal();
                    performGroupExpandChange(i, true);
                    if (anim) {
                        notifyItemRangeInserted(getGroupLayoutPosition(group) + 1, item.children.size());
                    } else {
                        notifyDataSetChanged();
                    }
                } else if (item.expanded) {
                    item.expanded = false;
                    // 改变数据源布局刷新
                    setDataInternal();
                    performGroupExpandChange(i, false);
                    if (anim) {
                        notifyItemRangeRemoved(getGroupLayoutPosition(i) + 1, item.children.size());
                    } else {
                        notifyDataSetChanged();
                    }
                }
            }
        } else {
            Group<Parent, Children> item = groups.get(group);
            if (!item.expanded) {
                item.expanded = true;
                // 改变数据源布局刷新
                setDataInternal();
                performGroupExpandChange(group, true);

                if (anim) {
                    notifyItemRangeInserted(getGroupLayoutPosition(group) + 1, item.children.size());
                } else {
                    notifyDataSetChanged();
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
    public final void collapseGroup(int group, boolean anim) {
        Group<Parent, Children> item = groups.get(group);
        if (item.expanded) {
            item.expanded = false;
            // 改变数据源布局刷新
            setDataInternal();
            if (anim) {
                notifyItemRangeRemoved(getGroupLayoutPosition(group) + 1, item.children.size());
            } else {
                notifyDataSetChanged();
            }
            performGroupExpandChange(group, false);
        }
    }

    private int getGroupLayoutPosition(int groupPosition) {
        int layoutPosition = 0;

        List<Group<Parent, Children>> itemData = this.groups.subList(0, groupPosition);
        for (Group<Parent, Children> item : itemData) {
            layoutPosition += item.getViewHolderCount();
        }
        return layoutPosition;
    }

    private int getChildrenLayoutPosition(int groupPosition, int childrenPosition) {

        return getGroupLayoutPosition(groupPosition) + 1 + childrenPosition;
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == GROUP_VIEW_TYPE) {
            RecyclerView.ViewHolder parentViewHolder = onCreateParentViewHolder(viewGroup);
            parentViewHolder.itemView.setZ(1);
            return parentViewHolder;
        } else {
            RecyclerView.ViewHolder childrenViewHolder = onCreateChildrenViewHolder(viewGroup);
//            childrenViewHolder.itemView.setZ(100);
            return childrenViewHolder;
        }
    }

    protected abstract RecyclerView.ViewHolder onCreateParentViewHolder(@NonNull ViewGroup viewGroup);

    protected abstract RecyclerView.ViewHolder onCreateChildrenViewHolder(@NonNull ViewGroup viewGroup);

    @Override
    public final int getItemViewType(int position) {
        return recyclerViewItemList.get(position).viewType;
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        throw new UnsupportedOperationException("not implementation");
    }

    @Override
    public final void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position, @NonNull List<Object> payloads) {
        final RecyclerViewItem recyclerViewItem = recyclerViewItemList.get(holder.getAdapterPosition());
        holder.itemView.setTag(GROUP_INDEX_FLAG, recyclerViewItem.groupPosition);
        holder.itemView.setTag(CHILDREN_INDEX_FLAG, recyclerViewItem.childPosition);
        if (getItemViewType(holder.getAdapterPosition()) == GROUP_VIEW_TYPE) {
            if (payloads.isEmpty()) {
                final boolean expand = isExpand(recyclerViewItem.groupPosition);
                holder.itemView.setTag(GROUP_IS_EXPAND_FLAG, expand);
                onBindParentViewHolder(holder, recyclerViewItem.parent, recyclerViewItem.groupPosition, expand);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isExpand(recyclerViewItem.groupPosition)) {
                            collapseGroup(recyclerViewItem.groupPosition, true);
                        } else {
                            expandGroup(recyclerViewItem.groupPosition, true);
                        }
                    }
                });
            } else {
                for (Object payload : payloads) {
                    if (GROUP_EXPAND_CHANGE == payload) {
                        final boolean expand = isExpand(recyclerViewItem.groupPosition);
                        holder.itemView.setTag(GROUP_IS_EXPAND_FLAG, expand);
                        long animDuration = expand ? expandableItemAnimator.getAddDuration() : expandableItemAnimator.getRemoveDuration();
                        onBindParentViewHolderExpandChange(holder, recyclerViewItem.parent, recyclerViewItem.groupPosition, animDuration, expand);
                    } else {
                        onBindParentViewHolder(holder, recyclerViewItem.parent, recyclerViewItem.groupPosition, payload);
                    }
                }
            }
        } else {
            if (payloads.isEmpty()) {
                onBindChildrenViewHolder(holder, recyclerViewItem.children, recyclerViewItem.groupPosition, recyclerViewItem.childPosition);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onChildrenItemClickListener != null) {
                            onChildrenItemClickListener.onItemClick(recyclerViewItem.children, recyclerViewItem.groupPosition, recyclerViewItem.childPosition);
                        }
                    }
                });
            } else {
                for (Object payload : payloads) {
                    onBindChildrenViewHolder(holder, recyclerViewItem.children, recyclerViewItem.groupPosition, recyclerViewItem.childPosition, payload);
                }
            }
        }

    }

    protected abstract void onBindChildrenViewHolder(@NonNull RecyclerView.ViewHolder holder, Children children, int group, int childrenPosition);

    protected void onBindChildrenViewHolder(@NonNull RecyclerView.ViewHolder holder, Children children, int group, int childrenPosition, Object refreshToken) {
        onBindChildrenViewHolder(holder, children, group, childrenPosition);
    }

    protected abstract void onBindParentViewHolder(@NonNull RecyclerView.ViewHolder holder, Parent parent, int groupPosition, boolean expand);

    protected void onBindParentViewHolder(@NonNull RecyclerView.ViewHolder holder, Parent parent, int groupPosition, Object refreshToken) {
        onBindParentViewHolder(holder, parent, groupPosition, isExpand(groupPosition));
    }

    protected abstract void onBindParentViewHolderExpandChange(@NonNull RecyclerView.ViewHolder holder, Parent parent, int groupPosition, long animDuration, boolean expand);


    public interface OnChildrenItemClickListener<T> {
        void onItemClick(T t, int groupPosition, int childrenPosition);

    }

    @Override
    public final int getItemCount() {
        return recyclerViewItemList.size();
    }


    public final int getGroupCount() {
        return groups.size();
    }

    public final int getChildrenCount(int groupIndex) {
        return groups.get(groupIndex).children.size();
    }

    /**
     * 获取组index
     *
     * @param viewHolder
     * @return
     */
    public final int getGroupIndex(RecyclerView.ViewHolder viewHolder) {
        Object index = viewHolder.itemView.getTag(GROUP_INDEX_FLAG);
        if (index instanceof Integer) {
            return (int) index;
        } else {
            return RecyclerView.NO_POSITION;
        }
    }

    /**
     * 获取children index
     *
     * @param viewHolder
     * @return
     */
    public final int getChildrenIndex(RecyclerView.ViewHolder viewHolder) {
        Object index = viewHolder.itemView.getTag(CHILDREN_INDEX_FLAG);
        if (index instanceof Integer) {
            return (int) index;
        } else {
            return RecyclerView.NO_POSITION;
        }
    }
}
