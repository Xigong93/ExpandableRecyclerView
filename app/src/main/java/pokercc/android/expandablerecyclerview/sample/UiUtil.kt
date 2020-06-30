package pokercc.android.expandablerecyclerview.sample

import android.content.Context
import android.util.TypedValue

fun Number.dpToPx(context: Context): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    )

