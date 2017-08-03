package wang.relish.colorpicker;

import android.graphics.Color;
import android.support.annotation.NonNull;

/**
 * @author Relish Wang
 * @since 2017/08/02
 */
class Utils {


    /**
     * 16进制颜色 转 ARGB(String)类型颜色
     *
     * @param color 16进制颜色
     * @return ARGB颜色
     */
    public static String convertToARGB(int color) {
        String rgb = convertToRGB(color);
        String alpha = Integer.toHexString(Color.alpha(color));
        if (alpha.length() == 1) alpha = "0" + alpha;
        return "#" + alpha + rgb.replace("#", "");
    }


    /**
     * 16进制颜色 转 RGB(String)类型颜色
     *
     * @param color 16进制颜色
     * @return RGB颜色(无透明度值)
     */
    public static String convertToRGB(int color) {
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));
        if (red.length() == 1) red = "0" + red;
        if (green.length() == 1) green = "0" + green;
        if (blue.length() == 1) blue = "0" + blue;
        return "#" + red + green + blue;
    }

    /**
     * ARGB(含RGB)颜色 转 16进制颜色
     *
     * @param argb ARGB(含RGB)颜色
     * @return 16进制颜色
     * @throws NumberFormatException 当{@param argb}不是一个正确的颜色格式的字符串时
     */
    public static int convertToColorInt(@NonNull String argb) throws IllegalArgumentException {
        if (!argb.startsWith("#")) {
            argb = "#" + argb;
        }
        if (argb.matches("#[0-9a-fA-F]{6}") || argb.matches("#[0-9a-fA-F]{8}"))
            return Color.parseColor(argb);
        throw new IllegalArgumentException(argb + " is not a valid color.");
    }
}
