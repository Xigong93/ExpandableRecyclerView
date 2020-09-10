package pokercc.android.expandablerecyclerview.sample

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.WindowManager

fun Number.dpToPx(context: Context? = null): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

/**
 * Context 安全的转Activity
 */
fun Context.asActivity(): Activity? {
    var context = this
    while (context !is Activity && context is ContextWrapper) {
        context = context.baseContext
    }
    return context as? Activity
}

/**
 * 设置activity全屏
 */
fun Activity.fullScreen() {

    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    val option =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    window.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val vis = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        option or vis
    } else {
        option
    }
    window.statusBarColor = Color.TRANSPARENT

}