# ExpandableRecyclerView ![Bintray](https://img.shields.io/bintray/v/pokercc/android/ExpandableRecyclerView)
使用RecyclerView 实现的折叠列表

## 效果图

### 粘性头部:
![粘性头部](./img/stick_header.gif)


### 最后一个条目展开动画:
![最后一个条目展开](./img/last_group_expand.gif)

### GridLayoutManager:
![GridLayout](./img/grid_layout.gif)

## 支持特性:
- 流畅的展开和关闭动画
- 支持只展开一个Group
- 支持展开和关闭全部
- 支持多类型item
- 支持LinearLayoutManager和GridLayoutManager
- 支持粘性头(Sticky Header)
- 展开的状态保存和恢复(横竖屏切换时)

## 欢迎下载demo，体验效果
下载地址:https://www.pgyer.com/ExpandableRecyclerView

![ExpandableRecyclerView](./img/ExpandableRecyclerView.png)

## 如何使用:
1. 引入依赖

last_version = ![Bintray](https://img.shields.io/bintray/v/pokercc/android/ExpandableRecyclerView)

```gradle
implementation("pokercc.android.ExpandableRecyclerView:expandableRecyclerView:${last_version}")

```

2. 配置代码
- 在布局中使用ExpandableRecyclerView
- 继承ExpandableAdapter,实现自己的adapter


注意事项:
- 使用StickyHeaderRecyclerViewContainer，GroupViewHolder.itemView请设置不透明的背景，否则会发生穿透的情况
- ExpandableRecyclerView的height需要设置为match_parent或固定大小,否则在展开和关闭时，RecyclerView的高度会发生变化导致动画的执行有问题 


## 原理分析
### 如何展开和折叠一个group
使用的RecyclerView.Adapter.notifyItemInsert()方法，
但是这个函数，使用的展开和折叠的动画默认都是alpha动画，想实现children item在下面，group滑开children 显示，只需要把alpha 动画改成不执行就可以了。
把alpha 动画修改为不执行之后,会出现child item 覆盖到group item上面的问题，因为child item 是后添加的，所以child item的绘制索引大于group

### 怎么解决child 覆盖group的问题呢？
有两种方案:
1. 给全部的child 设置z，让child item 在绘制顺序上都低于group item
缺点：
- group item 需要设置纯色背景
- View.setZ() 是有版本限制的，最低api 21
- 有时候groupPosition=0的childItem 会覆盖到groupPosition=1的childItem
- 当点击展开倒数第二个
2. 实现RecyclerView.setChildDrawingOrderCallback()函数
- group item 需要设置纯色背景
- 有时候groupPosition=0的childItem 会覆盖到groupPosition=1的childItem
3. 使用裁剪的方案
重写RecyclerView.drawChild()方法，需要在执行动画的过程中，裁剪child item
```kotlin
override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        return clipAndDrawChild(canvas, child) {
            super.drawChild(canvas, child, drawingTime)
        }
    }

/**
* 裁剪和绘制
*/
fun <T> clipAndDrawChild(canvas: Canvas, child: View, drawAction: (Canvas) -> T): T {
    val childViewHolder = getChildViewHolder(child)
    // 不裁减GroupViewHolder
    if (!isAnimating || requireAdapter().isGroup(childViewHolder.itemViewType)) {
        return drawAction(canvas)
    }
    val childGroupPosition = requireAdapter().getGroupPosition(childViewHolder)
    // 不能越过自己的group,也不能越过下一个group
    val groupView = findGroupViewHolder(childGroupPosition)?.itemView
    val groupViewBottom = groupView?.let { it.y + it.height } ?: 0f
    val nextGroupView = findGroupViewHolder(childGroupPosition + 1)?.itemView
    val bottom = nextGroupView?.y ?: height.toFloat()
    if (DEBUG) {
        val childPosition = requireAdapter().getChildPosition(childViewHolder)
        Log.d(
            LOG_TAG,
            "group:${childGroupPosition},child:$childPosition,top:$groupViewBottom,bottom:${bottom}"
        )
    }
    // 裁剪
    val saveCount = canvas.save()
    try {
        canvas.clipRect(
            child.x,
            groupViewBottom,
            child.x + child.width,
            bottom
        )
        return drawAction(canvas)
    } finally {
        canvas.restoreToCount(saveCount)
    }
}

```
这种方案最彻底，不需要设置不透明背景，也没有性能问题,但是有时候还是会有问题,因为在动画过程中没有重绘
还需要重写RecyclerView的draw方法
```kotlin
override fun draw(c: Canvas) {
    super.draw(c)
    // 修复动画不更新的bug
    if (itemDecorationCount == 0 && isAnimating) {
        postInvalidateOnAnimation()
    }
}
```
### 如何处理在动画过程中事件分发问题呢？
关键方法是:
```java
/**
    * Returns true if a child view contains the specified point when transformed
    * into its coordinate space.
    * Child must not be null.
    * @hide
    */
@UnsupportedAppUsage
protected boolean isTransformedTouchPointInView(float x, float y, View child,
        PointF outLocalPoint) {
    final float[] point = getTempPoint();
    point[0] = x;
    point[1] = y;
    transformPointToViewLocal(point, child);
    final boolean isInView = child.pointInView(point[0], point[1]);
    if (isInView && outLocalPoint != null) {
        outLocalPoint.set(point[0], point[1]);
    }
    return isInView;
}

```
嘿嘿，但是这个方法不让app使用，发现其中调用
```



    /**
     * Determines whether the given point, in local coordinates is inside the view.
     */
    /*package*/ final boolean pointInView(float localX, float localY) {
        return pointInView(localX, localY, 0);
    }

    /**
     * Utility method to determine whether the given point, in local coordinates,
     * is inside the view, where the area of the view is expanded by the slop factor.
     * This method is called while processing touch-move events to determine if the event
     * is still within the view.
     *
     * @hide
     */
    @UnsupportedAppUsage
    public boolean pointInView(float localX, float localY, float slop) {
        return localX >= -slop && localY >= -slop && localX < ((mRight - mLeft) + slop) &&
                localY < ((mBottom - mTop) + slop);
    }

```



传送门:https://github.com/pokercc/ExpandableRecyclerView

初次做开源项目，这个控件比较复杂里面还有很多细节，我写了20多天。
欢迎大家star或提issue