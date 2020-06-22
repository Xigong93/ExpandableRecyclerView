package pokercc.android.expandablerecyclerview.sample

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
import pokercc.android.expandablerecyclerview.ExpandableItemDecoration
import pokercc.android.expandablerecyclerview.sample.databinding.CityItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.CollegeActivityBinding
import pokercc.android.expandablerecyclerview.sample.databinding.CollegeItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.ProvinceItemBinding
import java.lang.IllegalArgumentException

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
            val countryAdapter = CollegeAdapter(it)
            binding.recyclerView.adapter = countryAdapter
        })
        binding.recyclerView.addItemDecoration(ExpandableItemDecoration())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.loadColleges()
    }
}


private class ProvinceViewHolder(val itemBinding: ProvinceItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CityViewHolder(val itemBinding: CityItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CollegeHolder(val itemBinding: CollegeItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CollegeAdapter(private val data: List<CollegeZone>) :
    ExpandableAdapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val PROVINCE_ITEM = 11
        private const val CITY_ITEM = 12
        private const val COLLEGE_ITEM = -1
    }

    override fun onCreateGroupViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            PROVINCE_ITEM -> {
                ProvinceViewHolder(
                    ProvinceItemBinding.inflate(
                        LayoutInflater.from(viewGroup.context),
                        viewGroup,
                        false
                    )
                )
            }
            CITY_ITEM -> {
                CityViewHolder(
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
        return CollegeHolder(
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

    override fun isGroup(viewType: Int): Boolean {
        return viewType == CITY_ITEM || viewType == PROVINCE_ITEM
    }

    override fun onBindChildViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        val children = data[groupPosition].colleges[childPosition]
        (holder as CollegeHolder).apply {
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
            (holder as? ProvinceViewHolder)?.apply {
                itemBinding.titleText.text = parent.name
                itemBinding.arrowImage.rotation = -90.0f
            }
            (holder as? CityViewHolder)?.apply {
                itemBinding.titleText.text = parent.name
                itemBinding.arrowImage.rotation = -90.0f
            }
        }
    }


    override fun onGroupViewHolderExpandChange(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        var arrowImage: View? = (holder as? ProvinceViewHolder)?.itemBinding?.arrowImage
        if (arrowImage == null) {
            arrowImage = (holder as? CityViewHolder)?.itemBinding?.arrowImage
        }
        arrowImage ?: return
        if (expand) {
            arrowImage.animate()
                .setDuration(animDuration)
                .rotation(0f)
                .start()
        } else {
            arrowImage.animate()
                .setDuration(animDuration)
                .rotation(-90.0f)
                .start()
        }

    }


    override fun getGroupCount(): Int = data.size

    override fun getChildCount(groupPosition: Int): Int = data[groupPosition].colleges.size

}
