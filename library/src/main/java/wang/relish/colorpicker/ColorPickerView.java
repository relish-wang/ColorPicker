package wang.relish.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 取色器
 * <p>
 * 所有注释单位为dp的全局变量，初始都是dp值，在使用之前会乘上屏幕像素(mDensity)称为px值
 *
 * @author Relish Wang
 * @since 2017/08/02
 */
class ColorPickerView extends View {

    @IntDef({PANEL.SAT_VAL, PANEL.HUE})
    @Retention(RetentionPolicy.SOURCE)
    @interface PANEL {
        int SAT_VAL = 0;
        int HUE = 1;
    }

    /**
     * 显示H、SV的矩形的边框粗细（单位：dp）
     */
    private final static float BORDER_WIDTH = 1;
    /**
     * H矩形的宽度（单位：dp）
     */
    private float mHuePanelWidth = 30f;
    /**
     * H、SV矩形间的间距（单位：dp）
     */
    private float mPanelSpacing = 10f;
    /**
     * 当mode为MeasureSpec.UNSPECIFIED时的首选高度（单位：dp）
     */
    private float mPreferredHeight = 200;
    /**
     * 当mode为MeasureSpec.UNSPECIFIED时的首选宽度（单位：dp）
     */
    private float mPreferredWidth = mPreferredHeight + mHuePanelWidth + mPanelSpacing;
    /**
     * SV指示器的半径（单位：dp）
     */
    private float mSVTrackerRadius = 5f;
    /**
     * H、SV矩形与父布局的边距（单位：dp）
     */
    private float mRectOffset = 2f;
    /**
     * 屏幕密度
     */
    private float mDensity = 1f;
    /**
     * 绘制SV的画笔
     */
    private Paint mSatValPaint;
    /**
     * 绘制SV指示器的画笔
     */
    private Paint mSatValTrackerPaint;
    /**
     * 绘制H的画笔
     */
    private Paint mHuePaint;
    /**
     * 绘制H指示器的画笔
     */
    private Paint mHueTrackerPaint;
    /**
     * 绘制H、SV矩形的边线的画笔
     */
    private Paint mBorderPaint;

    //H、V着色器
    private Shader mHueShader;
    private Shader mValShader;

    //HSV的默认值
    private float mHue = 360f;
    private float mSat = 0f;
    private float mVal = 0f;

    /**
     * 用于显示被选择H的位置的指示器的颜色
     */
    private int mSliderTrackerColor = 0xff1c1c1c;
    /**
     * H、SV矩形的边框颜色
     */
    private int mBorderColor = 0xff6E6E6E;
    /**
     * 记录上一次被点击的颜色板
     */
    @PANEL
    private int mLastTouchedPanel = PANEL.SAT_VAL;
    /**
     * 边距
     */
    private float mDrawingOffset;
    /**
     * H指示器
     */
    private RectF mDrawingRect;
    /**
     * 用于选择SV的矩形
     */
    private RectF mSatValRect;
    /**
     * 用于选择H的矩形
     */
    private RectF mHueRect;
    /**
     * SV指示器
     */
    private Point mStartTouchPoint = null;

    private OnColorChangedListener mListener;

    interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mDensity = getContext().getResources().getDisplayMetrics().density;//获取屏幕密度
        mSVTrackerRadius *= mDensity;//灰度饱和度指示器的半径
        mRectOffset *= mDensity;//H、SV矩形与父布局的边距
        mHuePanelWidth *= mDensity;//H矩形的宽度
        mPanelSpacing *= mDensity;//H、SV矩形间的间距
        mPreferredHeight *= mDensity;//当mode为MeasureSpec.UNSPECIFIED时的首选高度
        mPreferredWidth *= mDensity;//当mode为MeasureSpec.UNSPECIFIED时的首选宽度

        mDrawingOffset = calculateRequiredOffset();//计算所需位移

        initPaintTools();//初始化画笔、画布

        setFocusable(true);//设置可获取焦点
        setFocusableInTouchMode(true);//设置在被触摸时会获取焦点
    }

    /**
     * mSVTrackerRadius、
     * mRectOffset、
     * BORDER_WIDTH * mDensity
     * 三者的最大值的1.5倍
     *
     * @return 边距
     */
    private float calculateRequiredOffset() {
        float offset = Math.max(mSVTrackerRadius, mRectOffset);
        offset = Math.max(offset, BORDER_WIDTH * mDensity);
        return offset * 1.5f;
    }

    private void initPaintTools() {
        mSatValPaint = new Paint();
        mSatValTrackerPaint = new Paint();
        mHuePaint = new Paint();
        mHueTrackerPaint = new Paint();
        mBorderPaint = new Paint();

        mSatValTrackerPaint.setStyle(Style.STROKE);
        mSatValTrackerPaint.setStrokeWidth(2f * mDensity);
        mSatValTrackerPaint.setAntiAlias(true);

        mHueTrackerPaint.setColor(mSliderTrackerColor);
        mHueTrackerPaint.setStyle(Style.STROKE);
        mHueTrackerPaint.setStrokeWidth(2f * mDensity);
        mHueTrackerPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthAllowed = MeasureSpec.getSize(widthMeasureSpec);
        int heightAllowed = MeasureSpec.getSize(heightMeasureSpec);

        widthAllowed = isUnspecified(widthMode) ? (int) mPreferredWidth : widthAllowed;
        heightAllowed = isUnspecified(heightMode) ? (int) mPreferredHeight : heightAllowed;

        int width = widthAllowed;
        int height = (int) (widthAllowed - mPanelSpacing - mHuePanelWidth);
        //当根据宽度计算出来的高度大于可允许的最大高度时 或 当前是横屏
        if (height > heightAllowed || "landscape".equals(getTag())) {
            height = heightAllowed;
            width = (int) (height + mPanelSpacing + mHuePanelWidth);
        }
        setMeasuredDimension(width, height);
    }

    private static boolean isUnspecified(int mode) {
        return !(mode == MeasureSpec.EXACTLY || mode == MeasureSpec.AT_MOST);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawingRect.width() <= 0 || mDrawingRect.height() <= 0) return;
        drawSatValPanel(canvas);//绘制SV选择区域
        drawHuePanel(canvas);//绘制右侧H选择区域
    }

    /**
     * 绘制S、V选择区域（矩形）
     *
     * @param canvas 画布
     */
    private void drawSatValPanel(Canvas canvas) {
        //描边（先画一个大矩形, 再在内部画一个小矩形，就可以显示出描边的效果）
        mBorderPaint.setColor(mBorderColor);
        canvas.drawRect(
                mDrawingRect.left,
                mDrawingRect.top,
                mSatValRect.right + BORDER_WIDTH,
                mSatValRect.bottom + BORDER_WIDTH,
                mBorderPaint);

        //组合着色器 = 明度线性着色器 + 饱和度线性着色器
        ComposeShader mShader = generateSVShader();
        mSatValPaint.setShader(mShader);
        canvas.drawRect(mSatValRect, mSatValPaint);

        //初始化选择器的位置
        Point p = satValToPoint(mSat, mVal);
        //绘制显示SV值的选择器
        mSatValTrackerPaint.setColor(0xff000000);
        canvas.drawCircle(p.x, p.y, mSVTrackerRadius - 1f * mDensity, mSatValTrackerPaint);
        //绘制外圆
        mSatValTrackerPaint.setColor(0xffdddddd);
        canvas.drawCircle(p.x, p.y, mSVTrackerRadius, mSatValTrackerPaint);
    }

    /**
     * 创建SV着色器(明度线性着色器 + 饱和度线性着色器)
     *
     * @return 着色器
     */
    private ComposeShader generateSVShader() {
        //明度线性着色器
        if (mValShader == null) {
            mValShader = new LinearGradient(mSatValRect.left, mSatValRect.top, mSatValRect.left, mSatValRect.bottom,
                    0xffffffff, 0xff000000, TileMode.CLAMP);
        }
        //HSV转化为RGB
        int rgb = Color.HSVToColor(new float[]{mHue, 1f, 1f});
        //饱和线性着色器
        Shader satShader = new LinearGradient(mSatValRect.left, mSatValRect.top, mSatValRect.right, mSatValRect.top,
                0xffffffff, rgb, TileMode.CLAMP);
        //组合着色器 = 明度线性着色器 + 饱和度线性着色器
        return new ComposeShader(mValShader, satShader, PorterDuff.Mode.MULTIPLY);
    }

    /**
     * 绘制右侧H选择区域
     *
     * @param canvas 画布
     */
    private void drawHuePanel(Canvas canvas) {
        final RectF rect = mHueRect;

        mBorderPaint.setColor(mBorderColor);
        canvas.drawRect(rect.left - BORDER_WIDTH,
                rect.top - BORDER_WIDTH,
                rect.right + BORDER_WIDTH,
                rect.bottom + BORDER_WIDTH,
                mBorderPaint);
        //初始化H线性着色器
        if (mHueShader == null) {
            int[] hue = new int[361];
            int count = 0;
            for (int i = hue.length - 1; i >= 0; i--, count++) {
                hue[count] = Color.HSVToColor(new float[]{i, 1f, 1f});
            }
            mHueShader = new LinearGradient(
                    rect.left,
                    rect.top,
                    rect.left,
                    rect.bottom,
                    hue,
                    null,
                    TileMode.CLAMP);
            mHuePaint.setShader(mHueShader);
        }

        canvas.drawRect(rect, mHuePaint);

        float rectHeight = 4 * mDensity / 2;
        //初始化H选择器选择条位置
        Point p = hueToPoint(mHue);

        RectF r = new RectF();
        r.left = rect.left - mRectOffset;
        r.right = rect.right + mRectOffset;
        r.top = p.y - rectHeight;
        r.bottom = p.y + rectHeight;

        //绘制选择条
        canvas.drawRoundRect(r, 2, 2, mHueTrackerPaint);
    }

    private Point hueToPoint(float hue) {
        final RectF rect = mHueRect;
        final float height = rect.height();

        Point p = new Point();
        p.y = (int) (height - (hue * height / 360f) + rect.top);
        p.x = (int) rect.left;
        return p;
    }

    private Point satValToPoint(float sat, float val) {
        final float height = mSatValRect.height();
        final float width = mSatValRect.width();

        Point p = new Point();
        p.x = (int) (sat * width + mSatValRect.left);
        p.y = (int) ((1f - val) * height + mSatValRect.top);
        return p;
    }

    private float[] pointToSatVal(float x, float y) {
        final RectF rect = mSatValRect;
        float[] result = new float[2];

        float width = rect.width();
        float height = rect.height();

        if (x < rect.left) {
            x = 0f;
        } else if (x > rect.right) {
            x = width;
        } else {
            x = x - rect.left;
        }

        if (y < rect.top) {
            y = 0f;
        } else if (y > rect.bottom) {
            y = height;
        } else {
            y = y - rect.top;
        }
        result[0] = 1.f / width * x;
        result[1] = 1.f - (1.f / height * y);
        return result;
    }

    private float pointToHue(float y) {
        final RectF rect = mHueRect;
        float height = rect.height();
        if (y < rect.top) {
            y = 0f;
        } else if (y > rect.bottom) {
            y = height;
        } else {
            y = y - rect.top;
        }
        return 360f - (y * 360f / height);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        boolean update = false;
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            switch (mLastTouchedPanel) {
                case PANEL.SAT_VAL:
                    float sat, val;
                    sat = mSat + x / 50f;
                    val = mVal - y / 50f;
                    if (sat < 0f) {
                        sat = 0f;
                    } else if (sat > 1f) {
                        sat = 1f;
                    }
                    if (val < 0f) {
                        val = 0f;
                    } else if (val > 1f) {
                        val = 1f;
                    }
                    mSat = sat;
                    mVal = val;
                    update = true;
                    break;
                case PANEL.HUE:
                    float hue = mHue - y * 10f;
                    if (hue < 0f) {
                        hue = 0f;
                    } else if (hue > 360f) {
                        hue = 360f;
                    }
                    mHue = hue;
                    update = true;
                    break;
            }
        }
        if (update) {
            if (mListener != null) {
                mListener.onColorChanged(Color.HSVToColor(new float[]{mHue, mSat, mVal}));
            }
            invalidate();
            return true;
        }
        return super.onTrackballEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean update = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartTouchPoint = new Point((int) event.getX(), (int) event.getY());
                update = moveTrackersIfNeeded(event);
                break;
            case MotionEvent.ACTION_MOVE:
                update = moveTrackersIfNeeded(event);
                break;
            case MotionEvent.ACTION_UP:
                mStartTouchPoint = null;
                update = moveTrackersIfNeeded(event);
                break;
        }
        if (update) {
            if (mListener != null) {
                mListener.onColorChanged(Color.HSVToColor(new float[]{mHue, mSat, mVal}));
            }
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean moveTrackersIfNeeded(MotionEvent event) {
        if (mStartTouchPoint == null) return false;
        boolean update = false;
        int startX = mStartTouchPoint.x;
        int startY = mStartTouchPoint.y;
        if (mHueRect.contains(startX, startY)) {
            mLastTouchedPanel = PANEL.HUE;
            mHue = pointToHue(event.getY());
            update = true;
        } else if (mSatValRect.contains(startX, startY)) {
            mLastTouchedPanel = PANEL.SAT_VAL;
            float[] result = pointToSatVal(event.getX(), event.getY());
            mSat = result[0];
            mVal = result[1];
            update = true;
        }
        return update;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawingRect = new RectF();
        mDrawingRect.left = mDrawingOffset + getPaddingLeft();
        mDrawingRect.right = w - mDrawingOffset - getPaddingRight();
        mDrawingRect.top = mDrawingOffset + getPaddingTop();
        mDrawingRect.bottom = h - mDrawingOffset - getPaddingBottom();
        //当DatePickerView的长宽改变时，重新计算SV、H矩形大小
        setUpSatValRect();
        setUpHueRect();
    }

    private void setUpSatValRect() {
        final RectF dRect = mDrawingRect;
        float panelSide = dRect.height() - BORDER_WIDTH * 2;
        float left = dRect.left + BORDER_WIDTH;
        float top = dRect.top + BORDER_WIDTH;
        float bottom = top + panelSide;
        float right = left + panelSide;
        mSatValRect = new RectF(left, top, right, bottom);
    }

    private void setUpHueRect() {
        final RectF dRect = mDrawingRect;
        float left = dRect.right - mHuePanelWidth + BORDER_WIDTH;
        float top = dRect.top + BORDER_WIDTH;
        float bottom = dRect.bottom - BORDER_WIDTH;
        float right = dRect.right - BORDER_WIDTH;
        mHueRect = new RectF(left, top, right, bottom);
    }

    /**
     * 设置颜色改变监听器
     *
     * @param listener 颜色改变监听器
     */
    public void setOnColorChangedListener(OnColorChangedListener listener) {
        mListener = listener;
    }

    /**
     * 设置边框颜色
     *
     * @param color 边框颜色
     */
    public void setBorderColor(int color) {
        mBorderColor = color;
        invalidate();
    }

    /**
     * 获取边框颜色
     *
     * @return 边框颜色
     */
    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     * 获取当前颜色
     *
     * @return 当前颜色
     */
    public int getColor() {
        return Color.HSVToColor(new float[]{mHue, mSat, mVal});
    }

    /**
     * 设置选择的颜色
     *
     * @param color 被选择的颜色
     */
    public void setColor(int color) {
        setColor(color, false);
    }

    /**
     * 设置被选择的颜色
     *
     * @param color    被选择的颜色
     * @param callback 是否触发OnColorChangedListener
     */
    public void setColor(int color, boolean callback) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        mHue = hsv[0];
        mSat = hsv[1];
        mVal = hsv[2];
        if (callback && mListener != null) {
            mListener.onColorChanged(Color.HSVToColor(new float[]{mHue, mSat, mVal}));
        }
        invalidate();
    }

    /**
     * ColorPickerView的padding
     *
     * @return padding（单位：px）
     */
    public float getDrawingOffset() {
        return mDrawingOffset;
    }

    public void setSliderTrackerColor(int color) {
        mSliderTrackerColor = color;
        mHueTrackerPaint.setColor(mSliderTrackerColor);
        invalidate();
    }

    public int getSliderTrackerColor() {
        return mSliderTrackerColor;
    }
}