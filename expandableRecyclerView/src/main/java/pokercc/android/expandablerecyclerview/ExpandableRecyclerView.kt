package pokercc.android.expandablerecyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.ClassLoaderCreator
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.view.children
import androidx.customview.view.AbsSavedState
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min

/**
 * ExpandableRecyclerView with smoothness animation.
 * @author pokercc
 * @date 2020-06-30.22:18:47
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
        // To fix animation not update bugs.
        if (itemDecorationCount == 0 && isAnimating) {
            postInvalidateOnAnimation()
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter != null) {
            require(adapter is ExpandableAdapter)
        }
        super.setAdapter(adapter)
        if (adapter != null && itemAnimator !is ExpandableItemAnimator) {
            itemAnimator = ExpandableItemAnimator(this)
        }
    }


    fun requireAdapter(): ExpandableAdapter<*> {
        return requireNotNull(getExpandableAdapter())
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getExpandableAdapter(): ExpandableAdapter<*>? {
        return adapter as? ExpandableAdapter<*>
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        return clipAndDrawChild(canvas, child) {
            super.drawChild(canvas, child, drawingTime)
        }
    }

    /**
     * Clip and draw
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun <T> clipAndDrawChild(canvas: Canvas, child: View, drawAction: (Canvas) -> T): T {
        val holder = getChildViewHolder(child)
        // Ignore group
        val layoutManager = layoutManager
        if (layoutManager == null || !isAnimating || requireAdapter().isGroup(holder.itemViewType)) {
            return drawAction(canvas)
        }
        val (groupPosition, _) = requireAdapter().getItemLayoutPosition(holder)
        // Child must draw between it's group and next group.
        val groupView = findGroupViewHolder(groupPosition)?.itemView
        val groupViewBottom =
            groupView?.let { it.y + it.height + layoutManager.getBottomDecorationHeight(it) } ?: 0f
        val nextGroupView = findGroupViewHolder(groupPosition + 1)?.itemView
        val bottom = nextGroupView?.let { it.y - layoutManager.getTopDecorationHeight(it) }
            ?: height.toFloat()
        if (DEBUG) {
            val itemPosition = requireAdapter().getItemLayoutPosition(holder)
            Log.d(
                LOG_TAG,
                "itemPosition:${itemPosition},top:$groupViewBottom,bottom:${bottom},viewHolder:$holder"
            )
        }
        // Clip
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

    /**
     * Hook system method to handle touch event.
     */
    @Suppress("MemberVisibilityCanBePrivate", "unused")
    protected fun isTransformedTouchPointInView(
        x: Float, y: Float, child: View,
        outLocalPoint: PointF?
    ): Boolean {
        outLocalPoint?.apply {
            set(x, y)
            this.x += scrollX + child.left
            this.y += scrollY + child.top
        }
        val childViewHolder = getChildViewHolder(child)
        return if (!isAnimating || requireAdapter().isGroup(childViewHolder.itemViewType)) {
            x >= child.x && x <= (child.x + child.width) && y >= child.y && y <= (child.y + child.height)
        } else {
            childContain(childViewHolder, x, y)
        }
    }

    private fun childContain(child: ViewHolder, x: Float, y: Float): Boolean {
        val layoutManager = layoutManager ?: return false
        val childGroupPosition = requireAdapter().getItemLayoutPosition(child).groupPosition
        val groupView = findGroupViewHolder(childGroupPosition)?.itemView
        val groupBottom =
            groupView?.let { it.y + it.height + layoutManager.getBottomDecorationHeight(it) }
                ?: 0f
        val nextGroupView = findGroupViewHolder(childGroupPosition + 1)?.itemView
        val nextGroupTop = nextGroupView?.let { it.y - layoutManager.getTopDecorationHeight(it) }
            ?: height.toFloat()
        val itemView = child.itemView
        val borderTop = max(itemView.y, groupBottom)
        val borderBottom = min(itemView.y + itemView.height, nextGroupTop)
        return x >= itemView.left && x <= itemView.right && y >= borderTop && y <= borderBottom
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun findGroupViewHolder(groupLayoutPosition: Int): ViewHolder? {
        for (child in children) {
            val viewHolder = getChildViewHolder(child)
            if (!requireAdapter().isGroup(viewHolder.itemViewType)) continue
            if (groupLayoutPosition == requireAdapter().getItemLayoutPosition(viewHolder).groupPosition) {
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