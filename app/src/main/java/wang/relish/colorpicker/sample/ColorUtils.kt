package wang.relish.colorpicker.sample

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log


// #FF3A3A
// #FF6B6B #FF3A3A

fun log() {
    val hsvTheme = floatArrayOf(0F, 0F, 0F)
    Color.colorToHSV(Color.parseColor("#FF3A3A"), hsvTheme)
    Log.d("HSVColor", hsvTheme.contentToString())
    val hsvStart = floatArrayOf(0F, 0F, 0F)
    Color.colorToHSV(Color.parseColor("#FF6B6B"), hsvStart)
    Log.d("HSVColor", hsvStart.contentToString())
}


fun changeColor(context: Context, resId: Int, startColor: Int, endColor: Int): Bitmap {
    val bitmap = BitmapFactory.decodeResource(context.resources, resId)
    val width = 131//bitmap.width;
    val height = 131//bitmap.height;
    val bitmapArray = IntArray(width * height)
    var count = 0;
    val redB = Color.red(endColor);
    val greenB = Color.green(endColor);
    val blueB = Color.blue(endColor);
    val redW = Color.red(startColor);
    val greenW = Color.green(startColor);
    val blueW = Color.blue(startColor);
    var red: Int
    var green: Int
    var blue: Int
    for (j in 0 until height) {
        for (i in 0 until width) {
            green = (greenW * (1 - i.toFloat() / width) + greenB * i.toFloat() / width + blueB * i.toFloat() / width).toInt()
            red = (redW * (1 - i.toFloat() / width) + redB * i.toFloat() / width).toInt()
            bitmapArray[count++] = Color.argb(Color.alpha(bitmap.getPixel(i, j)), red, green, 0)
        }
    }
    bitmap.recycle();
    return Bitmap.createBitmap(bitmapArray, width, height,
            Bitmap.Config.ARGB_4444);
}