package wang.relish.colorpicker.sample;

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
        ColorPickerDialog.OnColorPickedListener, CompoundButton.OnCheckedChangeListener {


    private View mViewColor;
    private SwitchCompat mStHexEnable;

    /**
     * 选择的颜色
     */
    private int mColor = 0xFFFFFF;
    /**
     * 是否显示颜色数值（16进制）
     */
    private boolean mHexValueEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStHexEnable = (SwitchCompat) findViewById(R.id.st_hex_enable);

        mStHexEnable.setChecked(mHexValueEnable);

        mStHexEnable.setOnCheckedChangeListener(this);

        mViewColor = findViewById(R.id.view_color);
        mViewColor.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_color:
                new ColorPickerDialog.Builder(MainActivity.this, mColor)
                        .setHexValueEnabled(mHexValueEnable)//是否显示颜色值
                        .setOnColorChangedListener(MainActivity.this)//设置颜色改变监听器
                        .build()
                        .show();//展示
                break;
        }
    }

    @Override
    public void onColorPicked(int color) {
        mColor = color;
        mViewColor.setBackgroundColor(mColor);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mHexValueEnable = b;
    }
}
