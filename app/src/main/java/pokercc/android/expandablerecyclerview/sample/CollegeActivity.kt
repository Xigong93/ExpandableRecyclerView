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
import pokercc.android.expandablerecyclerview.sample.databinding.ActivityMainBinding
import pokercc.android.expandablerecyclerview.sample.databinding.CityItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.CollegeActivityBinding
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
            val countryAdapter =
                CollegeAdapter(it.map { z -> ExpandableAdapter.Group(z, z.colleges) })
            binding.recyclerView.adapter = countryAdapter
        })
        binding.recyclerView.addItemDecoration(ExpandableItemDecoration())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.loadColleges()
    }
}


private class CollegeZoneViewHolder(val itemBinding: ProvinceItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CollegeHolder(val itemBinding: CityItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CollegeAdapter(private val data: List<Group<CollegeZone, College>>) :
    ExpandableAdapter<CollegeZone, College>(data) {
    override fun onCreateParentViewHolder(viewGroup: ViewGroup): RecyclerView.ViewHolder {
        return CollegeZoneViewHolder(
            ProvinceItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun onCreateChildrenViewHolder(viewGroup: ViewGroup): RecyclerView.ViewHolder {
        return CollegeHolder(
            CityItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )

    }

    override fun onBindChildrenViewHolder(
        holder: RecyclerView.ViewHolder,
        children: College,
        groupPosition: Int,
        childrenPosition: Int,
        payloads: List<Any>
    ) {
        (holder as CollegeHolder).apply {
            itemBinding.titleText.text = children.name
        }

    }

    override fun onBindParentViewHolder(
        holder: RecyclerView.ViewHolder,
        parent: CollegeZone,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            (holder as CollegeZoneViewHolder).apply {
                itemBinding.titleText.text = parent.name
                itemBinding.arrowImage.rotation = -90.0f
            }
        }
    }


    override fun onParentViewHolderExpandChange(
        holder: RecyclerView.ViewHolder,
        parent: CollegeZone,
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


}
