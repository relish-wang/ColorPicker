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
public class CircleSpan extends ReplacementSpan {

    private static final int STANDARD_RADIUS = 4;
    private static final int STANDARD_GAP_WIDTH = 2;
    private static final int STANDARD_COLOR = Color.BLACK;

    @Px
    private final int mGapWidth;
    @Px
    private final int mRadius;
    @ColorInt
    private final int mColor;
    private final boolean mWantColor;

    private Path bulletPath;

    /**
     * Creates a {@link CircleSpan} with the default values.
     */
    public CircleSpan() {
        this(STANDARD_GAP_WIDTH, STANDARD_COLOR, false, STANDARD_RADIUS);
    }

    /**
     * Creates a {@link CircleSpan} based on a gap width
     *
     * @param gapWidth the distance, in pixels, between the bullet point and the paragraph.
     */
    public CircleSpan(int gapWidth) {
        this(gapWidth, STANDARD_COLOR, false, STANDARD_RADIUS);
    }

    /**
     * Creates a {@link CircleSpan} based on a gap width and a color integer.
     *
     * @param gapWidth the distance, in pixels, between the bullet point and the paragraph.
     * @param color    the bullet point color, as a color integer
     * @see Resources#getColor(int, Resources.Theme)
     */
    public CircleSpan(int gapWidth, @ColorInt int color) {
        this(gapWidth, color, true, STANDARD_RADIUS);
    }

    /**
     * Creates a {@link CircleSpan} based on a gap width and a color integer.
     *
     * @param gapWidth     the distance, in pixels, between the bullet point and the paragraph.
     * @param color        the bullet point color, as a color integer.
     * @param radius the radius of the bullet point, in pixels.
     * @see Resources#getColor(int, Resources.Theme)
     */
    public CircleSpan(int gapWidth, @ColorInt int color, @IntRange(from = 0) int radius) {
        this(gapWidth, color, true, radius);
    }

    private CircleSpan(int gapWidth, @ColorInt int color, boolean wantColor, @IntRange(from = 0) int radius) {
        mGapWidth = gapWidth;
        mRadius = radius;
        mColor = color;
        mWantColor = wantColor && color != Color.TRANSPARENT;
    }


    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return (mRadius + mGapWidth) * 2;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        if (mRadius == 0){
            return;
        }

        Paint.Style style = paint.getStyle();
        int oldcolor = 0;

        if (mWantColor) {
            oldcolor = paint.getColor();
            paint.setColor(mColor);
        }

        paint.setStyle(Paint.Style.FILL);


        final float yPoint = (top + bottom) / 2f;
        final float xPoint = x + mGapWidth + mRadius;

        if (canvas.isHardwareAccelerated()) {
            if (bulletPath == null) {
                bulletPath = new Path();
                bulletPath.addCircle(0.0f, 0.0f, mRadius, Path.Direction.CW);
            }

            canvas.save();
            canvas.translate(xPoint, yPoint);
            canvas.drawPath(bulletPath, paint);
            canvas.restore();
        } else {
            canvas.drawCircle(xPoint, yPoint, mRadius, paint);
        }

        if (mWantColor) {
            paint.setColor(oldcolor);
        }

        paint.setStyle(style);
    }
}
