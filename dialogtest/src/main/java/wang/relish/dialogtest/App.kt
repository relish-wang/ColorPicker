package wang.relish.dialogtest


import android.app.Application

/**
 * @author wangxin
 * @since 20200728
 */
class App : Application(){



    override fun onCreate() {
        super.onCreate()
        sContext = this
    }

    companion object{
        lateinit var sContext: Application
    }
}