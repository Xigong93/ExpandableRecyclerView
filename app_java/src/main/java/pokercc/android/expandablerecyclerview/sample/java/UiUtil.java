package pokercc.android.expandablerecyclerview.sample.java;

import android.content.res.Resources;
import android.util.TypedValue;

public class UiUtil {
    public static float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics()
        );
    }
}

