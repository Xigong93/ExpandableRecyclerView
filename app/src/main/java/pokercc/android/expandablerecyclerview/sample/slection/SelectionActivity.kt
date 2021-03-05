package pokercc.android.expandablerecyclerview.sample.slection

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import pokercc.android.expandablerecyclerview.sample.databinding.ActivitySelectionBinding

private val selections = listOf(
    SingleSelection("Gender", listOf("Male", "Female")),
    SingleSelection("Work Experience", listOf("0~3", "3~5", "5~10", "over 10")),
    SingleSelection("Type of work", listOf("Html", "Android", "iOS", "Java", "Other"))
)

class SelectionActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SelectionActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Developer research"
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = SelectionAdapter(selections).also {
                it.expandGroup(0, false)
            }
        }
    }
}