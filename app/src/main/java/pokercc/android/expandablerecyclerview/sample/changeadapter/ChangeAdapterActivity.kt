package pokercc.android.expandablerecyclerview.sample.changeadapter

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.sample.databinding.ChangeAdapterActivityBinding
import pokercc.android.expandablerecyclerview.sample.databinding.CountChildrenItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.CountParentItemBinding
import kotlin.random.Random

class ChangeAdapterActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ChangeAdapterActivity::class.java))
        }
    }

    private val binding by lazy { ChangeAdapterActivityBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.recyclerView.adapter = CountAdapter(3, 2)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.changeAdapter.setOnClickListener {
            binding.recyclerView.adapter = CountAdapter()
        }
        binding.notifyDataSet.setOnClickListener {
            (binding.recyclerView.adapter as? CountAdapter)?.setNewData()
        }
    }
}

private class ChildVH(val binding: CountChildrenItemBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

private class ParentVH(val binding: CountParentItemBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

private class CountAdapter(
    private var groupCount: Int = Random.nextInt(1, 10),
    private var childCount: Int = Random.nextInt(2, 10)
) : ExpandableAdapter<ExpandableAdapter.ViewHolder>() {

    fun setNewData() {
        groupCount = Random.nextInt(1, 10)
        childCount = Random.nextInt(2, 10)
        notifyDataSetChanged()
    }

    override fun onCreateGroupViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ExpandableAdapter.ViewHolder = LayoutInflater.from(viewGroup.context)
        .let { CountParentItemBinding.inflate(it, viewGroup, false) }
        .let(::ParentVH)

    override fun onCreateChildViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ExpandableAdapter.ViewHolder = LayoutInflater.from(viewGroup.context)
        .let { CountChildrenItemBinding.inflate(it, viewGroup, false) }
        .let(::ChildVH)


    override fun onBindChildViewHolder(
        holder: ExpandableAdapter.ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            (holder as? ChildVH)?.apply {
                binding.titleText.text = (childPosition + 1).toString()
            }
        }
    }

    override fun onBindGroupViewHolder(
        holder: ExpandableAdapter.ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            (holder as? ParentVH)?.apply {
                binding.titleText.text = (groupPosition + 1).toString()
                binding.arrowImage.rotation = if (expand) 0f else -90.0f
            }
        }
    }


    override fun onGroupViewHolderExpandChange(
        holder: ExpandableAdapter.ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {

        holder as ParentVH
        val arrowImage = holder.binding.arrowImage
        if (expand) {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, 0f)
                .setDuration(animDuration)
                .start()
            // 不要使用这种动画，Item离屏之后，动画会取消
//            arrowImage.animate()
//                .setDuration(animDuration)
//                .rotation(0f)
//                .start()
        } else {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, -90f)
                .setDuration(animDuration)
                .start()
        }

    }

    override fun getGroupCount(): Int = groupCount
    override fun getChildCount(groupPosition: Int): Int = childCount

}