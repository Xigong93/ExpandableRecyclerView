package pokercc.android.expandablerecyclerview.sample.markets

import android.animation.ArgbEvaluator
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RoundRectShape
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.sample.MenuActivity
import pokercc.android.expandablerecyclerview.sample.R
import pokercc.android.expandablerecyclerview.sample.databinding.MarketsChildItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.MarketsParentItemBinding
import pokercc.android.expandablerecyclerview.sample.dpToPx

class MarketChildVH(val binding: MarketsChildItemBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

class MarketParentVH(val binding: MarketsParentItemBinding) :
    ExpandableAdapter.ViewHolder(binding.root)

private val names = listOf(
    "Nathaniel Fitzgerald",
    "Lawrence Fuller",
    "Jacob Mullins",
    "Jesus Lewis",
    "Johnny Marr"
)

class MarketsAdapter : ExpandableAdapter<ExpandableAdapter.ViewHolder>() {
    override fun onCreateGroupViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ExpandableAdapter.ViewHolder = LayoutInflater.from(viewGroup.context)
        .let { MarketsParentItemBinding.inflate(it, viewGroup, false) }
        .let { MarketParentVH(it) }


    override fun onCreateChildViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ExpandableAdapter.ViewHolder = LayoutInflater.from(viewGroup.context)
        .let { MarketsChildItemBinding.inflate(it, viewGroup, false) }
        .let { MarketChildVH(it) }

    override fun onBindChildViewHolder(
        holder: ExpandableAdapter.ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        holder as MarketChildVH
        holder.binding.title.text = names.getOrNull(childPosition)
        val childCount = getChildCount(groupPosition)
        val radius = 4.dpToPx()
        val shape = when {
            childCount == 1 -> {
                RoundRectShape(FloatArray(8) { radius }, null, null)
            }
            childPosition == 0 -> {
                RoundRectShape(
                    floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f),
                    null,
                    null
                )
            }
            childPosition == childCount - 1 -> {
                RoundRectShape(
                    floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius),
                    null, null
                )
            }
            else -> {
                RoundRectShape(null, null, null)
            }
        }
        holder.binding.root.background = ShapeDrawable(shape).apply {
            paint.color = Color.WHITE
        }
        holder.itemView.setOnClickListener { MenuActivity.start(it.context) }
    }

    override fun onBindGroupViewHolder(
        holder: ExpandableAdapter.ViewHolder,
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
//            holder.binding.shadowView.alpha = if (expand) 1f else 0f
        }

    }

    override fun onGroupViewHolderExpandChange(
        holder: ExpandableAdapter.ViewHolder,
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
                val progress = if (expand) it.animatedFraction else 1 - it.animatedFraction
                (arrowImage.background as CircleDrawable).progress = progress
//                holder.binding.shadowView.alpha = progress
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