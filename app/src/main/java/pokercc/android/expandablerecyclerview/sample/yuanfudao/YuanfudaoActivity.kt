package pokercc.android.expandablerecyclerview.sample.yuanfudao

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
        val rv = binding.recyclerView
        rv.layoutManager = LinearLayoutManager(this)
        rv.addItemDecoration(YuanfudaoItemDecorator())
        rv.adapter = YudanfudaoAdapter()
    }

}

