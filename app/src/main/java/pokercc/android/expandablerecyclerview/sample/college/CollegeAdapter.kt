package pokercc.android.expandablerecyclerview.sample.college

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.sample.databinding.CityItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.CollegeItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.FamousCollegeItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.ProvinceItemBinding
import java.lang.IllegalArgumentException


private class ProvinceVH(val itemBinding: ProvinceItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CityVH(val itemBinding: CityItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CollegeVH(val itemBinding: CollegeItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class FamousCollegeVH(val itemBinding: FamousCollegeItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

internal class CollegeAdapter(private val shortList: Boolean, private val data: List<CollegeZone>) :
    ExpandableAdapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val PROVINCE_ITEM = 11
        private const val CITY_ITEM = 12
        private const val COLLEGE_ITEM = -1
        private const val FAMOUS_COLLEGE__ITEM = -2
    }

    override fun onCreateGroupViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return when (viewType) {
            PROVINCE_ITEM -> {
                ProvinceVH(
                    ProvinceItemBinding.inflate(inflater, viewGroup, false)
                )
            }
            CITY_ITEM -> {
                CityVH(
                    CityItemBinding.inflate(inflater, viewGroup, false)
                )
            }
            else -> {
                throw IllegalArgumentException("unSupport viewType:${viewType}")
            }
        }
    }


    override fun onCreateChildViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return when (viewType) {
            FAMOUS_COLLEGE__ITEM -> {
                FamousCollegeVH(
                    FamousCollegeItemBinding.inflate(inflater, viewGroup, false)
                )
            }
            COLLEGE_ITEM -> {
                CollegeVH(
                    CollegeItemBinding.inflate(inflater, viewGroup, false)
                )
            }
            else -> {
                throw IllegalArgumentException("unSupport viewType:${viewType}")
            }
        }
    }

    override fun getGroupItemViewType(groupPosition: Int): Int {
        return if (data[groupPosition].city) {
            CITY_ITEM
        } else {
            PROVINCE_ITEM
        }
    }

    override fun getChildItemViewType(groupPosition: Int, childPosition: Int): Int {
        return if (data[groupPosition].colleges[childPosition].famous) {
            FAMOUS_COLLEGE__ITEM
        } else {
            COLLEGE_ITEM
        }
    }

    override fun isGroup(viewType: Int): Boolean {
        return viewType > 0
    }

    override fun onBindChildViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        val children = data[groupPosition].colleges[childPosition]
        if (payloads.isEmpty()) {
            (holder as? CollegeVH)?.apply {
                itemBinding.titleText.text = children.name
            }
            (holder as? FamousCollegeVH)?.apply {
                itemBinding.titleText.text = children.name
            }
        }
    }

    override fun onBindGroupViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        val parent = data[groupPosition]
        if (payloads.isEmpty()) {
            (holder as? ProvinceVH)?.apply {
                itemBinding.titleText.text = parent.name
                itemBinding.arrowImage.rotation = if (expand) 0f else -90.0f
            }
            (holder as? CityVH)?.apply {
                itemBinding.titleText.text = parent.name
                itemBinding.arrowImage.rotation = if (expand) 0f else -90.0f
            }
        }
    }


    override fun onGroupViewHolderExpandChange(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {

        val arrowImage = when {
            holder as? ProvinceVH != null -> holder.itemBinding.arrowImage
            holder as? CityVH != null -> holder.itemBinding.arrowImage
            else -> return
        }
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


    override fun getGroupCount(): Int = data.size

    override fun getChildCount(groupPosition: Int): Int = if (shortList) {
        data[groupPosition].colleges.size.coerceAtMost(2)
    } else {
        data[groupPosition].colleges.size
    }

}
