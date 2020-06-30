package pokercc.android.expandablerecyclerview.sample.textbook

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.sample.databinding.ActivityTextBookListBinding
import pokercc.android.expandablerecyclerview.sample.databinding.TextBookGradeBinding
import pokercc.android.expandablerecyclerview.sample.databinding.TextBookTypeBinding
import pokercc.android.expandablerecyclerview.sample.dpToPx

/**
 * 教材列表页面
 * @author pokercc
 * @date 2020-6-30 16:25:44
 */
class TextBookListActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TextBookListActivity::class.java))
        }
    }

    private lateinit var binding: ActivityTextBookListBinding
    private lateinit var viewModel: TextBookListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextBookListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModelProvider = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )
        val gridLayoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.addItemDecoration(TextBookDecorator())
        viewModel = viewModelProvider.get(TextBookListViewModel::class.java)
        viewModel.loadData()
        viewModel.textBookLists.observe(this, Observer {
            val textBookAdapter = TextBookAdapter(it)
            binding.recyclerView.adapter = textBookAdapter
            gridLayoutManager.spanSizeLookup = TextBookSpanLookup(textBookAdapter)
        })
    }

}

private class TextBookDecorator : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val gridLayoutManager = parent.layoutManager as? GridLayoutManager ?: return
        val spanSizeLookup = gridLayoutManager.spanSizeLookup
        val adapterPosition = parent.getChildAdapterPosition(view)
        val spanSize = spanSizeLookup.getSpanSize(adapterPosition)
        if (spanSize != 1) return
        outRect.top = 15.dpToPx(parent.context).toInt()
        when (spanSizeLookup.getSpanIndex(adapterPosition, 3)) {
            0 -> {
                outRect.left = 20.dpToPx(parent.context).toInt()
                outRect.right = 10.dpToPx(parent.context).toInt()
            }
            1 -> {
                outRect.right = 10.dpToPx(parent.context).toInt()
            }
            else -> {
                outRect.right = 20.dpToPx(parent.context).toInt()

            }
        }


    }
}

private class TextBookSpanLookup(private val expandableAdapter: ExpandableAdapter<*>) :
    GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        val viewType = expandableAdapter.getItemViewType(position)
        return if (expandableAdapter.isGroup(viewType)) 3 else 1
    }

}

private class TypeViewHolder(val binding: TextBookTypeBinding) :
    RecyclerView.ViewHolder(binding.root)

private class GradeViewHolder(val binding: TextBookGradeBinding) :
    RecyclerView.ViewHolder(binding.root)

private class TextBookAdapter(private val items: List<TestBookList>) :
    ExpandableAdapter<RecyclerView.ViewHolder>() {
    override fun onCreateGroupViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding = TextBookTypeBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return TypeViewHolder(binding)
    }

    override fun onCreateChildViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding = TextBookGradeBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return GradeViewHolder(binding)
    }

    override fun onBindChildViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        val viewHolder = holder as? GradeViewHolder ?: return
        if (payloads.isEmpty()) {
            viewHolder.binding.titleText.text = items[groupPosition].filters[childPosition].name
            val isSelected = childPosition == 0
            viewHolder.binding.titleText.isSelected = isSelected
            val textColor = if (isSelected) Color.WHITE else Color.BLACK
            viewHolder.binding.titleText.setTextColor(textColor)
        }
    }

    override fun onBindGroupViewHolder(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        val viewHolder = holder as? TypeViewHolder ?: return
        if (payloads.isEmpty()) {
            viewHolder.binding.titleText.text = items[groupPosition].name
            viewHolder.binding.arrowImage.rotation = if (expand) 0f else -90.0f
        }

    }

    override fun onGroupViewHolderExpandChange(
        holder: RecyclerView.ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        val arrowImage: View = (holder as? TypeViewHolder)?.binding?.arrowImage ?: return
        if (expand) {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, 0f)
                .setDuration(animDuration)
                .start()

        } else {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, -90f)
                .setDuration(animDuration)
                .start()
        }
    }

    override fun getGroupCount(): Int = items.size

    override fun getChildCount(groupPosition: Int): Int = items[groupPosition].filters.size
}