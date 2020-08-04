package wang.relish.dialogtest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.btn1 -> {
                tvAdd.setData("+ 音乐应用")
            }
            R.id.btn2 -> {
                tvAdd.setData("")
            }
            R.id.btn3 -> {
                tvLive.setData("正在直播~")
            }
            R.id.btn4 -> {
                tvLive.setData("")
            }
            R.id.btn5 -> {
                tvDownload.setData("正在下载")
            }
            R.id.btn6 -> {
                tvDownload.setData("")
            }
            else -> {
            }
        }
    }
}
