# ExpandableRecyclerView
使用RecyclerView 实现的折叠列表
支持特性:
- 流畅的展开和关闭动画
- 支持只展开一个Group
- 支持多类型item
- 支持粘性头(Sticky Header)
- 展开的状态保存和恢复

TodoFeatures:
- 支持展开和关闭全部

注意事项:
- 使用StickyHeaderRecyclerViewContainer，GroupViewHolder.itemView请设置不透明的背景，否则会发生穿透的情况
- ExpandableRecyclerView的height需要设置为match_parent或固定大小,否则在展开和关闭时，RecyclerView的高度会发生变化导致动画的执行有问题