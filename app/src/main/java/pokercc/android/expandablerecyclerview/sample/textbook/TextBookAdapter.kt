package pokercc.android.expandablerecyclerview.sample.textbook

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.sample.databinding.TextBookGradeBinding
import pokercc.android.expandablerecyclerview.sample.databinding.TextBookTypeBinding
import pokercc.android.expandablerecyclerview.sample.dpToPx
import kotlin.math.ceil

internal class TextBookDecorator(private val spanCount: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        // Don't use spanSizeLookup to calculate spanIndex, it's not right.
        val spanIndex = (view.layoutParams as? GridLayoutManager.LayoutParams)?.spanIndex ?: return
        val verticalPadding = 15.dpToPx(parent.context).toInt()
        outRect.top = verticalPadding
        when (spanIndex) {
            0 -> {
                outRect.left = 20.dpToPx(parent.context).toInt()
                outRect.right = 10.dpToPx(parent.context).toInt()
            }
            spanCount - 1 -> {
                outRect.right = 20.dpToPx(parent.context).toInt()
            }
            else -> {
                outRect.right = 10.dpToPx(parent.context).toInt()
            }
        }
        // 如果是Group的最后一行，给一个下边距
        val expandableAdapter = parent.adapter as? ExpandableAdapter<*> ?: return
        val viewHolder = parent.getChildViewHolder(view)
        val (groupPosition, childPosition) = expandableAdapter.getItemLayoutPosition(viewHolder)
        childPosition ?: return
        val childCount = expandableAdapter.getChildCount(groupPosition)
        val rowInGroup = ceil((childPosition + 1) / spanCount.toFloat() - 1).toInt()
        val maxRow = ceil(childCount / spanCount.toFloat() - 1).toInt()
        if (rowInGroup == maxRow) {
            outRect.bottom = verticalPadding
        }
        Log.d(
            "TextBookDecorator",
            "groupPosition:$groupPosition,childPosition:$childPosition,childCount:$childCount,rowInGroup:$rowInGroup,maxRow:$maxRow"
        )
    }
}

internal class TextBookSpanLookup(
    private val spanCount: Int,
    private val expandableAdapter: ExpandableAdapter<*>
) :
    GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        val viewType = expandableAdapter.getItemViewType(position)
        return if (expandableAdapter.isGroup(viewType)) spanCount else 1
    }

}

private class TypeViewHolder(val binding: TextBookTypeBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

private class GradeViewHolder(val binding: TextBookGradeBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

internal class TextBookAdapter(private val items: List<TextBookList>) :
    ExpandableAdapter<ExpandableAdapter.ViewHolder>() {
    override fun onCreateGroupViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ExpandableAdapter.ViewHolder {
        val binding = TextBookTypeBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return TypeViewHolder(binding)
    }

    override fun onCreateChildViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ExpandableAdapter.ViewHolder {
        val binding = TextBookGradeBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return GradeViewHolder(binding)
    }

    override fun onBindChildViewHolder(
        holder: ExpandableAdapter.ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        val viewHolder = holder as? GradeViewHolder ?: return
        if (payloads.isEmpty()) {
            viewHolder.binding.titleText.text = items[groupPosition].filters[childPosition].name
            val isSelected = childPosition == 0
            viewHolder.binding.titleText.isSelected = isSelected
            val textColor = if (isSelected) Color.WHITE else Color.BLACK
            viewHolder.binding.titleText.setTextColor(textColor)
        }
    }

    override fun onBindGroupViewHolder(
        holder: ExpandableAdapter.ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        val viewHolder = holder as? TypeViewHolder ?: return
        if (payloads.isEmpty()) {
            viewHolder.binding.titleText.text = items[groupPosition].name
            viewHolder.binding.arrowImage.rotation = if (expand) 0f else -90.0f
        }

    }

    override fun onGroupViewHolderExpandChange(
        holder: ExpandableAdapter.ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        val arrowImage: View = (holder as? TypeViewHolder)?.binding?.arrowImage ?: return
        if (expand) {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, 0f)
                .setDuration(animDuration)
                .start()

        } else {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, -90f)
                .setDuration(animDuration)
                .start()
        }
    }

    override fun getGroupCount(): Int = items.size

    override fun getChildCount(groupPosition: Int): Int = items[groupPosition].filters.size
}