package wang.relish.dialogtest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * @author wangxin
 * @since 20200803
 */
class ListenChildVisibilityFrameLayout : FrameLayout, OnChildVisibilityChangedListener {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val mLinePaint: Paint = Paint()

    init {
        mLinePaint.color = Color.BLACK // TODO 换肤
        mLinePaint.strokeWidth = DimensionUtils.dpToPx(5F).toFloat()
    }

    override fun onChildVisibilityChanged(child: View, oldVisibility: Int, newVisibility: Int, index: Int) {
        if (oldVisibility == newVisibility) return
        if (newVisibility == View.VISIBLE) {
            for (i in 0 until index) { // 在其之下的都GONE
                val kid = getChildAt(i) ?: continue
                if (kid !is ICheckVisibility<*>) continue
                val c = kid as ICheckVisibility<*>
                c.setVisibilityWithOutCallback(View.GONE)
            }
        } else if (newVisibility == View.GONE) {
            for (i in index - 1 downTo 0) {
                val kid = getChildAt(i) ?: continue
                if (kid !is ICheckVisibility<*>) continue
                val c = kid as ICheckVisibility<*>
                if (c.isDataValid()) {
                    c.setVisibilityWithOutCallback(View.VISIBLE)
                    break
                }
            }
        }
        var allGone = true
        for (i in 0 until childCount) {
            if ((getChildAt(i) as ICheckVisibility<*>).isDataValid()) {
                allGone = false
                break
            }
        }
        visibility = if (allGone) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawLine(0F, 0F, width.toFloat(), 0F, mLinePaint)
    }
}

interface OnChildVisibilityChangedListener {
    fun onChildVisibilityChanged(
            child: View,
            oldVisibility: Int,
            newVisibility: Int,
            index: Int
    )
}

interface ICheckVisibility<T> {
    /** 是否有可显示数据 */
    fun isDataValid(): Boolean
    /** 设置数据并检测是否可显示 */
    fun setData(t: T)
    /** 检测是否可显示(触发回调到父布局) */
    fun checkVisible()
    /** 设置显示或隐藏且不产生回调 */
    fun setVisibilityWithOutCallback(visibility: Int)
}

fun View.hasBrotherVisibleAbove(): Boolean {
    parent ?: return false
    if (parent !is ViewGroup) return false
    val p = parent as ViewGroup
    val indexOfChild = p.indexOfChild(this)
    for (i in indexOfChild + 1 until p.childCount) {
        val childAt = p.getChildAt(i) ?: continue
        if (childAt is ICheckVisibility<*>) {
            if (childAt.isDataValid()) {
                return true
            }
        }
    }
    return false
}

fun View.setVisibilityWithCheck(visibility: Int, superSetVisibility: (visibility: Int) -> Unit) {
    val old = getVisibility()
    if (old == visibility) return
    if (visibility == View.VISIBLE && hasBrotherVisibleAbove()) {
        superSetVisibility.invoke(View.GONE)
        return
    }
    if (visibility == View.GONE && hasBrotherVisibleAbove()) {
        superSetVisibility.invoke(visibility)
        return
    }
    parent?.takeIf { it is ListenChildVisibilityFrameLayout }?.apply {
        val p = this as ListenChildVisibilityFrameLayout
        p.onChildVisibilityChanged(this@setVisibilityWithCheck, old, visibility, p.indexOfChild(this@setVisibilityWithCheck))
    }
    superSetVisibility.invoke(visibility)
}