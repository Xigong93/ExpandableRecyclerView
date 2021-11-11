package pokercc.android.expandablerecyclerview.sample.childsticky

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.sample.databinding.ChildStickyChildHeaderItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.ChildStickyChildItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.ChildStickyGroupItemBinding


private class ParentVH(val binding: ChildStickyGroupItemBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

private class ChildHeaderVH(val binding: ChildStickyChildHeaderItemBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

private class ChildVH(val binding: ChildStickyChildItemBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

private const val CHILD_HEADER = -10
private const val CHILD_ITEM = -11

class ChildStickyHeaderAdapter : ExpandableAdapter<ExpandableAdapter.ViewHolder>() {
    private var groupCount: Int = 30
    private var childCount: Int = 10


    override fun onCreateGroupViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder = LayoutInflater.from(viewGroup.context)
        .let { ChildStickyGroupItemBinding.inflate(it, viewGroup, false) }
        .let(::ParentVH)

    override fun onCreateChildViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return if (viewType == CHILD_HEADER) {
            LayoutInflater.from(viewGroup.context)
                .let { ChildStickyChildHeaderItemBinding.inflate(it, viewGroup, false) }
                .let(::ChildHeaderVH)
        } else {
            LayoutInflater.from(viewGroup.context)
                .let { ChildStickyChildItemBinding.inflate(it, viewGroup, false) }
                .let(::ChildVH)
        }
    }


    override fun onBindChildViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            if (holder.itemViewType == CHILD_HEADER) {
                holder as ChildHeaderVH
                holder.binding.apply {
                    title1.text = "Prop${groupPosition + 1}-1"
                    title2.text = "Prop${groupPosition + 1}-2"
                    title3.text = "Prop${groupPosition + 1}-3"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindGroupViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            (holder as? ParentVH)?.apply {
                binding.titleText.text = "Category ${(groupPosition + 1)}"
                binding.arrowImage.rotation = if (expand) 0f else -90.0f
            }
        }
    }


    override fun onGroupViewHolderExpandChange(
        holder: ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        holder as ParentVH
        val arrowImage = holder.binding.arrowImage
        if (expand) {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, 0f)
        } else {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, -90f)
        }.setDuration(animDuration).start()

    }

    override fun getChildItemViewType(groupPosition: Int, childPosition: Int): Int {
        return if (childPosition == 0) CHILD_HEADER else CHILD_ITEM
    }

    override fun getGroupCount(): Int = groupCount
    override fun getChildCount(groupPosition: Int): Int = childCount + 1

}