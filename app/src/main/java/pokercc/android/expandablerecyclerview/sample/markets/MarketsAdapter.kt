package pokercc.android.expandablerecyclerview.sample.markets

import android.animation.ArgbEvaluator
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.sample.databinding.MarketsChildItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.MarketsParentItemBinding

class MarketChildVH(val binding: MarketsChildItemBinding) : RecyclerView.ViewHolder(binding.root)
class MarketParentVH(val binding: MarketsParentItemBinding) : RecyclerView.ViewHolder(binding.root)

private val names = listOf(
    "Nathaniel Fitzgerald",
    "Lawrence Fuller",
    "Jacob Mullins",
    "Jesus Lewis",
    "Johnny Marr"
)

class MarketsAdapter : ExpandableAdapter<RecyclerView.ViewHolder>() {
    override fun onCreateGroupViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = LayoutInflater.from(viewGroup.context)
        .let { MarketsParentItemBinding.inflate(it, viewGroup, false) }
        .let { MarketParentVH(it) }


    override fun onCreateChildViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = LayoutInflater.from(viewGroup.context)
        .let { MarketsChildItemBinding.inflate(it, viewGroup, false) }
        .let { MarketChildVH(it) }

    override fun onBindChildViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        holder as MarketChildVH
        holder.binding.title.text = names.getOrNull(childPosition)
    }

    override fun onBindGroupViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        holder as MarketParentVH
        if (payloads.isEmpty()) {
            val arrowImage = holder.binding.arrowImage
            arrowImage.rotation = if (expand) -180f else 0f
            val circleDrawable = CircleDrawable()
            arrowImage.background = circleDrawable
            circleDrawable.progress = if (expand) 1f else 0f
        }

    }

    override fun onGroupViewHolderExpandChange(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        holder as MarketParentVH
        val arrowImage = holder.binding.arrowImage
        arrowImage.animate()
            .setDuration(animDuration)
            .rotation(if (expand) -180f else 0f)
            .setUpdateListener {
                val percent = if (expand) it.animatedFraction else 1 - it.animatedFraction
                (arrowImage.background as CircleDrawable).progress = percent
            }
            .start()
    }

    override fun getGroupCount(): Int = 6

    override fun getChildCount(groupPosition: Int): Int = 5
}

private class CircleDrawable : ShapeDrawable(OvalShape()) {
    private val argbEvaluator = ArgbEvaluator()
    private val startColor = 0xff494949.toInt()
    private val endColor = 0xfff64637.toInt()
    var progress: Float = 0f
        set(value) {
            paint.color = argbEvaluator.evaluate(value, startColor, endColor) as Int
            invalidateSelf()
        }
}