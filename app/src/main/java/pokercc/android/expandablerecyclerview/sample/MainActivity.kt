package pokercc.android.expandablerecyclerview.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pokercc.android.expandablerecyclerview.sample.college.CollegeActivity
import pokercc.android.expandablerecyclerview.sample.databinding.ActivityMainBinding
import pokercc.android.expandablerecyclerview.sample.textbook.TextBookListActivity

class MainActivity : AppCompatActivity() {
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
    }
}
