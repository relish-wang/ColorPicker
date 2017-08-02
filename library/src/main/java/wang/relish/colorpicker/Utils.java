package wang.relish.colorpicker;

import android.graphics.Color;

/**
 * @author Relish Wang
 * @since 2017/08/02
 */
class Utils {


    /**
     * convert a hex value of color to a String value of ARGB color
     *
     * @param color color
     * @return string value of ARGB color (with the alpha value)
     */
    public static String convertToARGB(int color) {
        String alpha = Integer.toHexString(Color.alpha(color));
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));

        if (alpha.length() == 1) {
            alpha = "0" + alpha;
        }

        if (red.length() == 1) {
            red = "0" + red;
        }

        if (green.length() == 1) {
            green = "0" + green;
        }

        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        return "#" + alpha + red + green + blue;
    }


    /**
     * convert a hex value of color to a String value of RGB color
     *
     * @param color color
     * @return A string representing the hex value of color,
     * without the alpha value
     */
    public static String convertToRGB(int color) {
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));

        if (red.length() == 1) {
            red = "0" + red;
        }

        if (green.length() == 1) {
            green = "0" + green;
        }

        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        return "#" + red + green + blue;
    }

    /**
     * convert a String value of color to a hex value of color
     *
     * @param argb colorStr
     * @return int vaule of color
     * @throws NumberFormatException when parse color
     */
    public static int convertToColorInt(String argb) throws IllegalArgumentException {

        if (!argb.startsWith("#")) {
            argb = "#" + argb;
        }

        return Color.parseColor(argb);
    }

}
