package wang.relish.colorpicker;

import android.graphics.Color;
import android.support.annotation.NonNull;

/**
 * @author Relish Wang
 * @since 2017/08/02
 */
class Utils {

    /**
     * 16进制颜色 转 RGB(String)类型颜色(无#号)
     *
     * @param color 16进制颜色
     * @return RGB颜色(无透明度值)
     */
    static String convertToRGB(int color) {
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));
        if (red.length() == 1) red = "0" + red;
        if (green.length() == 1) green = "0" + green;
        if (blue.length() == 1) blue = "0" + blue;
        return red + green + blue;
    }

    /**
     * ARGB(含RGB)颜色 转 16进制颜色
     *
     * @param argb ARGB(含RGB)颜色
     * @return 16进制颜色
     * @throws NumberFormatException 当{@param argb}不是一个正确的颜色格式的字符串时
     */
    static int convertToColorInt(@NonNull String argb) throws IllegalArgumentException {
        if (argb.matches("[0-9a-fA-F]{1,6}")) {
            switch (argb.length()) {
                case 1:
                    return Color.parseColor("#00000" + argb);
                case 2:
                    return Color.parseColor("#0000" + argb);
                case 3:
                    char r = argb.charAt(0), g = argb.charAt(1), b = argb.charAt(2);
                    //noinspection StringBufferReplaceableByString
                    return Color.parseColor(new StringBuilder("#")
                            .append(r).append(r)
                            .append(g).append(g)
                            .append(b).append(b)
                            .toString());
                case 4:
                    return Color.parseColor("#00" + argb);
                case 5:
                    return Color.parseColor("#0" + argb);
                case 6:
                    return Color.parseColor("#" + argb);
            }
        }
        throw new IllegalArgumentException(argb + " is not a valid color.");
    }
}
