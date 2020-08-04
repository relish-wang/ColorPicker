package wang.relish.dialogtest

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

/**
 * @author wangxin
 * @since 20200803
 */
class NotifyVisibilityToParentTextView : AppCompatTextView, ICheckVisibility<String> {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun setVisibility(visibility: Int) {
        setVisibilityWithCheck(visibility) {
            super.setVisibility(it)
        }
    }

    var mData: String? = ""

    override fun setData(t: String) {
        mData = t
        text = mData
        checkVisible()
    }

    override fun checkVisible() {
        visibility = if (isDataValid() && hasBrotherVisibleAbove().not()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun isDataValid(): Boolean = TextUtils.isEmpty(mData).not()

    override fun setVisibilityWithOutCallback(visibility: Int) {
        super.setVisibility(visibility)
    }

}