package pokercc.android.expandablerecyclerview.sample

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
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


private class CollegeZoneViewHolder(val itemBinding: ProvinceItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CollegeHolder(val itemBinding: CollegeItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CollegeAdapter(private val data: List<CollegeZone>) :
    ExpandableAdapter<RecyclerView.ViewHolder>() {
    override fun onCreateParentViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {
        return CollegeZoneViewHolder(
            ProvinceItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }


    override fun onCreateChildrenViewHolder(
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

    override fun onBindChildrenViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        childrenPosition: Int,
        payloads: List<Any>
    ) {
        val children = data[groupPosition].colleges[childrenPosition]
        (holder as CollegeHolder).apply {
            itemBinding.titleText.text = children.name
        }

    }

    override fun onBindParentViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            val parent = data[groupPosition]
            (holder as CollegeZoneViewHolder).apply {
                itemBinding.titleText.text = parent.name
                itemBinding.arrowImage.rotation = -90.0f
            }
        }
    }


    override fun onParentViewHolderExpandChange(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        val arrowImage = (holder as CollegeZoneViewHolder).itemBinding.arrowImage
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

    override fun getChildrenCount(groupPosition: Int): Int = data[groupPosition].colleges.size

}
