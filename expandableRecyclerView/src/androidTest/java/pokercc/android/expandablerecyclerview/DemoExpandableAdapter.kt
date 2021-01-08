package pokercc.android.expandablerecyclerview

import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class TestViewHolder(itemView: View) : ExpandableAdapter.ViewHolder(itemView)

open class DemoExpandableAdapter(
    private val groupCount: Int = 3,
    private val childCount: Int = 2
) : ExpandableAdapter<ExpandableAdapter.ViewHolder>() {
    override fun onCreateGroupViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return TestViewHolder(TextView(viewGroup.context))
    }

    override fun onCreateChildViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return TestViewHolder(TextView(viewGroup.context))
    }

    override fun onBindChildViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
    }

    override fun onBindGroupViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
    }

    override fun onGroupViewHolderExpandChange(
        holder: ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
    }

    override fun getGroupCount(): Int = groupCount

    override fun getChildCount(groupPosition: Int): Int = childCount
}