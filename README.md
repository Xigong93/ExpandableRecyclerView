# ExpandableRecyclerView
使用RecyclerView 实现的折叠列表
支持特性:
- 流畅的展开和关闭动画
- 支持只展开一个Group
- 支持多类型item
- 支持粘性头(Sticky Header)

注意事项:
- 使用StickyHeaderRecyclerViewContainer，GroupViewHolder.itemView请设置不透明的背景，否则会发生穿透的情况
- ExpandableRecyclerView的height需要设置为match_parent,否则动画的执行有问题