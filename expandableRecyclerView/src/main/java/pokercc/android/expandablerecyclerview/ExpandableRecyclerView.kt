package pokercc.android.expandablerecyclerview

import android.content.Context
import android.graphics.Canvas
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.ClassLoaderCreator
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.view.children
import androidx.customview.view.AbsSavedState
import androidx.recyclerview.widget.RecyclerView

/**
 * 可展开的RecycleView
 * - 支持展开动画，防止View动画错乱
 * @author pokercc
 * @date 2020年06月30日22:18:47
 */
open class ExpandableRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    companion object {
        private const val LOG_TAG = "ExpandableRecyclerView"

        @Suppress("MayBeConstant")
        val DEBUG = BuildConfig.DEBUG
    }

    override fun draw(c: Canvas) {
        super.draw(c)
        // 修复动画不更新的bug
        if (itemDecorationCount == 0 && isAnimating) {
            postInvalidateOnAnimation()
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter != null) {
            require(adapter is ExpandableAdapter)
        }
        super.setAdapter(adapter)
        if (adapter != null) {
            itemAnimator = ExpandableItemAnimator(this)
        }
    }


    fun requireAdapter(): ExpandableAdapter<*> {
        return requireNotNull(getExpandableAdapter())
    }

    fun getExpandableAdapter(): ExpandableAdapter<*>? {
        return adapter as? ExpandableAdapter<*>
    }

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
        val layoutManager = layoutManager ?: return drawAction(canvas)
        val childGroupPosition = requireAdapter().getGroupPosition(childViewHolder)
        // 不能越过自己的group,也不能越过上一个group
        val groupView = findGroupViewHolder(childGroupPosition)?.itemView
        val groupViewBottom =
            groupView?.let { it.y + it.height + layoutManager.getBottomDecorationHeight(it) } ?: 0f
        val nextGroupView = findGroupViewHolder(childGroupPosition + 1)?.itemView
        val bottom = nextGroupView?.let { it.y  + layoutManager.getTopDecorationHeight(it) }?: height.toFloat()
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

    @Suppress("MemberVisibilityCanBePrivate")
    fun findGroupViewHolder(groupPosition: Int): ViewHolder? {
        val expandableAdapter = adapter as? ExpandableAdapter<*> ?: return null
        for (child in children) {
            val viewHolder = getChildViewHolder(child)
            if (!expandableAdapter.isGroup(viewHolder.itemViewType)) continue
            if (groupPosition == expandableAdapter.getGroupPosition(viewHolder)) {
                return viewHolder
            }
        }
        return null
    }

    override fun onSaveInstanceState(): Parcelable {
        val state = SavedState(super.onSaveInstanceState()!!)
        state.expandState = getExpandableAdapter()?.onSaveInstanceState()
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        getExpandableAdapter()?.onRestoreInstanceState(state.expandState)
    }

    class SavedState : AbsSavedState {
        var expandState: Parcelable? = null

        /**
         * called by CREATOR
         */
        internal constructor(`in`: Parcel, loader: ClassLoader?) : super(`in`, loader) {
            expandState = `in`.readParcelable(
                loader ?: ExpandableAdapter::class.java.classLoader
            )
        }

        /**
         * Called by onSaveInstanceState
         */
        internal constructor(superState: Parcelable) : super(superState) {}

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeParcelable(expandState, 0)
        }

        companion object CREATOR : ClassLoaderCreator<SavedState> {
            override fun createFromParcel(
                `in`: Parcel,
                loader: ClassLoader
            ): SavedState {
                return SavedState(`in`, loader)
            }

            override fun createFromParcel(`in`: Parcel): SavedState {
                return SavedState(`in`, null)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }

    }


}