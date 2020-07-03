package pokercc.android.expandablerecyclerview.sample.textbook

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
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
import kotlin.math.ceil

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
        val spanCount = 3
        val gridLayoutManager = GridLayoutManager(this, spanCount)
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.addItemDecoration(TextBookDecorator(spanCount))
        viewModel = viewModelProvider.get(TextBookListViewModel::class.java)
        viewModel.loadData()
        viewModel.textBookLists.observe(this, Observer {
            val textBookAdapter = TextBookAdapter(it)
            binding.recyclerView.adapter = textBookAdapter
            gridLayoutManager.spanSizeLookup = TextBookSpanLookup(spanCount, textBookAdapter)
        })
    }

}

