package pokercc.android.expandablerecyclerview.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * 测量比较耗时的TextView,用来模拟性能问题
 */
@SuppressLint("AppCompatCustomView")
public class HeavyTextView extends TextView {
    public HeavyTextView(Context context) {
        super(context);
    }

    public HeavyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HeavyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        SystemClock.sleep(3);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
