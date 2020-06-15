package pokercc.android.expandablerecyclerview.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.ExpandableItemAnimator
import pokercc.android.expandablerecyclerview.ExpandableItemDecoration
import pokercc.android.expandablerecyclerview.sample.databinding.ActivityMainBinding
import pokercc.android.expandablerecyclerview.sample.databinding.CityItemBinding
import pokercc.android.expandablerecyclerview.sample.databinding.ProvinceItemBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CountryAdapter(China.map { ExpandableAdapter.Group(it, it.cities) })
            itemAnimator = ExpandableItemAnimator()
            addItemDecoration(ExpandableItemDecoration())
        }

    }
}


private data class Province(val name: String, val cities: List<City>)
private data class City(val name: String)

private class ProvinceViewHolder(val itemBinding: ProvinceItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CityViewHolder(val itemBinding: CityItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

private class CountryAdapter(private val data: List<Group<Province, City>>) :
    ExpandableAdapter<Province, City>(data) {
    override fun onCreateParentViewHolder(viewGroup: ViewGroup): RecyclerView.ViewHolder {
        return ProvinceViewHolder(
            ProvinceItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun onCreateChildrenViewHolder(viewGroup: ViewGroup): RecyclerView.ViewHolder {
        return CityViewHolder(
            CityItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )

    }

    override fun onBindChildrenViewHolder(
        holder: RecyclerView.ViewHolder,
        children: City,
        group: Int,
        childrenPosition: Int
    ) {
        (holder as CityViewHolder).apply {
            itemBinding.titleText.text = children.name
        }

    }

    override fun onBindParentViewHolder(
        holder: RecyclerView.ViewHolder,
        parent: Province,
        groupPosition: Int,
        expand: Boolean
    ) {
        (holder as ProvinceViewHolder).apply {
            itemBinding.titleText.text = parent.name
            itemBinding.arrowImage.rotation = -90.0f

        }

    }

    override fun onBindParentViewHolderExpandChange(
        holder: RecyclerView.ViewHolder,
        parent: Province,
        groupPosition: Int,
        expand: Boolean
    ) {
        val arrowImage = (holder as ProvinceViewHolder).itemBinding.arrowImage
        if (expand) {
            arrowImage.animate()
                .rotation(0f)
                .start()
        } else {
            arrowImage.animate()
                .rotation(-90.0f)
                .start()
        }

    }


}

private val China = listOf(
    Province(
        "河南省",
        listOf(
            City("郑州市"),
            City("开封市"),
            City("平顶山市"),
            City("安阳市"),
            City("鹤壁市"),
            City("新乡市"),
            City("焦作市"),
            City("濮阳市"),
            City("许昌市"),
            City("漯河市"),
            City("三门峡市"),
            City("南阳市"),
            City("商丘市"),
            City("信阳市"),
            City("周口市"),
            City("驻马店市")
        )
    ),
    Province(
        "河北省",
        listOf(
            City("石家庄市"),
            City("唐山市"),
            City("秦皇岛市"),
            City("邯郸市"),
            City("遵化市"),
            City("邢台市"),
            City("涿州市"),
            City("保定市"),
            City("晋州市"),
            City("武安市"),
            City("鹿泉市"),
            City("新乐市"),
            City("辛集市"),
            City("迁安市")
        )
    )
    , Province(
        "四川省",
        listOf(
            City("成都市"),
            City("自贡市"),
            City("攀枝花市"),
            City("泸州市"),
            City("德阳市"),
            City("绵阳市"),
            City("广元市"),
            City("遂宁市"),
            City("内江市"),
            City("乐山市"),
            City("南充市"),
            City("眉山市"),
            City("宜宾市")
        )
    )
)
