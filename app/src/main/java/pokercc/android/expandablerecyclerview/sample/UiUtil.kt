package pokercc.android.expandablerecyclerview.sample

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

fun Number.dpToPx(context: Context? = null): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

