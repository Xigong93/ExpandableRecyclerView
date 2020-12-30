package pokercc.android.expandablerecyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class TestExpandableAdapter : ExpandableAdapter<RecyclerView.ViewHolder>() {
    override fun onCreateGroupViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onCreateChildViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindChildViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        TODO("Not yet implemented")
    }

    override fun onBindGroupViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        TODO("Not yet implemented")
    }

    override fun onGroupViewHolderExpandChange(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun getGroupCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getChildCount(groupPosition: Int): Int {
        TODO("Not yet implemented")
    }
}