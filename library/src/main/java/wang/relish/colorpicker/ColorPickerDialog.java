package wang.relish.colorpicker;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * @author Relish Wang
 * @since 2017/3/22
 */
public class ColorPickerDialog extends DialogFragment implements ColorPickerView.OnColorSelectedChangedListener {

    private static final String RED = "red";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";
    private int mRed, mGreen, mBlue;

    private View vColor;
    private ColorPickerView colorPicker;

    public static ColorPickerDialog getInstance(int r, int g, int b) {
        ColorPickerDialog dialog = new ColorPickerDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(RED, r);
        bundle.putInt(GREEN, g);
        bundle.putInt(BLUE, b);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        dialog.setContentView(R.layout.dialog_color_picker);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消

        Bundle bundle = getArguments();
        mRed = bundle.getInt(RED);
        mGreen = bundle.getInt(GREEN);
        mBlue = bundle.getInt(BLUE);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_picker, container, false);
        v.setBackgroundColor(Color.TRANSPARENT);
        initViews(v);
        return v;
    }

    private void initViews(View v) {
        vColor = v.findViewById(R.id.view_color);
        vColor.setBackgroundColor(Color.rgb(mRed, mGreen, mBlue));
        colorPicker = (ColorPickerView) v.findViewById(R.id.picker);
        colorPicker.setOnColorSelectedChangedListenner(this);
    }

    @Override
    public void onColorChanged(ColorPickerView colorPicker, int r, int g, int b) {
        mRed = r;
        mGreen = g;
        mBlue = b;
        vColor.setBackgroundColor(Color.rgb(r, g, b));
        if (mListener != null) {
            mListener.onColorSelectCompleted(this, r, g, b);
            dismiss();
        }
    }

    @Override
    public void onMoveColor(ColorPickerView colorPicker, int r, int g, int b) {
        mRed = r;
        mGreen = g;
        mBlue = b;
        vColor.setBackgroundColor(Color.rgb(r, g, b));
    }

    private OnColorSelectCompletedListener mListener;

    public void setOnColorSelectCompletedListener(OnColorSelectCompletedListener mListener) {
        this.mListener = mListener;
    }

    public interface OnColorSelectCompletedListener {
        void onColorSelectCompleted(ColorPickerDialog dialog, int r, int g, int b);
    }
}
