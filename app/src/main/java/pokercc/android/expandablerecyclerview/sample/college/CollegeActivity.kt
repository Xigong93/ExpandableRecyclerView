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
        private const val SHORT_LIST = "short_list"
        fun start(context: Context, shortList: Boolean) {
            val intent = Intent(context, CollegeActivity::class.java)
            intent.putExtra(SHORT_LIST, shortList)
            context.startActivity(intent)
        }
    }

    private val shortList by lazy { intent.getBooleanExtra(SHORT_LIST, false) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = CollegeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val modelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        val viewModel: CollegeViewModel = ViewModelProvider(this, modelFactory).get()
        viewModel.colleges.observe(this, Observer {
            val list = if (shortList) it.subList(0, 2) else it
            binding.recyclerView.adapter = CollegeAdapter(shortList, list)
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.loadColleges()
    }
}

