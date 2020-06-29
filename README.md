# ExpandableRecyclerView
使用RecyclerView 实现的折叠列表
支持特性:
- 流畅的展开和关闭动画
- 支持只展开一个Group
- 支持多类型item
- 支持粘性头(Sticky Header)

注意事项:
- ItemView请设置不透明的背景，否则会发生穿透的情况
- ExpandableAdapter会对RecyclerView设置ItemAnimator和ItemDecoration
- ExpandableAdapter 可以设置不透明背景颜色，来防止Children Item超出 Group
