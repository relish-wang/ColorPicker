package wang.relish.dialogtest;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DimensionUtils {

    private static DisplayMetrics DISPLAY_METRICS;

    static {
        Resources resources = App.sContext.getResources();
        DISPLAY_METRICS = resources.getDisplayMetrics();
    }

    public static int dpToPx(float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, DISPLAY_METRICS) + 0.5);
    }

    public static float dpToPxF(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, DISPLAY_METRICS);
    }

    public static int getScreenWidth(Context context) {
        if (context == null) {
            context = App.sContext;
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
