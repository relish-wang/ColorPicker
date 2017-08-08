package wang.relish.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 与{@link ColorPickerView}配合使用, 用于显示当前选择的颜色
 *
 * @author Relish Wang
 * @since 2017/08/02
 */
public class ColorPickerPanelView extends View {

    /**
     * 面板的边框尺寸(单位:px)
     */
    private final static float BORDER_WIDTH_PX = 1;

    private int mBorderColor = 0xff6E6E6E;
    private int mColor = 0xff000000;

    private Paint mBorderPaint;
    private Paint mColorPaint;

    private RectF mDrawingRect;
    private RectF mColorRect;


    public ColorPickerPanelView(Context context) {
        this(context, null);
    }

    public ColorPickerPanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerPanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mBorderPaint = new Paint();
        mColorPaint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        final RectF rect = mColorRect;

        mBorderPaint.setColor(mBorderColor);
        canvas.drawRect(mDrawingRect, mBorderPaint);

        mColorPaint.setColor(mColor);

        canvas.drawRect(rect, mColorPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mDrawingRect = new RectF();
        mDrawingRect.left = getPaddingLeft();
        mDrawingRect.right = w - getPaddingRight();
        mDrawingRect.top = getPaddingTop();
        mDrawingRect.bottom = h - getPaddingBottom();

        setUpColorRect();

    }

    private void setUpColorRect() {
        final RectF dRect = mDrawingRect;

        float left = dRect.left + BORDER_WIDTH_PX;
        float top = dRect.top + BORDER_WIDTH_PX;
        float bottom = dRect.bottom - BORDER_WIDTH_PX;
        float right = dRect.right - BORDER_WIDTH_PX;

        mColorRect = new RectF(left, top, right, bottom);
    }

    /**
     * 设置面板颜色
     *
     * @param color 颜色值
     */
    public void setColor(int color) {
        mColor = color;
        invalidate();
    }

    /**
     * 获得当前面板颜色
     *
     * @return 颜色值
     */
    public int getColor() {
        return mColor;
    }

    /**
     * 设置面板的边框颜色
     *
     * @param color 颜色值
     */
    public void setBorderColor(int color) {
        mBorderColor = color;
        invalidate();
    }

    /**
     * 获得当前面板边框颜色
     */
    public int getBorderColor() {
        return mBorderColor;
    }

}