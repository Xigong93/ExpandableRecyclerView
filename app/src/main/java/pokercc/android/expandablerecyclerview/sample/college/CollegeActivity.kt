package pokercc.android.expandablerecyclerview.sample.college

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.sample.databinding.*
import java.lang.IllegalArgumentException

/**
 * 大学列表页面
 * @author pokercc
 * @date 2020-6-30 16:25:26
 */
class CollegeActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, CollegeActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = CollegeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: CollegeViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get()
        viewModel.colleges.observe(this, Observer {
            val countryAdapter =
                CollegeAdapter(
                    it
                )
            binding.recyclerView.adapter = countryAdapter
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.loadColleges()
    }
}


private class ProvinceVH(val itemBinding: ProvinceItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CityVH(val itemBinding: CityItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CollegeVH(val itemBinding: CollegeItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class FamousCollegeVH(val itemBinding: FamousCollegeItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CollegeAdapter(private val data: List<CollegeZone>) :
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
        return when (viewType) {
            PROVINCE_ITEM -> {
                ProvinceVH(
                    ProvinceItemBinding.inflate(
                        LayoutInflater.from(viewGroup.context),
                        viewGroup,
                        false
                    )
                )
            }
            CITY_ITEM -> {
                CityVH(
                    CityItemBinding.inflate(
                        LayoutInflater.from(viewGroup.context),
                        viewGroup,
                        false
                    )
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
        if (viewType == FAMOUS_COLLEGE__ITEM) {
            return FamousCollegeVH(
                FamousCollegeItemBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
            )
        }
        return CollegeVH(
            CollegeItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )

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
        (holder as? CollegeVH)?.apply {
            itemBinding.titleText.text = children.name
        }
        (holder as? FamousCollegeVH)?.apply {
            itemBinding.titleText.text = children.name
        }
    }

    override fun onBindGroupViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            val parent = data[groupPosition]
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

    override fun getChildCount(groupPosition: Int): Int = data[groupPosition].colleges.size

}
