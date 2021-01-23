package pokercc.android.expandablerecyclerview;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;

@SuppressLint("SoonBlockedPrivateApi")
class CanvasCompat {
    private static final String LOG_TAG = "CanvasCompat";

    @Nullable
    private static final Method disableZMethod;

    static {
        Method method = null;
        try {
            //noinspection JavaReflectionMemberAccess
            method = Canvas.class.getDeclaredMethod("insertInorderBarrier");
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, "disableZMethod", e);
        }
        disableZMethod = method;
    }

    private CanvasCompat() {
    }

    static void disableZ(@NonNull Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            canvas.disableZ();
            return;
        }
        try {
            Method method = disableZMethod;
            if (method != null) method.invoke(canvas);
        } catch (ReflectiveOperationException e) {
            Log.e(LOG_TAG, "disableZ", e);
        }
    }
}
