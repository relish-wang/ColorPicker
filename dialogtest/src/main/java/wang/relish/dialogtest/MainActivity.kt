package wang.relish.dialogtest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    var dialog: TouchDialog? = null

    fun showDialog(v: View) {
        if (dialog == null) {
            dialog = TouchDialog(this)
        }
        dialog?.show()
    }
}
