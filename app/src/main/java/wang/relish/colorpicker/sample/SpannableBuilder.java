package wang.relish.colorpicker.sample;

import android.content.Context;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;


/**
 * Created by wang
 * on 2017/1/6
 */

public class SpannableBuilder {

    private SpannableStringBuilder mSource;

    private int mStartIndex = 0;

    private int mEndIndex = 0;

    private SpannableBuilder(@Nullable CharSequence source) {
        if (source == null) {
            mSource = new SpannableStringBuilder();
        } else {
            mSource = SpannableStringBuilder.valueOf(source);
        }
    }

    public static SpannableBuilder source() {
        return source(null);
    }

    public static SpannableBuilder source(@Nullable CharSequence source) {
        return new SpannableBuilder(source);
    }

    /**
     * 移动结束位置到文本末尾
     */
    public SpannableBuilder endToTextEnd() {
        return endToTextEnd(0);
    }

    /**
     * 移动结束位置到文本末尾
     *
     * @param gapEnd 与文本末尾的间隔
     */
    public SpannableBuilder endToTextEnd(@IntRange(from = 0) int gapEnd) {
        mEndIndex = mSource.length() - gapEnd;
        return this;
    }

    /**
     * 替换文本为圆形
     */
    public SpannableBuilder circle(@NonNull Context context, int gapWidth, @ColorRes int color, @IntRange(from = 0) int radius) {
        return circle(DimensionUtils.dpToPx(gapWidth), ContextCompat.getColor(context, color), DimensionUtils.dpToPx(radius));
    }

    /**
     * 替换文本为圆形
     */
    public SpannableBuilder circle(@Px int gapWidth, @ColorInt int color, @Px @IntRange(from = 0) int radius) {
        return span(new CircleSpan(gapWidth, color, radius));
    }

    /**
     * 替换文本为矩形
     */
    public SpannableBuilder appendRect(@Px int gapWidth, @ColorInt int color, @Px @IntRange(from = 0) int padding, @Px @IntRange(from = 0) int width) {
        return append(" ", new RectSpan(gapWidth, color, padding, width));
    }



    public SpannableBuilder append(CharSequence text) {
        mSource.append(text);
        return this;
    }


    public SpannableBuilder append(CharSequence text, @NonNull Object span) {
        mStartIndex = mSource.length();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSource.append(text, span, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            mSource.append(text);
            span(span);
        }
        mEndIndex = mSource.length();
        return this;
    }

    public SpannableBuilder append(CharSequence text, int start, int end) {
        mSource.append(text, start, end);
        return this;
    }

    public SpannableBuilder append(CharSequence text, int start, int end, @NonNull Object span) {
        mStartIndex = mSource.length();
        mSource.append(text, start, end);
        mEndIndex = mSource.length();
        return span(span);
    }

    public SpannableBuilder span(@NonNull Object span) {
        mSource.setSpan(span, mStartIndex, mEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public int length() {
        return mSource.length();
    }

    public void into(TextView textView) {
        if (textView != null) {
            textView.setText(mSource);
        }
    }

    public SpannableStringBuilder get() {
        return mSource;
    }
    
}
