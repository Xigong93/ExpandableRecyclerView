package pokercc.android.expandablerecyclerview.sample.yuanfudao

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.sample.databinding.YuanfudaoChildItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.YuanfudaoGroupItemBinding

internal class YuanfudaoGroupViewHolder(val binding: YuanfudaoGroupItemBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

internal class YuanfudaoChildViewHolder(val binding: YuanfudaoChildItemBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

internal class YudanfudaoAdapter : ExpandableAdapter<ExpandableAdapter.ViewHolder>() {
    override fun onCreateGroupViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ExpandableAdapter.ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = YuanfudaoGroupItemBinding.inflate(
            inflater, viewGroup, false
        )
        return YuanfudaoGroupViewHolder(binding)
    }

    override fun onCreateChildViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ExpandableAdapter.ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = YuanfudaoChildItemBinding.inflate(
            inflater, viewGroup, false
        )
        return YuanfudaoChildViewHolder(binding)
    }

    override fun onBindChildViewHolder(
        holder: ExpandableAdapter.ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        val viewHolder = holder as? YuanfudaoChildViewHolder ?: return
        if (payloads.isEmpty()) {
            viewHolder.binding.titleText.text = "child ${childPosition + 1}"
            viewHolder.binding.indicatorView.setAlignView(viewHolder.binding.titleText)
            viewHolder.binding.indicatorView.isLast =
                childPosition == getChildCount(groupPosition) - 1
        }
    }

    override fun onBindGroupViewHolder(
        holder: ExpandableAdapter.ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        val viewHolder = holder as? YuanfudaoGroupViewHolder ?: return
        if (payloads.isEmpty()) {
            viewHolder.binding.titleText.text = "Group ${groupPosition + 1}"
            viewHolder.binding.indicatorView.setAlignView(viewHolder.binding.titleText)
            viewHolder.binding.indicatorView.setExpand(expand, false)
        }
    }

    override fun onGroupViewHolderExpandChange(
        holder: ExpandableAdapter.ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        val viewHolder = holder as? YuanfudaoGroupViewHolder ?: return
        viewHolder.binding.indicatorView.setExpand(expand, true)
    }

    override fun getGroupCount(): Int = 2

    override fun getChildCount(groupPosition: Int): Int = 4
}