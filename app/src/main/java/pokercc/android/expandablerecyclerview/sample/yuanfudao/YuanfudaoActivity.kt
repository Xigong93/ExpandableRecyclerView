package pokercc.android.expandablerecyclerview.sample.yuanfudao

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import pokercc.android.expandablerecyclerview.sample.databinding.*

/**
 * 教材列表页面
 * @author pokercc
 * @date 2020-6-30 16:25:44
 */
class YuanfudaoActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, YuanfudaoActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val binding by lazy {
        YuanfudaoActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.postDelayed({
            binding.recyclerView.adapter = YudanfudaoAdapter()
        },100)
    }

}

