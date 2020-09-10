package pokercc.android.expandablerecyclerview.sample.markets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pokercc.android.expandablerecyclerview.ExpandableItemAnimator
import pokercc.android.expandablerecyclerview.sample.databinding.MarketsActivityBinding

class MarketsActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MarketsActivity::class.java))
        }
    }

    private val binding by lazy { MarketsActivityBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.recyclerView.apply {
            adapter = MarketsAdapter()
            itemAnimator = ExpandableItemAnimator(this, flowAnim = true)
            addItemDecoration(MarketsItemDecoration())
            layoutManager = LinearLayoutManager(context)
        }

    }
}