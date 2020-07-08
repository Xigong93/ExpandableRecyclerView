package pokercc.android.expandablerecyclerview.sample.college

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import pokercc.android.expandablerecyclerview.sample.databinding.CollegeActivityBinding

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
    private lateinit var viewModel: CollegeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = CollegeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val modelFactory = SavedStateViewModelFactory(application, this)
        viewModel = ViewModelProvider(this, modelFactory).get()
        viewModel.colleges.observe(this, Observer {
            val list = if (shortList) it.subList(0, 2) else it
            binding.recyclerView.adapter = CollegeAdapter(shortList, list)
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.colleges.value.isNullOrEmpty()) {
            viewModel.loadColleges()
        }
    }
}

