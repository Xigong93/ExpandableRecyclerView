# ExpandableRecyclerView [![](https://jitpack.io/v/pokercc/ExpandableRecyclerView.svg)](https://jitpack.io/#pokercc/ExpandableRecyclerView)
使用RecyclerView 实现的折叠列表

## 效果图

![](./img/accordion.gif)

## 支持特性:
- 流畅的展开和关闭动画
- 支持只展开一个Group
- 支持展开和关闭全部
- 支持多类型item
- 支持LinearLayoutManager和GridLayoutManager
- 支持粘性头(Sticky Header)
- 展开的状态保存和恢复(横竖屏切换时)
- 支持java工程集成(大于v0.6.0版本)

## 欢迎下载demo，体验效果
下载地址:https://github.com/pokercc/ExpandableRecyclerView/releases

## 如何使用:
1. 引入依赖

last_version = [![](https://jitpack.io/v/pokercc/ExpandableRecyclerView.svg)](https://jitpack.io/#pokercc/ExpandableRecyclerView)

```gradle
implementation("com.github.pokercc:ExpandableRecyclerView:${last_version}")

```

2. 配置代码
- 在布局中使用ExpandableRecyclerView
- 继承ExpandableAdapter,实现自己的adapter

更多详细使用请参考demo

注意事项:
- 使用StickyHeader，GroupViewHolder.itemView请设置不透明的背景，否则会发生穿透的情况
- ExpandableRecyclerView的height需要设置为match_parent或固定大小,否则在展开和关闭时，RecyclerView的高度会发生变化导致动画的执行有问题

设计图来自:https://dribbble.com/shots/3253927-Accordion

传送门:https://github.com/pokercc/ExpandableRecyclerView

欢迎大家star或提issue