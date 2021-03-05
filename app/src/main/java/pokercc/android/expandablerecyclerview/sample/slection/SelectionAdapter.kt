package pokercc.android.expandablerecyclerview.sample.slection

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.sample.databinding.SelectionChildItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.SelectionParentItemBinding

private class SelectionGroupVH(val binding: SelectionParentItemBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

private class SelectionChildVH(val binding: SelectionChildItemBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

internal class SelectionAdapter(private val selections: List<SingleSelection<String>>) :
    ExpandableAdapter<ExpandableAdapter.ViewHolder>() {
    override fun onCreateGroupViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(viewGroup.context)
            .let { SelectionParentItemBinding.inflate(it, viewGroup, false) }
            .let(::SelectionGroupVH)
    }

    override fun onCreateChildViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(viewGroup.context)
            .let { SelectionChildItemBinding.inflate(it, viewGroup, false) }
            .let(::SelectionChildVH)
    }

    override fun onBindChildViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        holder as SelectionChildVH
        with(holder.binding) {
            val selection = selections[groupPosition]
            val option = selection.options[childPosition]
            radioButton.isChecked = childPosition == selection.selectedIndex
            radioButton.text = option
            root.setOnClickListener {
                onSelectionChanged(selection, groupPosition, childPosition)
            }
        }

    }

    private fun onSelectionChanged(
        selection: SingleSelection<String>,
        groupPosition: Int,
        childPosition: Int
    ) {
        val oldSelectedIndex = selection.selectedIndex
        if (oldSelectedIndex == childPosition) return
        selection.selectedIndex = childPosition
        notifyChildChange(groupPosition, childPosition)
        if (oldSelectedIndex >= 0) {
            notifyChildChange(groupPosition, oldSelectedIndex)
        }
    }

    override fun onBindGroupViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            holder as SelectionGroupVH
            val item = selections[groupPosition]
            holder.binding.titleText.text = "${groupPosition + 1}.${item.name}"
            holder.binding.arrowImage.rotation = if (expand) 0f else -90.0f
        }
    }

    override fun onGroupViewHolderExpandChange(
        holder: ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        holder as SelectionGroupVH
        val arrowImage = holder.binding.arrowImage
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

    override fun getGroupCount(): Int = selections.size

    override fun getChildCount(groupPosition: Int): Int = selections[groupPosition].options.size
}