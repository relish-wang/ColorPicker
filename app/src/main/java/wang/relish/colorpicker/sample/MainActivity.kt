package wang.relish.colorpicker.sample

import android.graphics.*
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import wang.relish.colorpicker.ColorPickerDialog
import java.io.IOException
import java.io.InputStream
import java.net.URL
import kotlin.concurrent.thread


/**
 * @author Relish Wang
 * @since 2017/7/31
 */
class MainActivity : AppCompatActivity() {
    private var mViewColor: View? = null

    /**
     * 选择的颜色
     */
    private var mColor = 0xFFFFFF

    /**
     * 是否显示颜色数值（16进制）
     */
    private var mHexValueEnable = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mStHexEnable = findViewById<View>(R.id.st_hex_enable) as SwitchCompat
        mStHexEnable.isChecked = mHexValueEnable
        mStHexEnable.setOnCheckedChangeListener { _, b -> mHexValueEnable = b }
        mViewColor = findViewById(R.id.view_color)
        mViewColor?.setOnClickListener(View.OnClickListener {
            ColorPickerDialog.Builder(this@MainActivity, mColor)
                    .setHexValueEnabled(mHexValueEnable) //是否显示颜色值
                    //设置点击应用颜色的事件监听
                    .setOnColorPickedListener { color ->
                        mColor = color
                        mViewColor?.setBackgroundColor(mColor)


                        val startColor = getStartColor(color)

                        thread {
                            val bitmap = imageDownload("https://p4.music.126.net/obj/wo3DlcOGw6DClTvDisK1/3265363129/ef9d/da40/a0f0/08c5e5c86c4f722497ae7efed8395fe9.png?imageView")
                                    ?: return@thread
                            val updatedBitmap = handleBitmap(bitmap, startColor, color)
                            dragonBall.post {
                                dragonBall.setImageBitmap(updatedBitmap)
                            }
                        }

                        // dragonBallVector
                        setVectorDrawable(dragonBallVector, startColor, color)
                    }
                    .build()
                    .show() //展示
            log()
        })
    }


    private fun setVectorDrawable(iv: ImageView, startColor: Int, endColor: Int){
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_dragonball) as VectorDrawable
        iv.setColorFilter(startColor)
        iv.setImageDrawable(drawable)
    }


    /**
     * 根据图片路径执行图片下载
     *
     * @param url url
     * @return Bitmap
     */
    private fun imageDownload(url: String): Bitmap? {
        val `is`: InputStream
        return try {
            `is` = URL(url).openConnection().getInputStream()
            val bitmap = BitmapFactory.decodeStream(`is`) // InputStream这种加载方式暂用内存最小
            `is`.close()
            bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    private fun handleBitmap(originalBitmap: Bitmap, startColor: Int, endColor: Int): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height
        val updatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(updatedBitmap).apply {
            drawBitmap(originalBitmap, 0F, 0F, null)
        }
        val paint = Paint().apply {
            this.shader = LinearGradient(0F, 0F, width.toFloat(), 0F, startColor, endColor, Shader.TileMode.CLAMP)
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        }
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        return updatedBitmap
    }


    private fun getStartColor(color: Int): Int {
        val hsv = floatArrayOf(0F, 0F, 0F)
        Color.colorToHSV(color, hsv)
        hsv[1] *= 0.3F//0.7512690813F
        return Color.HSVToColor(hsv)
    }

    private fun getStartColor2(color: Int): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb((255F * 0.7512690813F).toInt(), red, green, blue)
    }
}