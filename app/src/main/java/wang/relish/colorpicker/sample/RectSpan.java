package wang.relish.colorpicker.sample;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.style.ReplacementSpan;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

/**
 * Created on 2020/1/9
 * Author: bigwang
 * Description:
 */
public class RectSpan extends ReplacementSpan {

    private static final int STANDARD_PADDING = 4;
    private static final int STANDARD_WIDTH = 3;
    private static final int STANDARD_GAP_WIDTH = 6;

    @Px
    private final int mGapWidth;
    @Px
    private int mWidth;
    @Px
    private final int mPadding;
    @ColorInt
    private final int mColor;
    private final boolean mWantColor;

    private Path bulletPath;

    /**
     * Creates a {@link RectSpan} with the default values.
     */
    public RectSpan() {
        this(STANDARD_GAP_WIDTH, -1, false, STANDARD_PADDING, STANDARD_WIDTH);
    }

    /**
     * Creates a {@link RectSpan} based on a gap width
     *
     * @param gapWidth the distance, in pixels, between the bullet point and the paragraph.
     */
    public RectSpan(int gapWidth) {
        this(gapWidth, -1, false, STANDARD_PADDING, STANDARD_WIDTH);
    }

    /**
     * Creates a {@link RectSpan} based on a gap width and a color integer.
     *
     * @param gapWidth the distance, in pixels, between the bullet point and the paragraph.
     * @param color    the bullet point color, as a color integer
     * @see Resources#getColor(int, Resources.Theme)
     */
    public RectSpan(int gapWidth, @ColorInt int color) {
        this(gapWidth, color, true, STANDARD_PADDING, STANDARD_WIDTH);
    }

    public RectSpan(int gapWidth, @ColorInt int color, @IntRange(from = 0) int padding) {
        this(gapWidth, color, true, padding, STANDARD_WIDTH);
    }

    public RectSpan(int gapWidth, @ColorInt int color, @IntRange(from = 0) int padding, @IntRange(from = 0) int width) {
        this(gapWidth, color, true, padding, width);
    }

    private RectSpan(int gapWidth, @ColorInt int color, boolean wantColor, @IntRange(from = 0) int padding, @IntRange(from = 0) int width) {
        mGapWidth = gapWidth;
        mColor = color;
        mWantColor = wantColor && color != Color.TRANSPARENT;
        mPadding = padding;
        mWidth = width;
    }


    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return mGapWidth * 2 + mWidth;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        if (mWidth == 0) {
            return;
        }

        Paint.Style style = paint.getStyle();
        int oldcolor = 0;

        if (mWantColor) {
            oldcolor = paint.getColor();
            paint.setColor(mColor);
        }

        paint.setStyle(Paint.Style.FILL);

        final float height = bottom - top - mPadding * 2;
        final float yPoint = top + mPadding;
        final float xPoint = x + mGapWidth;

        if (canvas.isHardwareAccelerated()) {
            if (bulletPath == null) {
                bulletPath = new Path();
                bulletPath.addRect(0f, 0f, (float) mWidth, height, Path.Direction.CW);
            }

            canvas.save();
            canvas.translate(xPoint, yPoint);
            canvas.drawPath(bulletPath, paint);
            canvas.restore();
        } else {
            canvas.drawRect(xPoint, yPoint, xPoint + mWidth, yPoint + height, paint);
        }

        if (mWantColor) {
            paint.setColor(oldcolor);
        }

        paint.setStyle(style);
    }
}
