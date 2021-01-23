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

    private CanvasCompat() { }

    static void disableZ(@NonNull Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            canvas.disableZ();
            return;
        }
        DisableZMethod.invoke(canvas);
    }

    private static class DisableZMethod {
        @Nullable
        private static final Method DISABLE_Z_METHOD;

        static {
            Method method = null;
            try {
                //noinspection JavaReflectionMemberAccess
                method = Canvas.class.getDeclaredMethod("insertInorderBarrier");
                method.setAccessible(true);
            } catch (Exception e) {
                Log.e(LOG_TAG, "disableZMethod", e);
            }
            DISABLE_Z_METHOD = method;
        }

        static void invoke(@NonNull Canvas canvas) {
            try {
                Method method = DISABLE_Z_METHOD;
                if (method != null) method.invoke(canvas);
            } catch (Exception e) {
                Log.e(LOG_TAG, "disableZ", e);
            }
        }
    }
}
