package pokercc.android.expandablerecyclerview.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pokercc.android.expandablerecyclerview.sample.changeadapter.ChangeAdapterActivity
import pokercc.android.expandablerecyclerview.sample.college.CollegeActivity
import pokercc.android.expandablerecyclerview.sample.databinding.ActivityMainBinding
import pokercc.android.expandablerecyclerview.sample.databinding.MarketsActivityBinding
import pokercc.android.expandablerecyclerview.sample.markets.MarketsActivity
import pokercc.android.expandablerecyclerview.sample.textbook.TextBookListActivity
import pokercc.android.expandablerecyclerview.sample.yuanfudao.YuanfudaoActivity

class MenuActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MenuActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.collegeLongListButton.setOnClickListener {
            CollegeActivity.start(it.context, false)
        }
        binding.collegeShortListButton.setOnClickListener {
            CollegeActivity.start(it.context, true)
        }
        binding.textBookListButton.setOnClickListener {
            TextBookListActivity.start(it.context)
        }
        binding.yuanfudaoButton.setOnClickListener {
            YuanfudaoActivity.start(it.context)
        }
        binding.changeAdapter.setOnClickListener {
            ChangeAdapterActivity.start(it.context)
        }
    }
}
