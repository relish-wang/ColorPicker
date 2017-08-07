package wang.relish.colorpicker;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * 为凸显透明度和灰度的底层Drawable
 *
 * @author Relish Wang
 * @since 2017/08/02
 */
public class AlphaPatternDrawable extends Drawable {

    private int mRectangleSize = 10;

    private Paint mPaint = new Paint();
    private Paint mPaintWhite = new Paint();
    private Paint mPaintGray = new Paint();

    private int numRectanglesHorizontal;
    private int numRectanglesVertical;

    /**
     * 缓存
     */
    private Bitmap mBitmap;

    public AlphaPatternDrawable(int rectangleSize) {
        mRectangleSize = rectangleSize;
        mPaintWhite.setColor(0xffffffff);
        mPaintGray.setColor(0xffcbcbcb);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawBitmap(mBitmap, null, getBounds(), mPaint);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    @Override
    public void setAlpha(int alpha) {
        throw new UnsupportedOperationException("不允许设置Alpha值！");
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        throw new UnsupportedOperationException("不允许设置ColorFilter！");
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int height = bounds.height();
        int width = bounds.width();
        numRectanglesHorizontal = (int) Math.ceil((width / mRectangleSize));
        numRectanglesVertical = (int) Math.ceil(height / mRectangleSize);
        generatePatternBitmap();
    }

    /**
     * 生成一个用于绘制的期望大小的长方形的Bitmap，
     * 并缓存起来，因此不需要每次都调用draw方法
     */
    private void generatePatternBitmap() {
        if (getBounds().width() <= 0 || getBounds().height() <= 0) return;

        mBitmap = Bitmap.createBitmap(getBounds().width(), getBounds().height(), Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);

        Rect r = new Rect();
        boolean verticalStartWhite = true;
        for (int i = 0; i <= numRectanglesVertical; i++) {
            boolean isWhite = verticalStartWhite;
            for (int j = 0; j <= numRectanglesHorizontal; j++) {

                r.top = i * mRectangleSize;
                r.left = j * mRectangleSize;
                r.bottom = r.top + mRectangleSize;
                r.right = r.left + mRectangleSize;

                canvas.drawRect(r, isWhite ? mPaintWhite : mPaintGray);
                isWhite = !isWhite;
            }
            verticalStartWhite = !verticalStartWhite;
        }
    }
}