package wang.relish.colorpicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * 取色器对话框
 *
 * @author Relish Wang
 * @since 2017/08/02
 */
public class ColorPickerDialog extends Dialog implements ColorPickerView.OnColorChangedListener,
        View.OnClickListener {

    private ColorPickerView mColorPicker;

    private View mOldColor;
    private View mNewColor;

    private EditText mHexVal;
    private boolean mHexValueEnabled = false;
    private ColorStateList mHexDefaultTextColor;

    private OnColorPickedListener mListener;

    public interface OnColorPickedListener {
        void onColorPicked(int color);
    }

    private ColorPickerDialog(Context context, int initialColor) {
        super(context);
        init(initialColor);
    }

    /**
     * 设置初始颜色
     *
     * @param color 初始颜色
     */
    private void init(int color) {
        Window window = getWindow();
        if (window != null) {
            window.setFormat(PixelFormat.RGBA_8888);
        }
        setUp(color);
    }

    private void setUp(int color) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.dialog_color_picker, null);
        setContentView(layout);
        setTitle("取色器");

        mColorPicker = layout.findViewById(R.id.color_picker_view);
        mOldColor = layout.findViewById(R.id.old_color_panel);
        mNewColor = layout.findViewById(R.id.new_color_panel);

        mHexVal = layout.findViewById(R.id.hex_val);
        mHexVal.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mHexDefaultTextColor = mHexVal.getTextColors();

        mHexVal.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    String hexVal = mHexVal.getText().toString();
                    if (hexVal.length() > 5 || hexVal.length() < 10) {
                        try {
                            int c = Utils.convertToColorInt(hexVal);
                            mColorPicker.setColor(c, true);
                            mHexVal.setTextColor(mHexDefaultTextColor);
                        } catch (IllegalArgumentException e) {
                            mHexVal.setTextColor(Color.RED);
                        }
                    } else {
                        mHexVal.setTextColor(Color.RED);
                    }
                    return true;
                }
                return false;
            }
        });
        ((LinearLayout) mOldColor.getParent()).setPadding(
                Math.round(mColorPicker.getDrawingOffset()),
                0,
                Math.round(mColorPicker.getDrawingOffset()),
                0
        );
        mOldColor.setOnClickListener(this);
        mNewColor.setOnClickListener(this);
        mColorPicker.setOnColorChangedListener(this);
        mOldColor.setBackgroundColor(color);
        mColorPicker.setColor(color, true);
    }

    @Override
    public void onColorChanged(int color) {
        mNewColor.setBackgroundColor(color);
        if (mHexValueEnabled) updateHexValue(color);
    }

    public void setHexValueEnabled(boolean enable) {
        mHexValueEnabled = enable;
        if (enable) {
            mHexVal.setVisibility(View.VISIBLE);
            updateHexLengthFilter();
            updateHexValue(getColor());
        } else
            mHexVal.setVisibility(View.GONE);
    }

    public boolean getHexValueEnabled() {
        return mHexValueEnabled;
    }

    private void updateHexLengthFilter() {
        mHexVal.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
    }

    private void updateHexValue(int color) {
        mHexVal.setText(Utils.convertToRGB(color).toUpperCase(Locale.getDefault()));
        mHexVal.setTextColor(mHexDefaultTextColor);
    }

    /**
     * 设置颜色改变的监听器
     *
     * @param listener 颜色改变监听器
     */
    public void setOnColorChangedListener(OnColorPickedListener listener) {
        mListener = listener;
    }

    public int getColor() {
        return mColorPicker.getColor();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_color_panel) {
            if (mListener != null) {
                mNewColor.getDrawingCacheBackgroundColor();
                mListener.onColorPicked(((ColorDrawable) mNewColor.getBackground()).getColor());
            }
        }
        dismiss();
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt("old_color", ((ColorDrawable) mOldColor.getBackground()).getColor());
        state.putInt("new_color", ((ColorDrawable) mNewColor.getBackground()).getColor());
        return state;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mOldColor.setBackgroundColor(savedInstanceState.getInt("old_color"));
        mColorPicker.setColor(savedInstanceState.getInt("new_color"), true);
    }

    public static class Builder {
        private Context context;
        private int initColor;
        private boolean hexValueEnabled;
        private OnColorPickedListener listener;

        public Builder(Context context, int initColor) {
            this.context = context;
            this.initColor = initColor;
            this.hexValueEnabled = false;
        }

        public Builder setHexValueEnabled(boolean hexValueEnabled) {
            this.hexValueEnabled = hexValueEnabled;
            return this;
        }

        public Builder setOnColorChangedListener(OnColorPickedListener listener) {
            this.listener = listener;
            return this;
        }

        public ColorPickerDialog build() {
            ColorPickerDialog dialog = new ColorPickerDialog(context, initColor);
            dialog.setHexValueEnabled(hexValueEnabled);
            dialog.setOnColorChangedListener(listener);
            return dialog;
        }
    }
}
