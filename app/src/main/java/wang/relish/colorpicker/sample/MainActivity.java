package wang.relish.colorpicker.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;

import wang.relish.colorpicker.ColorPickerDialog;


/**
 * @author Relish Wang
 * @since 2017/7/31
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ColorPickerDialog.OnColorChangedListener, CompoundButton.OnCheckedChangeListener {


    private View mViewColor;
    private SwitchCompat mStHexEnable, mStAlphaEnable;

    /**
     * 选择的颜色
     */
    private int mColor = Color.parseColor("#FFFFFF");
    /**
     * 是否显示颜色数值（16进制）
     */
    private boolean mHexValueEnable = true;
    /**
     * 是否启用透明度
     */
    private boolean mAlphaSliderVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStHexEnable = (SwitchCompat) findViewById(R.id.st_hex_enable);
        mStAlphaEnable = (SwitchCompat) findViewById(R.id.st_alpha_enable);

        mStHexEnable.setChecked(mHexValueEnable);
        mStAlphaEnable.setChecked(mAlphaSliderVisible);

        mStHexEnable.setOnCheckedChangeListener(this);
        mStAlphaEnable.setOnCheckedChangeListener(this);

        mViewColor = findViewById(R.id.view_color);
        mViewColor.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_color:
                ColorPickerDialog dialog = new ColorPickerDialog(MainActivity.this, mColor);
                dialog.setAlphaSliderVisible(mAlphaSliderVisible);
                dialog.setHexValueEnabled(mHexValueEnable);
                dialog.setOnColorChangedListener(MainActivity.this);
                dialog.show();
                break;
        }
    }

    @Override
    public void onColorChanged(int color) {
        mColor = color;
        mViewColor.setBackgroundColor(mColor);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == mStAlphaEnable) {
            mAlphaSliderVisible = b;
        } else if (compoundButton == mStHexEnable) {
            mHexValueEnable = b;
        }
    }
}
