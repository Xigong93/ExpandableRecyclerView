# ExpandableRecyclerView [![](https://jitpack.io/v/pokercc/ExpandableRecyclerView.svg)](https://jitpack.io/#pokercc/ExpandableRecyclerView)
[中文README](./README_CN.md)
## Design Sketch

![](./img/accordion.gif)

## Features:
- Expand and close animations smoothly
- Supports expanding only one Group
- Support to expand and close all
- Supports multiple types of items
- Supports LinearLayoutManager and GridLayoutManager
- Support sticky header
- Expanded state saving and recovery (when switching between portrait and horizontal screens)
- Support for Java project integration (>= v0.6.0)

## Take a look
Download Url https://github.com/pokercc/ExpandableRecyclerView/releases

## How to use:
1. include dependence

Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
last_version = [![](https://jitpack.io/v/pokercc/ExpandableRecyclerView.svg)](https://jitpack.io/#pokercc/ExpandableRecyclerView)

```gradle
implementation("com.github.pokercc:ExpandableRecyclerView:${last_version}")

```

2. Write code
- Use `ExpandableRecyclerView` in your layout xml.
- Inheriting `ExpandableAdapter` to realize your adapter.

More detail please refer demo

Attention:
- Using StickyHeader，GroupViewHolder.itemView please set an opaque background, otherwise penetration will occur
- ExpandableRecyclerView's height must set to match_parent or fixed size, Otherwise, the RecyclerView height may change during expansion and closure, causing problems for the execution of the animation

Design from: https://dribbble.com/shots/3253927-Accordion

Welcome to star or create issue.