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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 取色器
 *
 * @author Relish Wang
 * @since 2017/08/02
 */
public class ColorPickerView extends View {

    private final static int PANEL_SAT_VAL = 0;
    private final static int PANEL_HUE = 1;

    /**
     * 用于显示颜色的面板的边框粗细（单位：px）
     */
    private final static float BORDER_WIDTH_PX = 1;

    /**
     * 色调面板的宽度（单位：dp）
     */
    private float HUE_PANEL_WIDTH = 30f;
    /**
     * 不同颜色面板间的间距（单位：dp）
     */
    private float PANEL_SPACING = 10f;
    /**
     * 颜色选择面板的半径（单位：dp）
     */
    private float PALETTE_CIRCLE_TRACKER_RADIUS = 5f;
    /**
     * 色调或透明值面板的padding（单位：dp）
     */
    private float RECTANGLE_TRACKER_OFFSET = 2f;

    private float mDensity = 1f;

    private OnColorChangedListener mListener;

    private Paint mSatValPaint;
    private Paint mSatValTrackerPaint;

    private Paint mHuePaint;
    private Paint mHueTrackerPaint;

    private Paint mBorderPaint;

    private Shader mValShader;
    private Shader mSatShader;
    private Shader mHueShader;

    private float mHue = 360f;
    private float mSat = 0f;
    private float mVal = 0f;

    private int mSliderTrackerColor = 0xff1c1c1c;
    private int mBorderColor = 0xff6E6E6E;

    /**
     * 记录上一次被点击的颜色板
     */
    private int mLastTouchedPanel = PANEL_SAT_VAL;

    /**
     * 边距
     */
    private float mDrawingOffset;

    private RectF mDrawingRect;

    private RectF mSatValRect;
    private RectF mHueRect;

    private Point mStartTouchPoint = null;

    public interface OnColorChangedListener {
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
        mDensity = getContext().getResources().getDisplayMetrics().density;//获取屏幕密度并初始化三区域各项参数
        PALETTE_CIRCLE_TRACKER_RADIUS *= mDensity;
        RECTANGLE_TRACKER_OFFSET *= mDensity;
        HUE_PANEL_WIDTH *= mDensity;
        PANEL_SPACING = PANEL_SPACING * mDensity;

        mDrawingOffset = calculateRequiredOffset();//计算所需位移

        initPaintTools();//初始化绘制三区域的画笔

        //Needed for receiving trackball motion events. 设置焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
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

    private float calculateRequiredOffset() {
        float offset = Math.max(PALETTE_CIRCLE_TRACKER_RADIUS, RECTANGLE_TRACKER_OFFSET);
        offset = Math.max(offset, BORDER_WIDTH_PX * mDensity);

        return offset * 1.5f;
    }

    private int[] buildHueColorArray() {

        int[] hue = new int[361];

        int count = 0;
        for (int i = hue.length - 1; i >= 0; i--, count++) {
            hue[count] = Color.HSVToColor(new float[]{i, 1f, 1f});
        }

        return hue;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mDrawingRect.width() <= 0 || mDrawingRect.height() <= 0) return;

        drawSatValPanel(canvas);//绘制饱和度选择区域
        drawHuePanel(canvas);//绘制右侧色相选择区域
    }

    /**
     * 绘制饱和度选择区域
     *
     * @param canvas
     */
    private void drawSatValPanel(Canvas canvas) {

        final RectF rect = mSatValRect;

        mBorderPaint.setColor(mBorderColor);
        canvas.drawRect(mDrawingRect.left, mDrawingRect.top, rect.right + BORDER_WIDTH_PX, rect.bottom + BORDER_WIDTH_PX, mBorderPaint);
        //明度线性渲染器
        if (mValShader == null) {
            mValShader = new LinearGradient(rect.left, rect.top, rect.left, rect.bottom,
                    0xffffffff, 0xff000000, TileMode.CLAMP);
        }
        //HSV转化为RGB
        int rgb = Color.HSVToColor(new float[]{mHue, 1f, 1f});
        //饱和线性渲染器
        mSatShader = new LinearGradient(rect.left, rect.top, rect.right, rect.top,
                0xffffffff, rgb, TileMode.CLAMP);
        //组合渲染 = 明度线性渲染器 + 饱和线性渲染器
        ComposeShader mShader = new ComposeShader(mValShader, mSatShader, PorterDuff.Mode.MULTIPLY);
        mSatValPaint.setShader(mShader);

        canvas.drawRect(rect, mSatValPaint);
        //初始化选择圆块的位置
        Point p = satValToPoint(mSat, mVal);
        //绘制黑色内圆
        mSatValTrackerPaint.setColor(0xff000000);
        canvas.drawCircle(p.x, p.y, PALETTE_CIRCLE_TRACKER_RADIUS - 1f * mDensity, mSatValTrackerPaint);
        //绘制外圆
        mSatValTrackerPaint.setColor(0xffdddddd);
        canvas.drawCircle(p.x, p.y, PALETTE_CIRCLE_TRACKER_RADIUS, mSatValTrackerPaint);

    }

    /**
     * 绘制右侧色相选择区域
     *
     * @param canvas 画布
     */
    private void drawHuePanel(Canvas canvas) {

        final RectF rect = mHueRect;

        mBorderPaint.setColor(mBorderColor);
        canvas.drawRect(rect.left - BORDER_WIDTH_PX,
                rect.top - BORDER_WIDTH_PX,
                rect.right + BORDER_WIDTH_PX,
                rect.bottom + BORDER_WIDTH_PX,
                mBorderPaint);
        //初始化色相线性渲染器
        if (mHueShader == null) {
            mHueShader = new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, buildHueColorArray(), null, TileMode.CLAMP);
            mHuePaint.setShader(mHueShader);
        }

        canvas.drawRect(rect, mHuePaint);

        float rectHeight = 4 * mDensity / 2;
        //初始化色相选择器选择条位置
        Point p = hueToPoint(mHue);

        RectF r = new RectF();
        r.left = rect.left - RECTANGLE_TRACKER_OFFSET;
        r.right = rect.right + RECTANGLE_TRACKER_OFFSET;
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
        final RectF rect = mSatValRect;
        final float height = rect.height();
        final float width = rect.width();

        Point p = new Point();
        p.x = (int) (sat * width + rect.left);
        p.y = (int) ((1f - val) * height + rect.top);
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
                case PANEL_SAT_VAL:
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
                case PANEL_HUE:
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
            mLastTouchedPanel = PANEL_HUE;
            mHue = pointToHue(event.getY());
            update = true;
        } else if (mSatValRect.contains(startX, startY)) {
            mLastTouchedPanel = PANEL_SAT_VAL;
            float[] result = pointToSatVal(event.getX(), event.getY());
            mSat = result[0];
            mVal = result[1];
            update = true;
        }
        return update;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthAllowed = MeasureSpec.getSize(widthMeasureSpec);
        int heightAllowed = MeasureSpec.getSize(heightMeasureSpec);
        widthAllowed = chooseWidth(widthMode, widthAllowed);
        heightAllowed = chooseHeight(heightMode, heightAllowed);
        height = (int) (widthAllowed - PANEL_SPACING - HUE_PANEL_WIDTH);
        //当根据宽度按计算出来宽度大于可允许的高度时
        if (height > heightAllowed || getTag().equals("landscape")) {
            height = heightAllowed;
            width = (int) (height + PANEL_SPACING + HUE_PANEL_WIDTH);
        } else {
            width = widthAllowed;
        }
        setMeasuredDimension(width, height);
    }

    private int chooseWidth(int mode, int size) {
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            return size;
        } else { // (mode == MeasureSpec.UNSPECIFIED)
            return getPrefferedWidth();
        }
    }

    private int chooseHeight(int mode, int size) {
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            return size;
        } else { // (mode == MeasureSpec.UNSPECIFIED)
            return getPrefferedHeight();
        }
    }

    private int getPrefferedWidth() {
        return (int) (getPrefferedHeight() + HUE_PANEL_WIDTH + PANEL_SPACING);
    }

    private int getPrefferedHeight() {
        return (int) (200 * mDensity);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mDrawingRect = new RectF();
        mDrawingRect.left = mDrawingOffset + getPaddingLeft();
        mDrawingRect.right = w - mDrawingOffset - getPaddingRight();
        mDrawingRect.top = mDrawingOffset + getPaddingTop();
        mDrawingRect.bottom = h - mDrawingOffset - getPaddingBottom();

        setUpSatValRect();
        setUpHueRect();
//        setUpAlphaRect();
    }

    private void setUpSatValRect() {
        final RectF dRect = mDrawingRect;
        float panelSide = dRect.height() - BORDER_WIDTH_PX * 2;
        float left = dRect.left + BORDER_WIDTH_PX;
        float top = dRect.top + BORDER_WIDTH_PX;
        float bottom = top + panelSide;
        float right = left + panelSide;
        mSatValRect = new RectF(left, top, right, bottom);
    }

    private void setUpHueRect() {
        final RectF dRect = mDrawingRect;
        float left = dRect.right - HUE_PANEL_WIDTH + BORDER_WIDTH_PX;
        float top = dRect.top + BORDER_WIDTH_PX;
        float bottom = dRect.bottom - BORDER_WIDTH_PX;
        float right = dRect.right - BORDER_WIDTH_PX;
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
     * color picker的padding
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