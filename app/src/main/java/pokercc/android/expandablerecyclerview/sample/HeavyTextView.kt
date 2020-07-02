package pokercc.android.expandablerecyclerview.sample

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

/**
 * 测量比较耗时的TextView,用来模拟性能问题
 */
@SuppressLint("AppCompatCustomView")
class HeavyTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        SystemClock.sleep(3);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}