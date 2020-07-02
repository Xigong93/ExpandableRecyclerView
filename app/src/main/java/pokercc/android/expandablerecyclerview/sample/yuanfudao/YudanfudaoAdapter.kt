package pokercc.android.expandablerecyclerview.sample.yuanfudao

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.sample.databinding.YuanfudaoChildItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.YuanfudaoGroupItemBinding

private class GroupViewHolder(val binding: YuanfudaoGroupItemBinding) :
    RecyclerView.ViewHolder(binding.root)

private class ChildViewHolder(val binding: YuanfudaoChildItemBinding) :
    RecyclerView.ViewHolder(binding.root)

internal class YudanfudaoAdapter : ExpandableAdapter<RecyclerView.ViewHolder>() {
    override fun onCreateGroupViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = YuanfudaoGroupItemBinding.inflate(
            inflater, viewGroup, false
        )
        return GroupViewHolder(binding)
    }

    override fun onCreateChildViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = YuanfudaoChildItemBinding.inflate(
            inflater, viewGroup, false
        )
        return ChildViewHolder(binding)
    }

    override fun onBindChildViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        val viewHolder = holder as? ChildViewHolder ?: return
        if (payloads.isEmpty()) {
            viewHolder.binding.titleText.text = "child ${childPosition + 1}"
        }
    }

    override fun onBindGroupViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        val viewHolder = holder as? GroupViewHolder ?: return
        if (payloads.isEmpty()) {
            viewHolder.binding.titleText.text = "Group ${groupPosition + 1}"
        }
    }

    override fun onGroupViewHolderExpandChange(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        val viewHolder = holder as? GroupViewHolder ?: return

        val titleText = viewHolder.binding.titleText
        titleText.rotation = 0f
        titleText.animate()
            .rotation(360f)
            .setDuration(animDuration)
            .start()
    }

    override fun getGroupCount(): Int = 2

    override fun getChildCount(groupPosition: Int): Int = 4
}