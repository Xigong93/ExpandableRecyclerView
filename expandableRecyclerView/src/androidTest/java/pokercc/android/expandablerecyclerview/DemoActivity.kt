package pokercc.android.expandablerecyclerview

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager

class DemoActivity : Activity() {
    val recyclerView by lazy { ExpandableRecyclerView(this) }
    val expandableAdapter = DemoExpandableAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(recyclerView)
        with(recyclerView) {
            adapter = expandableAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}