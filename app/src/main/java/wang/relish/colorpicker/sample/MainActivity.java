package wang.relish.colorpicker.sample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;

import wang.relish.colorpicker.ColorPickerDialog;


/**
 * @author Relish Wang
 * @since 2017/7/31
 */
public class MainActivity extends AppCompatActivity {


    private View mViewColor;

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

        SwitchCompat mStHexEnable = (SwitchCompat) findViewById(R.id.st_hex_enable);

        mStHexEnable.setChecked(mHexValueEnable);

        mStHexEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mHexValueEnable = b;
            }
        });

        mViewColor = findViewById(R.id.view_color);
        mViewColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog.Builder(MainActivity.this, mColor)
                        .setHexValueEnabled(mHexValueEnable)//是否显示颜色值
                        //设置点击应用颜色的事件监听
                        .setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
                            @Override
                            public void onColorPicked(int color) {
                                mColor = color;
                                mViewColor.setBackgroundColor(mColor);
                            }
                        })
                        .build()
                        .show();//展示
            }
        });
    }
}
