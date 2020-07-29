package wang.relish.colorpicker.sample;

import android.content.res.Configuration;
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

}
